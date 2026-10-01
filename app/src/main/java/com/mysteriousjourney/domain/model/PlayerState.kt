package com.mysteriousjourney.domain.model

/**
 * 玩家状态数据类
 * 包含玩家的所有属性和状态信息
 */
data class PlayerState(
    val name: String = "旅行者",
    val surfaceIdentity: String = "普通学生",
    val currentSequence: SequenceInfo = SequenceInfo("占卜家", 0),
    val attributes: Attributes = Attributes(),
    val spirituality: Spirituality = Spirituality(100, 100),
    val healthStatus: String = "健康",
    val sanity: Sanity = Sanity(0, 0),
    val money: Money = Money(10, 0, 0),
    val inventory: List<String> = emptyList(),
    val detailedInventory: List<Equipment> = emptyList(),
    val abilities: List<Ability> = emptyList(),
    val knowledge: List<String> = emptyList(),
    val statusEffects: List<String> = emptyList(),
    val detailedStatusEffects: List<GameStatus> = emptyList(),
    val factionRelations: Map<String, FactionRelation> = emptyMap(),
    val npcRelations: Map<String, Int> = emptyMap(),
    val fateNodes: List<FateNode> = emptyList(),
    val historyEvents: List<HistoryEvent> = emptyList(),
    val spiritVisionEnabled: Boolean = false,
    val rolePlayProgress: Map<String, Int> = emptyMap(),
    val rolePlayTopics: List<String> = emptyList(),
    val sealedItems: List<SealedItem> = emptyList(),
    val madnessManifestation: String = "正常",
    val dailyLog: List<DailyLog> = emptyList(),
    val titles: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val characterRelations: List<CharacterRelation> = emptyList(),
    val skillTree: List<SkillNode> = emptyList(),
    val destinyPath: List<DestinyNode> = emptyList(),
    val mysteryPoints: List<MysteryPoint> = emptyList(),
    val progressMetrics: ProgressMetrics = ProgressMetrics()
)

/**
 * 角色核心属性
 */
data class Attributes(
    val strength: Int = 10,
    val intelligence: Int = 10,
    val agility: Int = 10,
    val perception: Int = 10,
    val willPower: Int = 10,
    val luck: Int = 10,
    val endurance: Int = 10,
    val dexterity: Int = 10,
    val memory: Int = 10,
    val analysis: Int = 10,
    val creativity: Int = 10,
    val charm: Int = 10,
    val persuasion: Int = 10,
    val intimidation: Int = 10,
    val leadership: Int = 10
)

/**
 * 历史事件/成就
 */
data class HistoryEvent(
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val importance: Int = 0 // 0-5
)

/**
 * 序列信息
 * @param name 序列名称
 * @param number 序列编号（9-0）
 * @param digestionProgress 消化进度（0-100）
 */
data class SequenceInfo(
    val name: String,
    val number: Int = 9,
    val digestionProgress: Int = 0
)

/**
 * 灵性值
 */
data class Spirituality(
    val current: Int,
    val max: Int
)

/**
 * 理智状态
 */
data class Sanity(
    val madnessValue: Int,
    val corruptionLevel: Int
)

/**
 * 金钱
 */
data class Money(
    val goldPounds: Int,
    val soles: Int,
    val pence: Int
)

/**
 * 能力
 */
data class Ability(
    val name: String,
    val description: String,
    val level: Int
)

/**
 * 阵营关系
 */
data class FactionRelation(
    val status: String,
    val reputation: Int
)

/**
 * 命运节点
 */
data class FateNode(
    val id: String,
    val type: String,
    val description: String,
    val timestamp: Long,
    val potentialEffects: List<String>,
    val triggered: Boolean
)

/**
 * 封印物
 */
data class SealedItem(
    val name: String,
    val description: String,
    val active: Boolean,
    val effects: List<String>
)