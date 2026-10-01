package com.mysteriousjourney.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mysteriousjourney.GameApplication
import com.mysteriousjourney.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 设置仓库
 * 管理 AI 模型配置的持久化存储（DataStore）
 */
class SettingsRepository(
    private val gson: Gson = Gson()
) {
    companion object {
        private val AI_MODELS_CONFIG_KEY = stringPreferencesKey("ai_models_config")
    }

    private val dataStore: DataStore<Preferences>
        get() = GameApplication.instance.dataStore

    /**
     * 获取 AI 模型配置 Flow
     */
    fun getAiModelsConfigFlow(): Flow<AiModelsConfig> {
        return dataStore.data.map { prefs ->
            loadConfig(prefs)
        }
    }

    /**
     * 保存 AI 模型配置
     */
    suspend fun saveAiModelsConfig(config: AiModelsConfig) {
        dataStore.edit { prefs ->
            prefs[AI_MODELS_CONFIG_KEY] = gson.toJson(config)
        }
    }

    /**
     * 添加自定义模型
     * @return 实际入库的模型（id 由仓库生成）
     */
    suspend fun addCustomModel(model: CustomModelConfig): CustomModelConfig {
        val config = getConfig()
        val stored = model.copy(id = generateModelId())
        saveAiModelsConfig(config.copy(customModels = config.customModels + stored))
        return stored
    }

    /**
     * 更新自定义模型
     */
    suspend fun updateCustomModel(modelId: String, updated: CustomModelConfig) {
        val config = getConfig()
        val updatedModels = config.customModels.map {
            if (it.id == modelId) updated.copy(id = modelId) else it
        }
        saveAiModelsConfig(config.copy(customModels = updatedModels))
    }

    /**
     * 删除自定义模型
     */
    suspend fun deleteCustomModel(modelId: String) {
        val config = getConfig()
        val updatedModels = config.customModels.filter { it.id != modelId }
        saveAiModelsConfig(config.copy(customModels = updatedModels))
    }

    /**
     * 设置活跃模型（预设或自定义）
     */
    suspend fun setActiveModel(activeType: String) {
        val config = getConfig()
        saveAiModelsConfig(config.copy(activeModelType = activeType))
    }

    /**
     * 获取当前配置
     */
    suspend fun getConfig(): AiModelsConfig {
        return dataStore.data.map { prefs ->
            loadConfig(prefs)
        }.first()
    }

    private fun loadConfig(prefs: Preferences): AiModelsConfig {
        val json = prefs[AI_MODELS_CONFIG_KEY] ?: return AiModelsConfig()
        return try {
            val type = object : TypeToken<AiModelsConfig>() {}.type
            gson.fromJson(json, type) ?: AiModelsConfig()
        } catch (e: Exception) {
            AiModelsConfig()
        }
    }

    private var modelIdCounter = 0L
    private fun generateModelId(): String {
        modelIdCounter++
        return "custom_${System.currentTimeMillis()}_$modelIdCounter"
    }
}
