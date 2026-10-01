package com.mysteriousjourney.domain

import com.mysteriousjourney.data.api.AiApiService
import com.mysteriousjourney.data.model.Message
import com.mysteriousjourney.domain.model.ChatMessage
import com.mysteriousjourney.domain.model.GameConfig
import com.mysteriousjourney.domain.model.GameState
import com.mysteriousjourney.domain.model.Money
import com.mysteriousjourney.domain.model.OpeningScenario
import com.mysteriousjourney.domain.model.OpeningScenarios
import com.mysteriousjourney.domain.model.PlayerState
import com.mysteriousjourney.domain.model.WorldState
import com.mysteriousjourney.util.StateParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 游戏引擎核心类
 * 负责管理游戏对话历史、处理玩家输入、调用AI API、解析响应并更新游戏状态
 */
class GameEngine constructor(
    private val aiApiService: AiApiService
) {
    // 当前游戏状态
    private var gameState: GameState = GameConfig.INITIAL_GAME_STATE
    
    // 系统提示词
    private val systemPrompt: String
        get() = buildSystemPrompt()
    
    /**
     * 获取当前游戏状态
     */
    fun getGameState(): GameState = gameState
    
    fun applyStateUpdate(stateUpdate: StateParser.StateUpdate) {
        updateGameState(stateUpdate)
    }
    
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
     * 处理玩家输入
     * @param playerInput 玩家的自然语言输入
     * @return 处理结果，包含叙事文本和是否成功
     */
    suspend fun processPlayerInput(playerInput: String): ProcessResult = withContext(Dispatchers.IO) {
        // 构建聊天历史
        val chatHistory = buildChatHistory()
        
        // 调用AI API（流式版本）
        val result = aiApiService.sendGameMessageStream(
            systemPrompt = systemPrompt,
            chatHistory = chatHistory,
            userInput = playerInput
        ) { chunk ->
            // 流式回调暂时不处理，因为GameEngine需要完整结果
        }
        
        result.fold(
            onSuccess = { aiResponse ->
                // 解析AI响应
                val parseResult = StateParser.parseResponse(aiResponse)
                val narrative = parseResult.narrative
                val stateUpdate = parseResult.stateUpdate
                
                // 更新游戏状态
                if (stateUpdate != null) {
                    updateGameState(stateUpdate)
                }
                
                // 添加到聊天历史
                addToChatHistory(playerInput, aiResponse)
                
                ProcessResult(
                    narrative = narrative,
                    success = true,
                    error = null,
                    consistencyScore = parseResult.consistencyScore
                )
            },
            onFailure = { error ->
                ProcessResult(
                    narrative = "",
                    success = false,
                    error = error.message ?: "未知错误",
                    consistencyScore = 0
                )
            }
        )
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
- 物品：${player.inventory.joinToString("、")}
- 知识：${player.knowledge.joinToString("、")}
- 状态效果：${if (player.statusEffects.isEmpty()) "无" else player.statusEffects.joinToString("、")}
- 能力：${if (player.abilities.isEmpty()) "无" else player.abilities.joinToString(", ") { "${it.name}(等级${it.level})" }}
- 封印物品：${if (player.sealedItems.isEmpty()) "无" else player.sealedItems.joinToString(", ") { "${it.name}(${if(it.active) "激活" else "休眠"})" }}

【当前世界状态】
- 当前时间：${world.currentTime}
- 当前位置：${world.currentLocation}
- 天气：${world.weather}
- 已探索地点：${world.visitedLocations.joinToString("、")}
- NPC关系：${if (player.npcRelations.isEmpty()) "无" else player.npcRelations.map { "${it.key}:${it.value}" }.joinToString(", ")}

【当前任务】
${if (world.openQuests.isEmpty()) "暂无进行中的任务" else world.openQuests.map { "- ${it.name}: ${it.description} (${it.status})" }.joinToString("\n")}

【势力关系】
${player.factionRelations.map { "- ${it.key}: ${it.value}" }.joinToString("\n")}

【核心叙事记忆】
${if (gameState.gameMemory.isEmpty()) "暂无关键记忆" else gameState.gameMemory.map { "- $it" }.joinToString("\n")}

【重要】：确保你的回复与上述状态信息完全吻合，不要出现矛盾。叙事要基于当前的位置和状态展开，并提供有意义的选择，让玩家能够影响故事的发展方向。
""".trimIndent()
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
     * 添加消息到聊天历史
     */
    fun addToChatHistory(playerInput: String, aiResponse: String) {
        val currentTime = System.currentTimeMillis()
        
        val newHistory = gameState.chatHistory + listOf(
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
        )
        
        gameState = gameState.copy(chatHistory = newHistory)
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
                currentPlayer.spirituality.copy(
                    current = spiritUpdate.current.coerceIn(0, currentPlayer.spirituality.max)
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
            money = stateUpdate.money?.toMoney(currentPlayer.money) ?: currentPlayer.money,
            statusEffects = stateUpdate.statusEffects ?: currentPlayer.statusEffects
        )
        
        val updatedInventory = if (stateUpdate.inventoryChanges != null) {
            val (added, removed) = StateParser.parseInventoryChanges(stateUpdate.inventoryChanges!!)
            val newInventory = currentPlayer.inventory.toMutableList()
            
            removed.forEach { item ->
                val index = newInventory.indexOfFirst { it == item }
                if (index != -1) {
                    newInventory.removeAt(index)
                } else {
                    val fuzzyIndex = newInventory.indexOfFirst { it.contains(item, ignoreCase = true) }
                    if (fuzzyIndex != -1) {
                        newInventory.removeAt(fuzzyIndex)
                    }
                }
            }
            
            newInventory.addAll(added)
            newInventory
        } else {
            currentPlayer.inventory
        }
        
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
     * 执行冥想恢复灵性
     * @param hours 冥想时长（小时）
     * @return 恢复的灵性值
     */
    fun meditate(hours: Int = 1): Int {
        val player = gameState.player
        val recoveryRate = 10 // 每小时恢复10点灵性
        val recovered = (recoveryRate * hours).coerceAtMost(
            player.spirituality.max - player.spirituality.current
        )
        
        gameState = gameState.copy(
            player = player.copy(
                spirituality = player.spirituality.copy(
                    current = player.spirituality.current + recovered
                )
            )
        )
        
        return recovered
    }
    
    /**
     * 检查玩家是否处于危险状态
     */
    fun isPlayerInDanger(): Boolean {
        val player = gameState.player
        return player.spirituality.current < 20 ||
                player.sanity.madnessValue > GameConfig.GameRules.MADNESS_THRESHOLD_HIGH
    }
    
    /**
     * 获取玩家状态摘要
     */
    fun getPlayerStatusSummary(): String {
        val player = gameState.player
        return buildString {
            append("灵性: ${player.spirituality.current}/${player.spirituality.max}")
            append(" | 疯狂: ${player.sanity.madnessValue}")
            append(" | 金钱: ${player.money.goldPounds}金镑")
            append(" | 位置: ${gameState.world.currentLocation}")
        }
    }
    
    /**
     * 处理结果数据类
     */
    data class ProcessResult(
        val narrative: String,
        val success: Boolean,
        val error: String?,
        val consistencyScore: Int = 100 // 新增：一致性评分
    )
}
