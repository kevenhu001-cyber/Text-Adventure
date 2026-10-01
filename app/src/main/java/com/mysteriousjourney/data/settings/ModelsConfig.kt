package com.mysteriousjourney.data.settings

/**
 * 用户自定义的 AI 模型配置
 */
data class CustomModelConfig(
    val id: String = "",            // 唯一标识
    val displayName: String = "",   // 显示名称
    val apiUrl: String = "",        // API 地址
    val modelId: String = "",       // 模型 ID
    val apiKey: String = "",        // API Key
    val isActive: Boolean = false   // 是否为当前使用的模型
) {
    fun isValid(): Boolean {
        return apiUrl.isNotBlank() && modelId.isNotBlank() && apiKey.isNotBlank()
    }
}

/**
 * AI 模型配置列表（存储在 DataStore 中）
 */
data class AiModelsConfig(
    val customModels: List<CustomModelConfig> = emptyList(),
    val activeModelType: String = "" // "custom_<id>"，空字符串表示未选择
)
