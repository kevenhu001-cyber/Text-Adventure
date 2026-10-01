package com.mysteriousjourney.domain

import com.mysteriousjourney.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.gson.Gson

/**
 * 游戏状态管理器
 * 使用StateFlow管理游戏状态，提供状态更新和持久化功能
 */
class GameStateManager {

    private val gson = Gson()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    /**
     * 获取当前游戏状态
     */
    fun getCurrentState(): GameState = _gameState.value

    /**
     * 更新整个游戏状态
     */
    fun updateGameState(newState: GameState) {
        _gameState.value = newState
    }

    /**
     * 更新玩家状态
     */
    fun updatePlayerState(playerState: PlayerState) {
        _gameState.value = _gameState.value.copy(player = playerState)
    }

    /**
     * 更新世界状态
     */
    fun updateWorldState(worldState: WorldState) {
        _gameState.value = _gameState.value.copy(world = worldState)
    }

    /**
     * 更新玩家名称
     */
    fun updatePlayerName(name: String) {
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(name = name)
        )
    }

    /**
     * 更新玩家位置
     */
    fun updatePlayerLocation(location: String) {
        val visitedLocations = _gameState.value.world.visitedLocations
        val newVisitedLocations = if (location in visitedLocations) {
            visitedLocations
        } else {
            visitedLocations + location
        }
        _gameState.value = _gameState.value.copy(
            world = _gameState.value.world.copy(
                currentLocation = location,
                visitedLocations = newVisitedLocations
            )
        )
    }

    /**
     * 更新时间
     */
    fun updateTime(time: String) {
        _gameState.value = _gameState.value.copy(
            world = _gameState.value.world.copy(currentTime = time)
        )
    }

    /**
     * 更新灵性值
     */
    fun updateSpirituality(current: Int, max: Int? = null) {
        val currentSpirituality = _gameState.value.player.spirituality
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                spirituality = Spirituality(
                    current = current,
                    max = max ?: currentSpirituality.max
                )
            )
        )
    }

    /**
     * 消耗灵性
     * @param amount 消耗量
     * @return 是否成功消耗
     */
    fun consumeSpirituality(amount: Int): Boolean {
        val currentSpirituality = _gameState.value.player.spirituality
        if (currentSpirituality.current < amount) {
            return false
        }
        updateSpirituality(currentSpirituality.current - amount)
        return true
    }

    /**
     * 恢复灵性
     * @param amount 恢复量
     */
    fun restoreSpirituality(amount: Int) {
        val currentSpirituality = _gameState.value.player.spirituality
        val newCurrent = minOf(
            currentSpirituality.current + amount,
            currentSpirituality.max
        )
        updateSpirituality(newCurrent)
    }

    /**
     * 更新金钱
     */
    fun updateMoney(goldPounds: Int, soles: Int, pence: Int) {
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                money = Money(goldPounds, soles, pence)
            )
        )
    }

    /**
     * 添加物品到背包
     */
    fun addItem(item: String) {
        val currentInventory = _gameState.value.player.inventory
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                inventory = currentInventory + item
            )
        )
    }

    /**
     * 从背包移除物品
     */
    fun removeItem(item: String): Boolean {
        val currentInventory = _gameState.value.player.inventory
        val index = currentInventory.indexOf(item)
        if (index == -1) return false
        
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                inventory = currentInventory.toMutableList().apply { removeAt(index) }
            )
        )
        return true
    }

    /**
     * 添加能力
     */
    fun addAbility(ability: Ability) {
        val currentAbilities = _gameState.value.player.abilities
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                abilities = currentAbilities + ability
            )
        )
    }

    /**
     * 添加知识
     */
    fun addKnowledge(knowledge: String) {
        val currentKnowledge = _gameState.value.player.knowledge
        if (knowledge in currentKnowledge) return
        
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                knowledge = currentKnowledge + knowledge
            )
        )
    }

    /**
     * 更新序列信息
     */
    fun updateSequence(sequenceInfo: SequenceInfo) {
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                currentSequence = sequenceInfo
            )
        )
    }

    /**
     * 更新理智值
     */
    fun updateSanity(madnessValue: Int, corruptionLevel: Int? = null) {
        val currentSanity = _gameState.value.player.sanity
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                sanity = Sanity(
                    madnessValue = madnessValue,
                    corruptionLevel = corruptionLevel ?: currentSanity.corruptionLevel
                )
            )
        )
    }

    /**
     * 添加状态效果
     */
    fun addStatusEffect(effect: String) {
        val currentEffects = _gameState.value.player.statusEffects
        if (effect in currentEffects) return
        
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                statusEffects = currentEffects + effect
            )
        )
    }

    /**
     * 移除状态效果
     */
    fun removeStatusEffect(effect: String) {
        val currentEffects = _gameState.value.player.statusEffects
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                statusEffects = currentEffects - effect
            )
        )
    }

    /**
     * 更新派系关系
     */
    fun updateFactionRelation(faction: String, status: String, reputation: Int = 0) {
        val currentRelations = _gameState.value.player.factionRelations
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                factionRelations = currentRelations + (faction to FactionRelation(status, reputation))
            )
        )
    }

    /**
     * 更新NPC关系
     */
    fun updateNpcRelation(npcName: String, relationValue: Int) {
        val currentRelations = _gameState.value.player.npcRelations
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                npcRelations = currentRelations + (npcName to relationValue)
            )
        )
    }

    /**
     * 添加任务
     */
    fun addQuest(quest: Quest) {
        val currentQuests = _gameState.value.world.openQuests
        _gameState.value = _gameState.value.copy(
            world = _gameState.value.world.copy(
                openQuests = currentQuests + quest
            )
        )
    }

    /**
     * 更新任务状态
     */
    fun updateQuestStatus(questId: String, status: String) {
        val currentQuests = _gameState.value.world.openQuests
        val updatedQuests = currentQuests.map { quest ->
            if (quest.id == questId) {
                quest.copy(status = status)
            } else {
                quest
            }
        }
        _gameState.value = _gameState.value.copy(
            world = _gameState.value.world.copy(openQuests = updatedQuests)
        )
    }

    /**
     * 更新NPC状态
     */
    fun updateNpcState(npcName: String, npcState: NpcState) {
        val currentNpcStates = _gameState.value.world.npcStates
        _gameState.value = _gameState.value.copy(
            world = _gameState.value.world.copy(
                npcStates = currentNpcStates + (npcName to npcState)
            )
        )
    }

    /**
     * 添加聊天消息
     */
    fun addChatMessage(message: ChatMessage) {
        val currentHistory = _gameState.value.chatHistory
        _gameState.value = _gameState.value.copy(
            chatHistory = currentHistory + message
        )
    }

    /**
     * 清空聊天历史
     */
    fun clearChatHistory() {
        _gameState.value = _gameState.value.copy(
            chatHistory = emptyList()
        )
    }

    /**
     * 初始化游戏
     */
    fun initializeGame(playerName: String) {
        _gameState.value = GameState(
            player = PlayerState(name = playerName),
            world = WorldState(),
            chatHistory = emptyList(),
            isInitialized = true
        )
    }

    /**
     * 重置游戏
     */
    fun resetGame() {
        _gameState.value = GameState()
    }

    /**
     * 序列化游戏状态为JSON字符串
     */
    fun serializeState(): String {
        return try {
            gson.toJson(_gameState.value)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 从JSON字符串反序列化游戏状态
     */
    fun deserializeState(jsonString: String): Boolean {
        return try {
            val state = gson.fromJson(jsonString, GameState::class.java)
            _gameState.value = state
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 加载游戏状态
     */
    fun loadState(state: GameState) {
        _gameState.value = state
    }

    /**
     * 获取玩家状态快照
     */
    fun getPlayerSnapshot(): PlayerState = _gameState.value.player

    /**
     * 获取世界状态快照
     */
    fun getWorldSnapshot(): WorldState = _gameState.value.world

    /**
     * 检查游戏是否已初始化
     */
    fun isGameInitialized(): Boolean = _gameState.value.isInitialized

    // =========================================
    // 新机制：命运的织网（因果与命运系统）
    // =========================================

    /**
     * 添加命运节点
     */
    fun addFateNode(fateNode: FateNode) {
        val currentFateNodes = _gameState.value.player.fateNodes
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                fateNodes = currentFateNodes + fateNode
            )
        )
    }

    /**
     * 触发命运节点
     */
    fun triggerFateNode(nodeId: String): Boolean {
        val currentFateNodes = _gameState.value.player.fateNodes
        val index = currentFateNodes.indexOfFirst { it.id == nodeId }
        if (index == -1) return false

        val updatedFateNodes = currentFateNodes.toMutableList()
        updatedFateNodes[index] = updatedFateNodes[index].copy(triggered = true)

        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                fateNodes = updatedFateNodes
            )
        )
        return true
    }

    /**
     * 获取未触发的命运节点
     */
    fun getUntriggeredFateNodes(): List<FateNode> {
        return _gameState.value.player.fateNodes.filter { !it.triggered }
    }

    // =========================================
    // 新机制：灵性视觉与信息分层
    // =========================================

    /**
     * 切换灵性视觉
     */
    fun toggleSpiritVision(): Boolean {
        val currentState = _gameState.value.player
        val spiritualityCost = 10
        
        if (!currentState.spiritVisionEnabled) {
            if (!consumeSpirituality(spiritualityCost)) {
                return false
            }
        }
        
        _gameState.value = _gameState.value.copy(
            player = currentState.copy(spiritVisionEnabled = !currentState.spiritVisionEnabled)
        )
        return true
    }

    /**
     * 检查灵性视觉是否开启
     */
    fun isSpiritVisionActive(): Boolean {
        return _gameState.value.player.spiritVisionEnabled
    }

    fun updateRolePlayProgress(sequence: String, progress: Int) {
        val currentProgress = _gameState.value.player.rolePlayProgress
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                rolePlayProgress = currentProgress + (sequence to progress)
            )
        )
    }

    fun addRolePlayProgress(sequence: String, amount: Int) {
        val currentProgress = _gameState.value.player.rolePlayProgress
        val currentValue = currentProgress[sequence] ?: 0
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                rolePlayProgress = currentProgress + (sequence to (currentValue + amount))
            )
        )
    }

    fun completeRolePlayTopic(topic: String) {
        val currentTopics = _gameState.value.player.rolePlayTopics
        val newTopics = currentTopics.filter { it != topic }
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(rolePlayTopics = newTopics)
        )
        addRolePlayProgress("占卜家", 15)
    }

    // =========================================
    // 新机制：隐秘聚会与阵营声望
    // =========================================

    /**
     * 更新阵营声望
     */
    fun updateFactionReputation(faction: String, reputationChange: Int) {
        val currentRelations = _gameState.value.player.factionRelations
        val currentRelation = currentRelations[faction] ?: FactionRelation("未知", 0)
        val newReputation = currentRelation.reputation + reputationChange
        
        // 根据声望值更新关系状态
        val newStatus = when {
            newReputation < -70 -> "敌对"
            newReputation < -30 -> "仇视"
            newReputation < 0 -> "冷淡"
            newReputation < 30 -> "中立"
            newReputation < 70 -> "友好"
            else -> "亲密"
        }
        
        val newRelation = FactionRelation(newStatus, newReputation)
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                factionRelations = currentRelations + (faction to newRelation)
            )
        )
    }

    /**
     * 获取阵营声望
     */
    fun getFactionReputation(faction: String): Int {
        return _gameState.value.player.factionRelations[faction]?.reputation ?: 0
    }

    // =========================================
    // 新机制：封印物（神奇物品）的"个性"
    // =========================================

    /**
     * 添加封印物
     */
    fun addSealedItem(sealedItem: SealedItem) {
        val currentSealedItems = _gameState.value.player.sealedItems
        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                sealedItems = currentSealedItems + sealedItem
            )
        )
    }

    /**
     * 激活封印物
     */
    fun activateSealedItem(itemName: String): Boolean {
        val currentSealedItems = _gameState.value.player.sealedItems
        val index = currentSealedItems.indexOfFirst { it.name == itemName }
        if (index == -1) return false

        val updatedSealedItems = currentSealedItems.toMutableList()
        updatedSealedItems[index] = updatedSealedItems[index].copy(active = true)

        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                sealedItems = updatedSealedItems
            )
        )
        return true
    }

    /**
     * 停用封印物
     */
    fun deactivateSealedItem(itemName: String): Boolean {
        val currentSealedItems = _gameState.value.player.sealedItems
        val index = currentSealedItems.indexOfFirst { it.name == itemName }
        if (index == -1) return false

        val updatedSealedItems = currentSealedItems.toMutableList()
        updatedSealedItems[index] = updatedSealedItems[index].copy(active = false)

        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                sealedItems = updatedSealedItems
            )
        )
        return true
    }

    /**
     * 更新疯狂具象化状态
     */
    fun updateMadnessManifestation() {
        val madnessValue = _gameState.value.player.sanity.madnessValue
        val newStatus = when {
            madnessValue < 50 -> "正常"
            madnessValue < 75 -> "内心独白"
            madnessValue < 90 -> "幻影同伴"
            else -> "支配"
        }

        _gameState.value = _gameState.value.copy(
            player = _gameState.value.player.copy(
                madnessManifestation = newStatus
            )
        )
    }

    /**
     * 获取当前疯狂具象化状态
     */
    fun getCurrentMadnessManifestation(): String {
        return _gameState.value.player.madnessManifestation
    }

    // =========================================
    // 辅助方法
    // =========================================

    /**
     * 处理玩家重大选择
     */
    fun handleMajorChoice(choiceDescription: String, potentialEffects: List<String>) {
        val fateNode = FateNode(
            id = "fate_${System.currentTimeMillis()}",
            type = "选择",
            description = choiceDescription,
            timestamp = System.currentTimeMillis(),
            potentialEffects = potentialEffects,
            triggered = false
        )
        addFateNode(fateNode)
    }

    /**
     * 处理关键物品接触
     */
    fun handleKeyItemContact(itemName: String, potentialEffects: List<String>) {
        val fateNode = FateNode(
            id = "fate_${System.currentTimeMillis()}",
            type = "物品",
            description = "接触关键物品：$itemName",
            timestamp = System.currentTimeMillis(),
            potentialEffects = potentialEffects,
            triggered = false
        )
        addFateNode(fateNode)
    }

    /**
     * 处理深刻恩怨
     */
    fun handleDeepEnmity(npcName: String, isPositive: Boolean, potentialEffects: List<String>) {
        val fateNode = FateNode(
            id = "fate_${System.currentTimeMillis()}",
            type = "恩怨",
            description = "与$npcName 结下${if (isPositive) "友谊" else "恩怨"}",
            timestamp = System.currentTimeMillis(),
            potentialEffects = potentialEffects,
            triggered = false
        )
        addFateNode(fateNode)
    }
}
