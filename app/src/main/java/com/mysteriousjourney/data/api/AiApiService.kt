package com.mysteriousjourney.data.api

import com.mysteriousjourney.data.model.Message
import com.mysteriousjourney.data.settings.AiModelsConfig
import com.mysteriousjourney.data.settings.CustomModelConfig
import com.mysteriousjourney.data.settings.SettingsRepository
import com.mysteriousjourney.domain.model.GameConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * AI API 服务
 * 调用用户在应用内配置的 OpenAI 兼容端点（Chat Completions，支持 SSE 流式输出）。
 * API 地址、模型 ID 和 Key 全部由用户在设置页填写，持久化于 DataStore，
 * 代码中不保存任何密钥。
 */
class AiApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(360, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    private val jsonMediaType = "application/json".toMediaType()

    private val settingsRepository = SettingsRepository()

    // 当前使用的模型配置
    private var currentConfig: CustomModelConfig? = null

    /**
     * 从 DataStore 加载启用的模型配置
     */
    suspend fun loadConfig() {
        try {
            currentConfig = resolveActiveModel(settingsRepository.getConfig())
        } catch (e: Exception) {
            println("加载 AI 配置失败: ${e.message}")
        }
    }

    fun setModelConfig(config: CustomModelConfig) {
        currentConfig = config
    }

    fun getCurrentModel(): CustomModelConfig? = currentConfig

    /**
     * 解析启用模型：优先 activeModelType 指定的模型，否则取第一个有效配置
     */
    fun resolveActiveModel(config: AiModelsConfig): CustomModelConfig? {
        val activeId = config.activeModelType.removePrefix("custom_")
        return config.customModels.find { it.id == activeId }
            ?: config.customModels.firstOrNull { it.isValid() }
    }

    /**
     * 发送游戏消息到 AI（流式版本）。
     *
     * 以 [Flow] 按到达顺序输出增量文本。相较回调式接口，这样做有两个关键收益：
     *  1. 消费方在同一个协程里顺序消费，不会出现乱序或迟到的中间帧覆盖最终结果；
     *  2. 协程被取消时 [awaitClose] 会取消底层 OkHttp Call，
     *     阻塞中的 socket 读取能立刻抛异常退出，不会泄漏连接。
     */
    fun streamGameMessage(
        systemPrompt: String,
        chatHistory: List<Message>,
        userInput: String
    ): Flow<String> = channelFlow {
        val config = currentConfig
        if (config == null || !config.isValid()) {
            throw IllegalStateException("尚未配置 AI 模型，请在「设置」中填写 API 地址、模型 ID 和 Key")
        }

        val requestBody = JSONObject().apply {
            put("model", config.modelId)
            put("messages", buildMessages(systemPrompt, chatHistory, userInput))
            put("temperature", 0.8)
            put("max_tokens", 8192)
            put("stream", true)
            put("top_p", 0.95)
            put("frequency_penalty", 0.2)
            put("presence_penalty", 0.2)
        }.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(config.apiUrl)
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                close(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    response.use { resp ->
                        if (!resp.isSuccessful) {
                            val detail = resp.body?.string() ?: "无错误详情"
                            close(IllegalStateException("API调用失败: ${resp.code} ${resp.message}, 详情: $detail"))
                            return@use
                        }

                        val source = resp.body?.source()
                            ?: throw IllegalStateException("API 响应体为空")

                        // SSE：逐行读取 data: 事件，遇到 [DONE] 立即结束
                        // （必须真正 break 掉读取循环，否则会一直阻塞到服务端关闭连接）
                        while (true) {
                            val line = source.readUtf8Line() ?: break
                            if (!line.startsWith("data:")) continue
                            val data = line.removePrefix("data:").trim()
                            if (data == "[DONE]") break
                            extractDeltaContent(data)?.let { trySend(it) }
                        }
                    }
                    close()
                } catch (e: Throwable) {
                    close(e)
                }
            }
        })

        awaitClose { call.cancel() }
    }.flowOn(Dispatchers.IO)

    /**
     * 从单条 SSE data 载荷中取出增量文本，格式不符合预期时返回 null 并跳过。
     */
    private fun extractDeltaContent(data: String): String? {
        return try {
            val choices = JSONObject(data).optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null
            val delta = choices.getJSONObject(0).optJSONObject("delta") ?: return null
            delta.optString("content", "").takeIf { it.isNotEmpty() && it != "null" }
        } catch (e: Exception) {
            // 个别分片解析失败不影响整体，静默跳过
            null
        }
    }

    /**
     * 测试一组模型配置是否可用（设置页“测试连接”使用）
     */
    suspend fun testConnection(apiUrl: String, modelId: String, apiKey: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val config = CustomModelConfig(apiUrl = apiUrl, modelId = modelId, apiKey = apiKey)
                val messages = JSONArray().put(
                    JSONObject().put("role", "user").put("content", "你好")
                )
                callChatCompletion(config, messages, maxTokens = 16)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * 构建消息数组
     */
    private fun buildMessages(
        systemPrompt: String,
        chatHistory: List<Message>,
        userInput: String
    ): JSONArray {
        val messages = JSONArray()
        val compressedHistory = compressChatHistory(chatHistory)

        // 旧对话摘要是以 role="system" 单独插进消息数组的，
        // 但部分 OpenAI 兼容端点只接受首条 system 消息，中途插入会直接报错。
        // 这里改为并进主 system prompt，兼容性更好，语义也没有损失。
        val historySummary = compressedHistory.firstOrNull { it.role == Message.ROLE_SYSTEM }
        val basePrompt = if (systemPrompt.isNotEmpty()) systemPrompt else GameConfig.SYSTEM_PROMPT
        val effectivePrompt = if (historySummary != null) {
            "$basePrompt\n\n${historySummary.content}"
        } else {
            basePrompt
        }

        messages.put(
            JSONObject().apply {
                put("role", "system")
                put("content", effectivePrompt)
            }
        )

        for (message in compressedHistory) {
            // 摘要已经并进 system prompt，这里跳过，避免在消息数组中间出现第二条 system
            if (message.role == Message.ROLE_SYSTEM) continue
            messages.put(
                JSONObject().apply {
                    put("role", message.role)
                    put("content", message.content)
                }
            )
        }

        // 本轮玩家输入尚未写入 chatHistory，必须作为最后一条 user 消息下发
        messages.put(
            JSONObject().apply {
                put("role", "user")
                put("content", userInput)
            }
        )

        return messages
    }

    /**
     * 压缩聊天历史
     */
    private fun compressChatHistory(chatHistory: List<Message>): List<Message> {
        if (chatHistory.size <= 10) return chatHistory

        val totalChars = chatHistory.sumOf { it.content.length }
        if (totalChars > 8000) {
            val recentMessages = chatHistory.takeLast(6)
            val olderMessages = chatHistory.dropLast(6)
            val summary = summarizeOlderMessages(olderMessages)

            val compressed = mutableListOf<Message>()
            if (summary.isNotEmpty()) {
                compressed.add(Message(
                    role = "system",
                    content = "【之前的故事摘要】\n$summary"
                ))
            }
            compressed.addAll(recentMessages)
            return compressed
        }
        return chatHistory.takeLast(12)
    }

    /**
     * 总结旧消息
     */
    private fun summarizeOlderMessages(messages: List<Message>): String {
        if (messages.isEmpty()) return ""
        val summary = StringBuilder()
        summary.append("玩家经历了以下事件：\n")
        messages.forEach { message ->
            if (message.role == "assistant") {
                val locationMatch = Regex("\\{location:([^}]+)\\}").find(message.content)
                if (locationMatch != null) {
                    summary.append("- 到达了${locationMatch.groupValues[1]}\n")
                }
                val content = message.content
                    .replace(Regex("\\{[^}]+\\}"), "")
                    .trim()
                if (content.length > 50) {
                    val snippet = content.take(100).replace("\n", " ")
                    summary.append("- $snippet...\n")
                }
            }
        }
        return summary.toString()
    }

    /**
     * OpenAI 兼容 API 非流式调用（用于连接测试等短请求）
     */
    private suspend fun callChatCompletion(
        config: CustomModelConfig,
        messages: JSONArray,
        maxTokens: Int
    ): String = suspendCancellableCoroutine { continuation ->
        val requestBody = JSONObject().apply {
            put("model", config.modelId)
            put("messages", messages)
            put("max_tokens", maxTokens)
        }.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(config.apiUrl)
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        // 协程被取消时同步取消请求，避免连接泄漏
        continuation.invokeOnCancellation { call.cancel() }
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "无错误详情"
                        continuation.resumeWithException(
                            Exception("API调用失败: ${response.code} ${response.message}, 详情: $errorBody")
                        )
                        return
                    }

                    try {
                        val responseBody = response.body?.string() ?: ""
                        val jsonResponse = JSONObject(responseBody)
                        val choices = jsonResponse.getJSONArray("choices")
                        val message = choices.getJSONObject(0).getJSONObject("message")
                        continuation.resume(message.getString("content"))
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            }
        })
    }
}
