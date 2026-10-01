package com.mysteriousjourney.domain.model

/**
 * 增强角色数据适配器
 * 将 PlayerState + GameState 转换为 EnhancedCharacterData
 * 用于对接 EnhancedCharacterDetailScreen
 */
object EnhancedCharacterAdapter {

    /**
     * 从 PlayerState 构建 EnhancedCharacterData
     * @param playerState 玩家状态
     * @param gameState 完整游戏状态（可选，用于获取额外信息）
     */
    fun fromPlayerState(
        playerState: PlayerState,
        gameState: GameState? = null
    ): EnhancedCharacterData {
        val health = buildHealth(playerState)
        val equippedItems = buildEquippedItems(playerState)
        val activeStatuses = buildActiveStatuses(playerState)
        val skillTree = buildSkillTree(playerState)
        val destinyPath = buildDestinyPath(playerState)
        val mysteryPoints = buildMysteryPoints(playerState)
        val progressMetrics = buildProgressMetrics(playerState, gameState)
        val characterRelations = playerState.characterRelations

        return EnhancedCharacterData(
            baseState = playerState,
            health = health,
            equippedItems = equippedItems,
            activeStatuses = activeStatuses,
            skillTree = skillTree,
            destinyPath = destinyPath,
            mysteryPoints = mysteryPoints,
            progressMetrics = progressMetrics,
            titles = playerState.titles,
            achievements = playerState.achievements,
            characterRelations = characterRelations
        )
    }

    private fun buildHealth(playerState: PlayerState): HealthStatus {
        val maxHealth = playerState.attributes.strength * 10
        val currentHealth = when (playerState.healthStatus) {
            "健康" -> maxHealth
            "受伤" -> (maxHealth * 0.6).toInt()
            "重伤" -> (maxHealth * 0.25).toInt()
            "濒死" -> 1
            else -> (maxHealth * 0.8).toInt()
        }
        return HealthStatus(
            currentHealth = currentHealth.coerceAtLeast(1),
            maxHealth = maxHealth,
            mentalState = when {
                playerState.sanity.madnessValue > 80 -> HealthStatus.MentalState.CRITICAL
                playerState.sanity.madnessValue > 60 -> HealthStatus.MentalState.UNSTABLE
                playerState.sanity.madnessValue > 40 -> HealthStatus.MentalState.PARANOID
                playerState.sanity.madnessValue > 20 -> HealthStatus.MentalState.ANXIOUS
                playerState.sanity.madnessValue > 10 -> HealthStatus.MentalState.STRESSED
                else -> HealthStatus.MentalState.NORMAL
            }
        )
    }

    private fun buildEquippedItems(playerState: PlayerState): Map<Equipment.EquipmentType, Equipment?> {
        val map = mutableMapOf<Equipment.EquipmentType, Equipment?>()
        // 尝试将背包中的物品映射到装备栏
        val detailedEquips = playerState.detailedInventory
        map[Equipment.EquipmentType.WEAPON] = detailedEquips.find {
            it.type == Equipment.EquipmentType.WEAPON || 
            it.name.contains("剑") || it.name.contains("匕首") || it.name.contains("枪")
        }?.copy(isEquipped = true)
        map[Equipment.EquipmentType.ARMOR] = detailedEquips.find {
            it.type == Equipment.EquipmentType.ARMOR ||
            it.name.contains("护") || it.name.contains("甲") || it.name.contains("服")
        }?.copy(isEquipped = true)
        map[Equipment.EquipmentType.ACCESSORY] = detailedEquips.find {
            it.type == Equipment.EquipmentType.ACCESSORY ||
            it.name.contains("坠") || it.name.contains("环") || it.name.contains("饰")
        }?.copy(isEquipped = true)
        map[Equipment.EquipmentType.ARTIFACT] = detailedEquips.find {
            it.type == Equipment.EquipmentType.ARTIFACT ||
            it.name.contains("封印") || it.name.contains("神秘")
        }?.copy(isEquipped = true)
        return map
    }

    private fun buildActiveStatuses(playerState: PlayerState): List<GameStatus> {
        if (playerState.detailedStatusEffects.isNotEmpty()) {
            return playerState.detailedStatusEffects
        }
        // 从字符串状态列表构建
        return playerState.statusEffects.map { effectName ->
            GameStatus(
                id = effectName.hashCode().toString(),
                name = effectName,
                description = effectName,
                type = GameStatus.StatusType.NEUTRAL
            )
        }
    }

    private fun buildSkillTree(playerState: PlayerState): List<SkillNode> {
        if (playerState.skillTree.isNotEmpty()) {
            return playerState.skillTree
        }
        return playerState.abilities.map { ability ->
            SkillNode(
                id = ability.name.hashCode().toString(),
                name = ability.name,
                description = ability.description,
                level = ability.level,
                maxLevel = 10,
                path = SkillNode.SkillPath.SPECIAL,
                unlocked = true
            )
        }
    }

    private fun buildDestinyPath(playerState: PlayerState): List<DestinyNode> {
        if (playerState.destinyPath.isNotEmpty()) {
            return playerState.destinyPath
        }
        return playerState.fateNodes.map { node ->
            DestinyNode(
                id = node.id,
                name = node.description.take(20),
                description = node.description,
                stage = if (node.triggered) 1 else 0,
                totalStages = 5,
                currentEffect = if (node.triggered) "已触发" else "未触发",
                nextEffect = node.potentialEffects.firstOrNull() ?: "未知",
                triggered = node.triggered,
                timestamp = node.timestamp
            )
        }
    }

    private fun buildMysteryPoints(playerState: PlayerState): List<MysteryPoint> {
        if (playerState.mysteryPoints.isNotEmpty()) {
            return playerState.mysteryPoints
        }
        return listOf(
            MysteryPoint(
                id = "1",
                name = playerState.currentSequence.name,
                category = MysteryPoint.MysteryCategory.MYTHOS,
                description = "当前非凡途径：${playerState.currentSequence.name}（序列${playerState.currentSequence.number}）",
                discovered = true
            )
        )
    }

    private fun buildProgressMetrics(playerState: PlayerState, gameState: GameState?): ProgressMetrics {
        return playerState.progressMetrics.copy(
            sequenceProgress = playerState.currentSequence.digestionProgress,
            sequenceGoal = 100,
            rolePlayProgress = playerState.currentSequence.digestionProgress
        )
    }
}
