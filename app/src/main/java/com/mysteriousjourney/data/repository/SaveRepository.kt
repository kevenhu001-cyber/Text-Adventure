package com.mysteriousjourney.data.repository

import com.google.gson.Gson
import com.mysteriousjourney.GameApplication
import com.mysteriousjourney.domain.model.Attributes
import com.mysteriousjourney.domain.model.GameState
import com.mysteriousjourney.domain.model.Money
import com.mysteriousjourney.domain.model.PlayerState
import com.mysteriousjourney.domain.model.ProgressMetrics
import com.mysteriousjourney.domain.model.Sanity
import com.mysteriousjourney.domain.model.SequenceInfo
import com.mysteriousjourney.domain.model.Spirituality
import com.mysteriousjourney.domain.model.WorldState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 存档仓库（文件系统实现）
 *
 * 每个存档拆成两个文件：`<id>.meta.json` 放列表页要的元数据，`<id>.state.json` 放完整游戏状态。
 *
 * 早期实现把全部 20 个存档序列化进 DataStore 的一个 preference 字符串里，
 * 每次增删改都要重写整个数组，而且 `GameState` 内嵌完整 chatHistory，单个存档可达上百 KB。
 * 拆分之后：列存档只读几十个小文件，加载存档只读一个文件，DataStore 不再承载大对象。
 */
