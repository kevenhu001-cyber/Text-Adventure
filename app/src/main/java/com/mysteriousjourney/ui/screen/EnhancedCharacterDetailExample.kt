package com.mysteriousjourney.ui.screen

import com.mysteriousjourney.domain.model.*

/**
 * 增强角色详情页面使用示例
 * 
 * 本文件展示如何创建和使用EnhancedCharacterData
 */

fun createSampleEnhancedCharacterData(): EnhancedCharacterData {
    val playerState = PlayerState(
        name = "克莱恩·莫雷蒂",
        surfaceIdentity = "历史系学生",
        currentSequence = SequenceInfo("占卜家", 9, 5),
        attributes = Attributes(
            strength = 12,
            intelligence = 18,
            agility = 14,
            perception = 16,
            willPower = 15,
            luck = 13,
            endurance = 11,
            dexterity = 14,
            memory = 17,
            analysis = 16,
            creativity = 15,
            charm = 14,
            persuasion = 13,
            intimidation = 10,
            leadership = 12
        ),
        spirituality = Spirituality(50, 100),
        healthStatus = "健康",
        sanity = Sanity(5, 0),
        money = Money(15, 12, 6),
        inventory = listOf(
            "占卜吊坠",
            "古老笔记本",
            "几枚硬币",
            "学生证",
            "左轮手枪",
            "银制子弹×5"
        ),
        abilities = listOf(
            Ability("基础占卜", "使用塔罗牌进行简单占卜", 1),
            Ability("灵视", "看到灵性世界的基础能力", 1),
            Ability("基础魔药学", "制作和饮用基础魔药", 1)
        ),
        knowledge = listOf(
            "穿越者记忆",
            "地球历史",
            "基础占卜知识"
        ),
        factionRelations = mapOf(
            "值夜者" to FactionRelation("未知", 0),
            "教会" to FactionRelation("中立", 0)
        ),
        dailyLog = listOf(
            DailyLog(
                timestamp = System.currentTimeMillis(),
                type = DailyLog.LogType.INVESTIGATION,
                title = "调查连续自杀案",
                description = "首次接触非凡案件，开始调查廷根市的连续自杀事件",
                impact = 10
            ),
            DailyLog(
                timestamp = System.currentTimeMillis() - 86400000,
                type = DailyLog.LogType.TRAINING,
                title = "基础占卜训练",
                description = "在宿舍练习基础占卜技巧，提升灵性感知",
                impact = 5
            )
        ),
        titles = listOf("占卜家"),
        achievements = listOf("首次接触非凡世界"),
        characterRelations = listOf(
            CharacterRelation(
                characterId = "邓恩",
                characterName = "邓恩·史密斯",
                relationType = CharacterRelation.RelationType.MENTOR,
                affection = 75,
                trust = 80,
                description = "值夜者小队队长，你的导师",
                firstMet = "1349年10月15日",
                lastInteraction = "1349年11月3日",
                status = "友好",
                notes = listOf("经验丰富，值得信赖")
            ),
            CharacterRelation(
                characterId = "奥黛丽",
                characterName = "奥黛丽·霍尔",
                relationType = CharacterRelation.RelationType.FRIEND,
                affection = 60,
                trust = 55,
                description = "大贵族小姐，塔罗会成员",
                firstMet = "1349年11月20日",
                lastInteraction = "1349年11月25日",
                status = "友好",
                notes = listOf("聪慧善良，值得信赖")
            )
        ),
        fateNodes = listOf(
            FateNode(
                id = "node_001",
                type = "命运",
                description = "成为占卜家序列非凡者",
                timestamp = System.currentTimeMillis(),
                potentialEffects = listOf("获得占卜家能力", "解锁灵视"),
                triggered = true
            )
        ),
        historyEvents = listOf(
            HistoryEvent(
                title = "穿越到诡秘世界",
                description = "从地球穿越到北大陆，成为克莱恩·莫雷蒂",
                timestamp = System.currentTimeMillis(),
                importance = 5
            ),
            HistoryEvent(
                title = "首次接触非凡世界",
                description = "通过神秘信件接触到占卜家序列",
                timestamp = System.currentTimeMillis() - 86400000,
                importance = 4
            )
        ),
        spiritVisionEnabled = true,
        sealedItems = emptyList(),
        madnessManifestation = "正常"
    )
    
    val health = HealthStatus(
        currentHealth = 100,
        maxHealth = 100,
        fatigue = 20,
        maxFatigue = 100,
        mentalState = HealthStatus.MentalState.NORMAL,
        sanity = 95,
        maxSanity = 100,
        stress = 15,
        maxStress = 100
    )
    
    val equippedItems = mapOf(
        Equipment.EquipmentType.ACCESSORY to Equipment(
            id = "item_001",
            name = "占卜吊坠",
            type = Equipment.EquipmentType.ACCESSORY,
            rarity = Equipment.Rarity.RARE,
            description = "古老的占卜工具，具有基础占卜功能",
            effects = listOf(
                Equipment.EquipmentEffect(
                    type = Equipment.EquipmentEffect.EffectType.SPIRITUALITY_MAX,
                    value = 20,
                    description = "灵性上限+20"
                )
            ),
            requirements = emptyList(),
            isEquipped = true
        )
    )
    
    val activeStatuses = listOf(
        GameStatus(
            id = "status_001",
            name = "专注",
            description = "精神高度集中，占卜成功率提升",
            type = GameStatus.StatusType.BUFF,
            duration = 3,
            effects = listOf(
                GameStatus.StatusEffect(
                    type = GameStatus.StatusEffect.EffectType.PERCEPTION_MOD,
                    value = 5,
                    description = "感知+5"
                )
            )
        )
    )
    
    val skillTree = listOf(
        SkillNode(
            id = "skill_001",
            name = "基础占卜",
            description = "使用塔罗牌进行简单占卜的能力",
            level = 1,
            maxLevel = 10,
            path = SkillNode.SkillPath.MYSTERY,
            unlocked = true
        ),
        SkillNode(
            id = "skill_002",
            name = "灵视",
            description = "看到灵性世界的基础能力",
            level = 1,
            maxLevel = 5,
            path = SkillNode.SkillPath.MYSTERY,
            unlocked = true
        ),
        SkillNode(
            id = "skill_003",
            name = "魔药学",
            description = "制作和饮用魔药的知识",
            level = 0,
            maxLevel = 10,
            path = SkillNode.SkillPath.MYSTERY,
            unlocked = false,
            requirements = listOf("基础占卜 Lv.5")
        )
    )
    
    val destinyPath = listOf(
        DestinyNode(
            id = "destiny_001",
            name = "占卜家之路",
            description = "踏上占卜家序列的晋升之路",
            stage = 1,
            totalStages = 10,
            currentEffect = "获得基础占卜能力",
            nextEffect = "解锁灵视能力",
            triggered = true,
            timestamp = System.currentTimeMillis()
        )
    )
    
    val mysteryPoints = listOf(
        MysteryPoint(
            id = "mystery_001",
            name = "廷根市连续自杀案",
            category = MysteryPoint.MysteryCategory.EVENT,
            description = "一系列离奇的自杀案件，背后隐藏着非凡力量的痕迹",
            discovered = true,
            clues = listOf(
                MysteryClue("clue_001", "现场留有黑色痕迹", discovered = true),
                MysteryClue("clue_002", "死者均无明显外伤", discovered = true)
            )
        )
    )
    
    val progressMetrics = ProgressMetrics(
        sequenceProgress = 5,
        sequenceGoal = 100,
        spiritualityRegenProgress = 50,
        spiritualityRegenGoal = 100,
        rolePlayProgress = 10,
        rolePlayGoal = 100,
        mysteryUnlocked = 1,
        mysteryTotal = 10
    )
    
    return EnhancedCharacterData(
        baseState = playerState,
        health = health,
        equippedItems = equippedItems,
        activeStatuses = activeStatuses,
        skillTree = skillTree,
        destinyPath = destinyPath,
        mysteryPoints = mysteryPoints,
        progressMetrics = progressMetrics
    )
}
