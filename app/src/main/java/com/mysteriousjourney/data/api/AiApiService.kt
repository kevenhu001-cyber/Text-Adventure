package com.mysteriousjourney.data.api

import com.mysteriousjourney.data.model.Message
import com.mysteriousjourney.data.settings.AiModelsConfig
import com.mysteriousjourney.data.settings.CustomModelConfig
import com.mysteriousjourney.data.settings.SettingsRepository
import com.mysteriousjourney.domain.model.GameConfig
import kotlinx.coroutines.Dispatchers
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
     * 发送游戏消息到AI (流式版本)
     */
    suspend fun sendGameMessageStream(
        systemPrompt: String,
        chatHistory: List<Message>,
        userInput: String,
        onChunk: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val config = currentConfig
        if (config == null || !config.isValid()) {
            return@withContext Result.failure(
                Exception("尚未配置 AI 模型，请在「设置」中填写 API 地址、模型 ID 和 Key")
            )
        }
        return@withContext try {
            val messages = buildMessages(systemPrompt, chatHistory, userInput)
            Result.success(callChatCompletionStream(config, messages, onChunk))
        } catch (e: Exception) {
            Result.failure(e)
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
        val effectivePrompt = if (systemPrompt.isNotEmpty()) systemPrompt else GameConfig.SYSTEM_PROMPT

        messages.put(
            JSONObject().apply {
                put("role", "system")
                put("content", effectivePrompt)
            }
        )

        val compressedHistory = compressChatHistory(chatHistory)

        for (i in compressedHistory.indices) {
            val message = compressedHistory[i]
            messages.put(
                JSONObject().apply {
                    put("role", message.role)
                    put("content", message.content)
                }
            )
        }

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
     * OpenAI 兼容 API 流式调用（SSE）
     */
    private suspend fun callChatCompletionStream(
        config: CustomModelConfig,
        messages: JSONArray,
        onChunk: (String) -> Unit
    ): String = suspendCancellableCoroutine { continuation ->
        val requestBody = JSONObject().apply {
            put("model", config.modelId)
            put("messages", messages)
            put("temperature", 0.8)
            put("max_tokens", 8192)
            put("stream", true)
            put("top_p", 0.95)
            put("frequency_penalty", 0.2)
            put("presence_penalty", 0.2)
            put("response_format", JSONObject().put("type", "text"))
        }.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(config.apiUrl)
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
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
                        val responseBody = response.body ?: return
                        val reader = responseBody.charStream()
                        val fullContent = StringBuilder()

                        reader.useLines { lines ->
                            lines.forEach { line ->
                                if (line.trim().isNotEmpty() && line.startsWith("data: ")) {
                                    val data = line.substring(6).trim()
                                    if (data == "[DONE]") {
                                        return@useLines
                                    }

                                    try {
                                        val jsonData = JSONObject(data)
                                        val choices = jsonData.optJSONArray("choices")
                                        if (choices != null && choices.length() > 0) {
                                            val choice = choices.getJSONObject(0)
                                            val delta = choice.optJSONObject("delta")
                                            if (delta != null) {
                                                val content = delta.optString("content", "")
                                                if (content.isNotEmpty() && content != "null") {
                                                    fullContent.append(content)
                                                    onChunk(content)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("解析流式数据失败: ${e.message}")
                                    }
                                }
                            }
                        }

                        continuation.resume(fullContent.toString())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            }
        })
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

        client.newCall(request).enqueue(object : okhttp3.Callback {
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