class SaveRepository(
    private val gson: Gson = Gson()
) {
    private companion object {
        const val MAX_SAVES = 20
        const val SCHEMA_VERSION = 2
        const val META_SUFFIX = ".meta.json"
        const val STATE_SUFFIX = ".state.json"
        const val DEFAULT_SPIRITUALITY_MAX = 100
    }

    private val saveDir: File
        get() = File(GameApplication.instance.filesDir, "saves").apply { mkdirs() }

    /**
     * 列表页使用的存档元数据（不含游戏状态，体积很小）
     */
    data class SaveSlotEntry(
        val id: Long,
        val name: String,
        val timestamp: Long,
        val playerName: String,
        val location: String,
        val spirit: Int,
        val madness: Int,
        val playTime: String
    )

    /**
     * 落盘的存档内容
     *
     * [schemaVersion] 缺失时按 v1 处理，由 [migrate] 负责补齐；
     * 读回时还要过一遍 [sanitize]，防止旧存档里缺失字段反序列化成 0/null 引发除零或空指针。
     */
    private data class SavePayload(
        val schemaVersion: Int = SCHEMA_VERSION,
        val savedAt: Long = 0L,
        val gameState: Any? = null
    )

    data class SaveMetadata(
        val playerName: String = "旅行者",
        val location: String = "未知",
        val spirit: Int = 100,
        val madness: Int = 0,
        val playTime: String = "0分钟"
    )

    /**
     * 保存游戏
     * @return 存档ID
     */
    suspend fun <T : Any> saveGame(
        saveName: String,
        gameState: T,
        metadataFn: (suspend () -> SaveMetadata)? = null
    ): Long = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val id = generateSaveId(currentTime)
        val metadata = metadataFn?.invoke() ?: SaveMetadata()

        writeMeta(
            SaveSlotEntry(
                id = id,
                name = saveName,
                timestamp = currentTime,
                playerName = metadata.playerName,
                location = metadata.location,
                spirit = metadata.spirit,
                madness = metadata.madness,
                playTime = metadata.playTime
            )
        )

        // 状态单独落盘并携带 schema 版本
        val payload = SavePayload(
            schemaVersion = SCHEMA_VERSION,
            savedAt = currentTime,
            gameState = gameState
        )
        stateFile(id).writeText(gson.toJson(payload), Charsets.UTF_8)

        enforceSaveLimit()
        id
    }

    /**
     * 更新已有存档（保留原存档名与 ID）
     */
    suspend fun <T : Any> updateSave(
        saveId: Long,
        gameState: T,
        metadataFn: (suspend () -> SaveMetadata)? = null
    ) = withContext(Dispatchers.IO) {
        // meta 损坏/丢失时补建一条占位元数据，而不是静默丢弃这次保存——
        // 否则状态文件已写入但列表页永远看不到这个存档，形成"幽灵存档"。
        val existing = readMeta(saveId) ?: SaveSlotEntry(
            id = saveId,
            name = "存档 $saveId",
            timestamp = System.currentTimeMillis(),
            playerName = "旅行者",
            location = "未知",
            spirit = DEFAULT_SPIRITUALITY_MAX,
            madness = 0,
            playTime = "0分钟"
        )
        val metadata = metadataFn?.invoke() ?: SaveMetadata()

        writeMeta(existing.copy(
            timestamp = System.currentTimeMillis(),
            playerName = metadata.playerName,
            location = metadata.location,
            spirit = metadata.spirit,
            madness = metadata.madness,
            playTime = metadata.playTime
        ))

        val payload = SavePayload(
            schemaVersion = SCHEMA_VERSION,
            savedAt = System.currentTimeMillis(),
            gameState = gameState
        )
        stateFile(saveId).writeText(gson.toJson(payload), Charsets.UTF_8)
    }

    /**
     * 加载游戏
     * @return 游戏状态对象；存档不存在、损坏或版本不认识时返回 null
     */
    suspend fun <T> loadGame(saveId: Long, stateClass: Class<T>): T? = withContext(Dispatchers.IO) {
        val file = stateFile(saveId)
        if (!file.exists()) return@withContext null

        try {
            val rawPayload = gson.fromJson(file.readText(Charsets.UTF_8), SavePayload::class.java)
                ?: return@withContext null
            val payload = migrate(rawPayload)
            val rawState = payload.gameState ?: return@withContext null

            // gameState 声明为 Any?，Gson 会先读成 LinkedTreeMap，回写 JSON 再按目标类型解析
            val state = gson.fromJson(gson.toJson(rawState), stateClass) ?: return@withContext null
            sanitize(state)
        } catch (e: Exception) {
            // 损坏的存档不能拖垮整个列表，也不能抛出到 UI
            null
        }
    }

    /**
     * 获取所有存档元数据，按时间倒序
     */
    suspend fun getAllSaves(): List<SaveSlotEntry> = withContext(Dispatchers.IO) {
        val entries = metaFiles()
            .mapNotNull { readMetaByFile(it) }
            // 损坏或孤立的元数据跳过，不影响其余存档显示
            .sortedByDescending { it.timestamp }

        // meta 损坏或被删后会残留永不展示的 state 文件，读到列表时顺手清理。
        // saveGame 先写 meta 再写 state，因此不会在保存中途误删正在写入的存档。
        val knownIds = entries.mapTo(HashSet()) { it.id }
        saveDir.listFiles { f -> f.isFile && f.name.endsWith(STATE_SUFFIX) }
            ?.forEach { f ->
                if (f.name.removeSuffix(STATE_SUFFIX).toLongOrNull() !in knownIds) f.delete()
            }

        entries
    }

    /**
     * 删除存档
     */
    suspend fun deleteSave(saveId: Long) = withContext(Dispatchers.IO) {
        stateFile(saveId).delete()
        metaFile(saveId).delete()
    }

    // ============ 内部实现 ============

    private fun metaFile(id: Long) = File(saveDir, "$id$META_SUFFIX")

    private fun stateFile(id: Long) = File(saveDir, "$id$STATE_SUFFIX")

    private fun metaFiles(): List<File> =
        // listFiles 返回的是平台类型 Array<File>!，直接 orEmpty() 会因重载歧义被推断成 List，
        // 这里显式 toList() 再兜底，避免编译期类型不匹配。
        saveDir.listFiles { f -> f.isFile && f.name.endsWith(META_SUFFIX) }?.toList() ?: emptyList()

    private fun writeMeta(entry: SaveSlotEntry) {
        metaFile(entry.id).writeText(gson.toJson(entry), Charsets.UTF_8)
    }

    private fun readMeta(saveId: Long): SaveSlotEntry? =
        metaFile(saveId).takeIf { it.exists() }?.let { readMetaByFile(it) }

    /**
     * 读取单个元数据文件。
     *
     * Gson 用 Unsafe 分配实例、不跑构造函数，字段缺失时会塞 null 而不是默认值。
     * 列表页直接把 name/playerName/location 交给 UI 显示，这里必须先筛掉字段不完整的条目，
     * 否则一条损坏的元数据会让整个存档页崩掉。
     */
    @Suppress("SENSELESS_COMPARISON")
    private fun readMetaByFile(file: File): SaveSlotEntry? = try {
        gson.fromJson(file.readText(Charsets.UTF_8), SaveSlotEntry::class.java)
            ?.takeIf { it.id != 0L && it.name != null && it.playerName != null && it.location != null }
    } catch (e: Exception) {
        null
    }

    /**
     * 存档 ID 以时间戳为基础。同一毫秒内连续保存时补一个序号，避免主键碰撞导致
     * `updateSave` 改错档、`deleteSave` 误删两条。
     */
    private fun generateSaveId(now: Long): Long {
        var id = now
        var bump = 0
        while (metaFile(id).exists() || stateFile(id).exists()) {
            bump++
            id = now + bump
        }
        return id
    }

    /** 超出上限时删除最旧的存档 */
    private fun enforceSaveLimit() {
        val entries = metaFiles().mapNotNull { readMetaByFile(it) }.sortedByDescending { it.timestamp }
        entries.drop(MAX_SAVES).forEach {
            stateFile(it.id).delete()
            metaFile(it.id).delete()
        }
    }

    /**
     * 旧存档版本迁移。
     *
     * v1 → v2 只是存储结构变化（状态从嵌套 JSON 字符串变成直接内嵌对象），
     * 数据本身不需要改写；这里保留扩展位，后续加字段时在此按版本补齐。
     */
    private fun migrate(payload: SavePayload): SavePayload =
        when (payload.schemaVersion) {
            1 -> payload.copy(schemaVersion = 2)
            else -> payload
        }

    /**
     * 反序列化后的数据体检。
     *
     * Gson 不会跑构造函数，缺失字段会变成 0/null——例如旧存档里没有
     * `spirituality.max`，反序列化后就是 0，进度条会算出 Infinity/NaN。
     * 这里把明显不合理的值修正回安全区间。
     *
     * 列表字段在 Kotlin 侧是非空类型，但 Unsafe 分配的实例里确实可能是 null，
     * 所以下面的 `?:` 是必要的运行时兜底，编译器给的"永不为左操作数"告警可以忽略。
     */
    @Suppress("SENSELESS_COMPARISON")
    private fun <T> sanitize(state: T): T {
        if (state !is GameState) return state

        // 嵌套对象整体也可能缺成 null（例如旧版没有 spirituality 字段），先逐层兜底
        val player = state.player ?: PlayerState()
        val rawSpirit = player.spirituality ?: Spirituality(DEFAULT_SPIRITUALITY_MAX, DEFAULT_SPIRITUALITY_MAX)
        val safeMax = if (rawSpirit.max <= 0) DEFAULT_SPIRITUALITY_MAX else rawSpirit.max
        val safeSpirituality = rawSpirit.copy(
            max = safeMax,
            current = rawSpirit.current.coerceIn(0, safeMax)
        )
        val rawSanity = player.sanity ?: Sanity(0, 0)
        val rawMoney = player.money ?: Money(0, 0, 0)
        val rawSequence = player.currentSequence ?: SequenceInfo("占卜家", 9)

        val safePlayer = player.copy(
            name = player.name?.ifBlank { "旅行者" } ?: "旅行者",
            surfaceIdentity = player.surfaceIdentity?.ifBlank { "普通学生" } ?: "普通学生",
            currentSequence = rawSequence.copy(
                name = rawSequence.name?.ifBlank { "占卜家" } ?: "占卜家"
            ),
            attributes = player.attributes ?: Attributes(),
            spirituality = safeSpirituality,
            healthStatus = player.healthStatus?.ifBlank { "健康" } ?: "健康",
            sanity = rawSanity.copy(
                madnessValue = rawSanity.madnessValue.coerceIn(0, 100),
                corruptionLevel = rawSanity.corruptionLevel.coerceAtLeast(0)
            ),
            money = rawMoney.copy(
                goldPounds = rawMoney.goldPounds.coerceAtLeast(0),
                soles = rawMoney.soles.coerceAtLeast(0),
                pence = rawMoney.pence.coerceAtLeast(0)
            ),
            inventory = player.inventory ?: emptyList(),
            detailedInventory = player.detailedInventory ?: emptyList(),
            abilities = player.abilities ?: emptyList(),
            knowledge = player.knowledge ?: emptyList(),
            statusEffects = player.statusEffects ?: emptyList(),
            detailedStatusEffects = player.detailedStatusEffects ?: emptyList(),
            factionRelations = player.factionRelations ?: emptyMap(),
            npcRelations = player.npcRelations ?: emptyMap(),
            fateNodes = player.fateNodes ?: emptyList(),
            historyEvents = player.historyEvents ?: emptyList(),
            rolePlayProgress = player.rolePlayProgress ?: emptyMap(),
            rolePlayTopics = player.rolePlayTopics ?: emptyList(),
            sealedItems = player.sealedItems ?: emptyList(),
            madnessManifestation = player.madnessManifestation ?: "正常",
            dailyLog = player.dailyLog ?: emptyList(),
            titles = player.titles ?: emptyList(),
            achievements = player.achievements ?: emptyList(),
            characterRelations = player.characterRelations ?: emptyList(),
            skillTree = player.skillTree ?: emptyList(),
            destinyPath = player.destinyPath ?: emptyList(),
            mysteryPoints = player.mysteryPoints ?: emptyList(),
            progressMetrics = player.progressMetrics ?: ProgressMetrics()
        )

        val world = state.world ?: WorldState()
        val safeWorld = world.copy(
            currentTime = world.currentTime?.ifBlank { "未知" } ?: "未知",
            currentLocation = world.currentLocation?.ifBlank { "未知" } ?: "未知",
            weather = world.weather ?: "",
            visitedLocations = world.visitedLocations ?: emptyList(),
            openQuests = world.openQuests ?: emptyList(),
            npcStates = world.npcStates ?: emptyMap()
        )

        return state.copy(
            player = safePlayer,
            world = safeWorld,
            chatHistory = state.chatHistory ?: emptyList(),
            gameMemory = state.gameMemory ?: emptyList()
        ) as T
    }
}
