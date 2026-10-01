package com.mysteriousjourney.data.repository

import android.content.Context
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
 * 存档仓库（DataStore 实现）
 * 提供游戏存档的保存、加载、删除等操作
 * 所有存档数据序列化为 JSON 存储在 DataStore 中
 */
class SaveRepository(
    private val gson: Gson = Gson()
) {
    companion object {
        private val SAVES_KEY = stringPreferencesKey("game_saves")

        private const val MAX_SAVES = 20
    }

    private val dataStore: DataStore<Preferences>
        get() = GameApplication.instance.dataStore

    /**
     * DataStore 中存储的存档条目
     */
    data class SaveSlotEntry(
        val id: Long,
        val name: String,
        val timestamp: Long,
        val playerName: String,
        val location: String,
        val spirit: Int,
        val madness: Int,
        val playTime: String,
        val gameStateJson: String
    )

    /**
     * 保存游戏状态
     * @param saveName 存档名称
     * @param gameState 游戏状态对象（任意类型）
     * @param metadataFn 提供显示用的元数据（玩家名、位置、灵性等）
     * @return 存档ID
     */
    suspend fun <T> saveGame(
        saveName: String,
        gameState: T,
        metadataFn: (suspend () -> SaveMetadata)? = null
    ): Long {
        val currentTime = System.currentTimeMillis()
        val gameStateJson = gson.toJson(gameState)
        val id = currentTime // 使用时间戳作为ID

        val metadata = metadataFn?.invoke() ?: SaveMetadata()

        val entry = SaveSlotEntry(
            id = id,
            name = saveName,
            timestamp = currentTime,
            playerName = metadata.playerName,
            location = metadata.location,
            spirit = metadata.spirit,
            madness = metadata.madness,
            playTime = metadata.playTime,
            gameStateJson = gameStateJson
        )

        dataStore.edit { prefs ->
            val saves = loadSaves(prefs).toMutableList()
            saves.add(0, entry) // 最新存档放前面
            // 限制最大存档数
            val trimmed = if (saves.size > MAX_SAVES) saves.take(MAX_SAVES) else saves
            prefs[SAVES_KEY] = gson.toJson(trimmed)
        }

        return id
    }

    /**
     * 更新已有存档
     */
    suspend fun <T> updateSave(
        saveId: Long,
        gameState: T,
        metadataFn: (suspend () -> SaveMetadata)? = null
    ) {
        val gameStateJson = gson.toJson(gameState)
        val currentTime = System.currentTimeMillis()

        val metadata = metadataFn?.invoke() ?: SaveMetadata()

        dataStore.edit { prefs ->
            val saves = loadSaves(prefs).toMutableList()
            val index = saves.indexOfFirst { it.id == saveId }
            if (index != -1) {
                saves[index] = saves[index].copy(
                    gameStateJson = gameStateJson,
                    timestamp = currentTime,
                    playerName = metadata.playerName,
                    location = metadata.location,
                    spirit = metadata.spirit,
                    madness = metadata.madness,
                    playTime = metadata.playTime,
                    name = saves[index].name // 保留原存档名
                )
                prefs[SAVES_KEY] = gson.toJson(saves)
            }
        }
    }

    /**
     * 加载游戏状态
     * @param saveId 存档ID
     * @param stateClass 游戏状态类类型
     * @return 游戏状态对象，如果不存在则返回null
     */
    suspend fun <T> loadGame(saveId: Long, stateClass: Class<T>): T? {
        val saves = getAllSavesInternal()
        val entry = saves.find { it.id == saveId } ?: return null
        return try {
            gson.fromJson(entry.gameStateJson, stateClass)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取所有存档列表（不含 gameStateJson，供 UI 使用）
     */
    suspend fun getAllSaves(): List<SaveSlotEntry> {
        return getAllSavesInternal()
    }

    /**
     * 获取存档列表 Flow
     */
    fun getAllSavesFlow(): Flow<List<SaveSlotEntry>> {
        return dataStore.data.map { prefs ->
            loadSaves(prefs)
        }
    }

    /**
     * 删除存档
     */
    suspend fun deleteSave(saveId: Long) {
        dataStore.edit { prefs ->
            val saves = loadSaves(prefs).toMutableList()
            saves.removeAll { it.id == saveId }
            prefs[SAVES_KEY] = gson.toJson(saves)
        }
    }

    /**
     * 删除所有存档
     */
    suspend fun deleteAllSaves() {
        dataStore.edit { prefs ->
            prefs.remove(SAVES_KEY)
        }
    }

    /**
     * 获取存档数量
     */
    suspend fun getSaveCount(): Int {
        return getAllSavesInternal().size
    }

    /**
     * 检查存档是否存在
     */
    suspend fun saveExists(saveId: Long): Boolean {
        return getAllSavesInternal().any { it.id == saveId }
    }

    // ============ 内部方法 ============

    private suspend fun getAllSavesInternal(): List<SaveSlotEntry> {
        return dataStore.data.map { prefs ->
            loadSaves(prefs)
        }.first()
    }

    private fun loadSaves(prefs: Preferences): List<SaveSlotEntry> {
        val json = prefs[SAVES_KEY] ?: return emptyList()
        return try {
            val type = object : TypeToken<List<SaveSlotEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 存档显示元数据
     */
    data class SaveMetadata(
        val playerName: String = "旅行者",
        val location: String = "未知",
        val spirit: Int = 100,
        val madness: Int = 0,
        val playTime: String = "0分钟"
    )
}
