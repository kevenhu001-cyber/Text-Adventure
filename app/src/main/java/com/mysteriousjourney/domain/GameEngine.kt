package com.mysteriousjourney.domain

import com.mysteriousjourney.data.api.AiApiService
import com.mysteriousjourney.data.model.Message
import com.mysteriousjourney.domain.model.ChatMessage
import com.mysteriousjourney.domain.model.GameConfig
import com.mysteriousjourney.domain.model.GameState
import com.mysteriousjourney.domain.model.OpeningScenario
import com.mysteriousjourney.domain.model.OpeningScenarios
import com.mysteriousjourney.domain.model.PlayerState
import com.mysteriousjourney.domain.model.WorldState
import com.mysteriousjourney.util.StateParser
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

/**
 * 游戏引擎核心类
 * 负责管理游戏对话历史、处理玩家输入、调用AI API、解析响应并更新游戏状态
 */
class GameEngine constructor(
    private val aiApiService: AiApiService
) {
    private companion object {
        /** 系统提示词里各类状态数据的展示上限，控制提示词体积不随游戏时长膨胀 */
        const val INVENTORY_LIMIT = 20
        const val KNOWLEDGE_LIMIT = 12
        const val STATUS_LIMIT = 10
        const val ABILITY_LIMIT = 10
        const val SEALED_LIMIT = 10
        const val LOCATION_LIMIT = 12
        const val RELATION_LIMIT = 12
        const val MEMORY_LIMIT = 10

        /** 聊天历史保留上限：20 轮 = 40 条消息 */
        const val MAX_CHAT_MESSAGES = 40

        /** 聊天历史总字符上限，防止单轮超长响应把存档撑爆 */
        const val MAX_CHAT_CHARS = 12_000
    }

    // 当前游戏状态
    private var gameState: GameState = GameConfig.INITIAL_GAME_STATE

    /**
     * 获取当前游戏状态
     */
    fun getGameState(): GameState = gameState
    
    /**
     * 初始化游戏
     * @return 开场叙事文本
     */
    fun initializeGame(): String {
        val scenario = OpeningScenarios.getRandomScenario()
        gameState = GameState(
            player = scenario.initialPlayerState,
            world = scenario.initialWorldState,
            isInitialized = true
        )
        return scenario.narrative
    }
    
    fun initializeGameWithScenario(scenario: OpeningScenario): String {
        gameState = GameState(
            player = scenario.initialPlayerState,
            world = scenario.initialWorldState,
            isInitialized = true
        )
        return scenario.narrative
    }
    
    fun initializeGameWithOpening(openingNarrative: String): String {
        val scenario = OpeningScenarios.ALL_SCENARIOS.find { it.narrative == openingNarrative }
            ?: OpeningScenarios.getRandomScenario()
        gameState = GameState(
            player = scenario.initialPlayerState,
            world = scenario.initialWorldState,
            isInitialized = true
        )
        return scenario.narrative
    }
    
    /**
     * 加载游戏状态
     * @param savedState 保存的游戏状态
     */
    fun loadGameState(savedState: GameState) {
        gameState = savedState
    }
    
    /**
     * 重置游戏
     */
    fun resetGame() {
        gameState = GameConfig.INITIAL_GAME_STATE
    }
    
    /**
     * 推进一个回合：流式获取 AI 叙事，并在流结束后统一落地状态。
     *
     * 事件顺序固定为若干 [TurnEvent.Chunk]，最后一个 [TurnEvent.Completed]。
     * 消费方只需在 collect 正常结束后提交最终 UI 状态，
     * 从协议上排除了"迟到的中间帧覆盖最终结果"的可能。
     */
    fun streamTurn(userInput: String): Flow<TurnEvent> = channelFlow {
        val buffer = StringBuilder()

        aiApiService.streamGameMessage(
            systemPrompt = buildSystemPrompt(),
            chatHistory = buildChatHistory(),
            userInput = userInput
        ).collect { chunk ->
            buffer.append(chunk)
            // 用 trySend 而非 emit：这里身处内层流的 collect 回调，
            // flow{} 的 emit 会被 Flow invariant 校验拒绝
            trySend(TurnEvent.Chunk(chunk))
        }

        val fullText = buffer.toString()

        val stateUpdate = StateParser.parseStateUpdate(fullText)
        if (stateUpdate != null) {
            updateGameState(stateUpdate)
        }

        addToChatHistory(playerInput = userInput, aiResponse = fullText)
        trySend(TurnEvent.Completed(fullText))
    }.flowOn(Dispatchers.IO)

    /**
     * 单个回合内向外广播的事件
     */
    sealed interface TurnEvent {
        /** 一段增量叙事文本，顺序与 AI 返回顺序一致 */
        data class Chunk(val text: String) : TurnEvent

        /** 整轮结束，[fullText] 为含状态标记的完整原始响应 */
        data class Completed(val fullText: String) : TurnEvent
    }
    
    /**
     * 构建系统提示词
     * 包含当前游戏状态的上下文信息
     */
    fun buildSystemPrompt(): String {
        val player = gameState.player
        val world = gameState.world
        
        return """
${GameConfig.SYSTEM_PROMPT}

【当前玩家状态】
- 姓名：${player.name}
- 表面身份：${player.surfaceIdentity}
- 当前序列：${player.currentSequence.name}
- 消化进度：${player.currentSequence.digestionProgress}%
- 灵性：${player.spirituality.current}/${player.spirituality.max}
- 健康状态：${player.healthStatus}
- 疯狂值：${player.sanity.madnessValue}
- 污染程度：${player.sanity.corruptionLevel}
- 金钱：${player.money.goldPounds}金镑 ${player.money.soles}苏勒 ${player.money.pence}便士
- 物品：${player.inventory.renderPromptList(INVENTORY_LIMIT)}
- 知识：${player.knowledge.renderPromptList(KNOWLEDGE_LIMIT)}
- 状态效果：${player.statusEffects.renderPromptList(STATUS_LIMIT)}
- 能力：${player.abilities.renderPromptList(ABILITY_LIMIT) { "${it.name}(等级${it.level})" }}
- 封印物品：${player.sealedItems.renderPromptList(SEALED_LIMIT) { "${it.name}(${if(it.active) "激活" else "休眠"})" }}

【当前世界状态】
- 当前时间：${world.currentTime}
- 当前位置：${world.currentLocation}
- 天气：${world.weather}
- 已探索地点：${world.visitedLocations.renderPromptList(LOCATION_LIMIT)}
- NPC关系：${player.npcRelations.renderPromptList(RELATION_LIMIT) { "${it.key}:${it.value}" }}

【当前任务】
${if (world.openQuests.isEmpty()) "暂无进行中的任务" else world.openQuests.map { "- ${it.name}: ${it.description} (${it.status})" }.joinToString("\n")}

【势力关系】
${player.factionRelations.map { "- ${it.key}: ${it.value}" }.joinToString("\n")}

【核心叙事记忆】
${if (gameState.gameMemory.isEmpty()) "暂无关键记忆" else gameState.gameMemory.renderPromptList(MEMORY_LIMIT) { "- $it" }}

以上是既成事实，不得矛盾。写正文时不要复述这些信息，只在剧情需要时让它们自然登场。
""".trimIndent()
    }

    /**
     * 把列表拼进系统提示词，超过上限时只保留最近若干项，并显式说明被截断。
     *
     * 状态块如果完全不加限制，玩得越久系统提示词就越长，开头的风格规则会被越埋越深，
     * 最终导致"改了提示词但玩到后面文风又飘回去"。这里给每类数据设上限来兜住提示词体积。
     * 明写"另有 N 项未列出"比静默截断更重要——否则 AI 会以为玩家身上就只有列出的这几样。
     */
    private fun <T> List<T>.renderPromptList(limit: Int, transform: (T) -> String = { it.toString() }): String {
        if (isEmpty()) return "无"
        val shown = if (size <= limit) this else takeLast(limit)
        val body = shown.joinToString("、", transform = transform)
        return if (shown.size < size) "$body（另有 ${size - shown.size} 项未列出）" else body
    }

    /**
     * Map 版本的同类限制（NPC 关系用）。
     */
    private fun <K, V> Map<K, V>.renderPromptList(limit: Int, transform: (K, V) -> String): String {
        if (isEmpty()) return "无"
        val all = entries.toList()
        val shown = if (all.size <= limit) all else all.takeLast(limit)
        val body = shown.joinToString("、") { (k, v) -> transform(k, v) }
        return if (shown.size < all.size) "$body（另有 ${all.size - shown.size} 项未列出）" else body
    }

    /**
     * 构建聊天历史
     * 将GameState中的聊天历史转换为API所需的Message格式
     */
    fun buildChatHistory(): List<Message> {
        return gameState.chatHistory.map { chatMessage ->
            Message(
                role = chatMessage.role,
                content = chatMessage.content
            )
        }
    }
    
    /**
     * 添加消息到聊天历史。
     *
     * 历史会随回合数无限增长，而它整体会被序列化进存档。
     * 这里只保留最近若干轮：[AiApiService] 下发给模型的本来也就是最后 12 条，
     * 更早的内容从不进入 AI 上下文，因此裁剪不会影响叙事连贯性。
     */
    fun addToChatHistory(playerInput: String, aiResponse: String) {
        val currentTime = System.currentTimeMillis()

        val newHistory = (gameState.chatHistory + listOf(
            ChatMessage(
                role = Message.ROLE_USER,
                content = playerInput,
                timestamp = currentTime
            ),
            ChatMessage(
                role = Message.ROLE_ASSISTANT,
                content = aiResponse,
                timestamp = currentTime + 1
            )
        )).let { trimToRecent(it) }

        gameState = gameState.copy(chatHistory = newHistory)
    }

    /**
     * 按轮数与总字符数双重裁剪聊天历史，优先保留最近的内容。
     */
    private fun trimToRecent(history: List<ChatMessage>): List<ChatMessage> {
        var trimmed = if (history.size > MAX_CHAT_MESSAGES) history.takeLast(MAX_CHAT_MESSAGES) else history

        var totalChars = trimmed.sumOf { it.content.length }
        while (totalChars > MAX_CHAT_CHARS && trimmed.size > 2) {
            val dropped = trimmed.first()
            trimmed = trimmed.drop(1)
            totalChars -= dropped.content.length
        }
        return trimmed
    }
    
    /**
     * 根据状态更新对象更新游戏状态
     */
    private fun updateGameState(stateUpdate: StateParser.StateUpdate) {
        val currentPlayer = gameState.player
        val currentWorld = gameState.world
        
        val spiritUpdate = stateUpdate.spirituality
        val spiritChange = stateUpdate.spiritChange
        val madnessUpdate = stateUpdate.madness
        val madnessChange = stateUpdate.madnessChange
        
        val newSpirituality = when {
            spiritUpdate != null -> {
                // 上限随序列晋升提升。只接受"变大"，避免 AI 误写导致上限回退。
                val newMax = maxOf(currentPlayer.spirituality.max, spiritUpdate.max)
                currentPlayer.spirituality.copy(
                    current = spiritUpdate.current.coerceIn(0, newMax),
                    max = newMax
                )
            }
            spiritChange != null -> {
                currentPlayer.spirituality.copy(
                    current = (currentPlayer.spirituality.current + spiritChange).coerceIn(0, currentPlayer.spirituality.max)
                )
            }
            else -> currentPlayer.spirituality
        }
        
        val newMadness = when {
            madnessUpdate != null -> {
                madnessUpdate.coerceIn(0, 100)
            }
            madnessChange != null -> {
                (currentPlayer.sanity.madnessValue + madnessChange).coerceIn(0, 100)
            }
            else -> currentPlayer.sanity.madnessValue
        }
        
        val updatedPlayer = currentPlayer.copy(
            spirituality = newSpirituality,
            sanity = currentPlayer.sanity.copy(madnessValue = newMadness),
            money = stateUpdate.money?.toMoney() ?: currentPlayer.money,
            statusEffects = stateUpdate.statusChanges
                ?.let { applyItemChanges(currentPlayer.statusEffects, it) }
                ?: currentPlayer.statusEffects
        )

        val updatedInventory = stateUpdate.inventoryChanges
            ?.let { applyItemChanges(currentPlayer.inventory, it) }
            ?: currentPlayer.inventory
        
        val updatedWorld = currentWorld.copy(
            currentLocation = stateUpdate.location ?: currentWorld.currentLocation,
            currentTime = stateUpdate.time ?: currentWorld.currentTime,
            visitedLocations = if (stateUpdate.location != null && 
                stateUpdate.location != currentWorld.currentLocation) {
                (currentWorld.visitedLocations + stateUpdate.location?.let { listOf(it) }.orEmpty()).distinct()
            } else {
                currentWorld.visitedLocations
            }
        )
        
        val finalPlayer = updatedPlayer.copy(inventory = updatedInventory)
        
        // 更新关键记忆
        val updatedMemory = if (stateUpdate.memories != null) {
            (gameState.gameMemory + stateUpdate.memories!!).distinct()
        } else {
            gameState.gameMemory
        }
        
        gameState = gameState.copy(
            player = finalPlayer,
            world = updatedWorld,
            gameMemory = updatedMemory
        )
    }

    /**
     * 对一个字符串列表施加 [StateParser.ItemChanges] 增删。
     *
     * 删除时先精确匹配；匹配不到才退化为模糊匹配，并取"名称最接近"的那个
     * （长度差最小），而不是列表里第一个命中的——
     * 否则"失去钥匙"会优先吃掉"黄铜钥匙"，而真正想丢的可能是"神秘钥匙"。
     */
    private fun applyItemChanges(current: List<String>, changes: StateParser.ItemChanges): List<String> {
        val result = current.toMutableList()

        changes.removed.forEach { item ->
            val exact = result.indexOf(item)
            if (exact != -1) {
                result.removeAt(exact)
                return@forEach
            }

            val fuzzy = result.indices
                .filter { result[it].contains(item, ignoreCase = true) }
                .minByOrNull { abs(result[it].length - item.length) }
            if (fuzzy != null) {
                result.removeAt(fuzzy)
            }
        }

        changes.added
            .filter { it.isNotBlank() }
            .forEach { item -> if (!result.contains(item)) result += item }

        return result
    }
    
}
