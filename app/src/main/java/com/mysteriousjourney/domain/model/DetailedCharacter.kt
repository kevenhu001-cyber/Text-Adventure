package com.mysteriousjourney.domain.model

/**
 * 角色关系网络
 */
data class CharacterRelationship(
    val targetCharacterId: String,
    val targetCharacterName: String,
    val relationshipType: RelationshipType,
    val relationshipLevel: Int = 0, // -100 到 100，负值为敌对，正值为友好
    val description: String = "",
    val knownSecrets: List<String> = emptyList(),
    val interactionHistory: List<String> = emptyList()
) {
    enum class RelationshipType {
        FAMILY,          // 亲属关系
        SUPERIOR,        // 上下级关系（上级）
        SUBORDINATE,     // 上下级关系（下级）
        FRIEND,           // 朋友关系
        ENEMY,            // 敌对关系
        RIVAL,            // 竞争关系
        MENTOR,           // 导师关系
        STUDENT,          // 学生关系
        LOVER,            // 恋人关系
        ACQUAINTANCE,     // 熟人关系
        ALLY,             // 盟友关系
        NEUTRAL,          // 中立关系
        COWORKER,         // 同事关系
        NEIGHBOR,         // 邻居关系
        BUSINESS_PARTNER   // 商业伙伴
    }
    
    fun getRelationshipDescription(): String {
        return when (relationshipType) {
            RelationshipType.FAMILY -> "亲属"
            RelationshipType.SUPERIOR -> "上级"
            RelationshipType.SUBORDINATE -> "下级"
            RelationshipType.FRIEND -> "朋友"
            RelationshipType.ENEMY -> "敌人"
            RelationshipType.RIVAL -> "竞争者"
            RelationshipType.MENTOR -> "导师"
            RelationshipType.STUDENT -> "学生"
            RelationshipType.LOVER -> "恋人"
            RelationshipType.ACQUAINTANCE -> "熟人"
            RelationshipType.ALLY -> "盟友"
            RelationshipType.NEUTRAL -> "中立"
            RelationshipType.COWORKER -> "同事"
            RelationshipType.NEIGHBOR -> "邻居"
            RelationshipType.BUSINESS_PARTNER -> "商业伙伴"
        }
    }
    
    fun getAttitude(): String {
        return when {
            relationshipLevel >= 80 -> "极度友好"
            relationshipLevel >= 50 -> "友好"
            relationshipLevel >= 20 -> "友善"
            relationshipLevel >= -20 -> "中立"
            relationshipLevel >= -50 -> "敌视"
            relationshipLevel >= -80 -> "仇恨"
            else -> "极度仇恨"
        }
    }
}

/**
 * 角色核心能力设定
 */
data class CharacterAbilities(
    val specialSkills: List<SpecialSkill> = emptyList(),
    val professionalKnowledge: List<ProfessionalKnowledge> = emptyList(),
    val physicalAttributes: PhysicalAttributes = PhysicalAttributes(),
    val mentalAttributes: MentalAttributes = MentalAttributes(),
    val sequencePathway: SequencePathway? = null,
    val extraordinaryAbilities: List<ExtraordinaryAbility> = emptyList()
) {
    data class SpecialSkill(
        val name: String,
        val description: String,
        val proficiencyLevel: Int, // 1-10
        val usageFrequency: UsageFrequency,
        val learnedFrom: String = "",
        val notableUses: List<String> = emptyList()
    ) {
        enum class UsageFrequency {
            DAILY,       // 日常使用
            WEEKLY,      // 每周使用
            MONTHLY,     // 每月使用
            RARELY,      // 很少使用
            EMERGENCY     // 紧急情况使用
        }
        
        fun getProficiencyDescription(): String {
            return when (proficiencyLevel) {
                in 1..2 -> "初学"
                in 3..4 -> "基础"
                in 5..6 -> "熟练"
                in 7..8 -> "精通"
                in 9..9 -> "大师"
                else -> "传奇"
            }
        }
    }
    
    data class ProfessionalKnowledge(
        val field: String,
        val description: String,
        val expertiseLevel: Int, // 1-10
        val educationBackground: String = "",
        val practicalExperience: Int = 0 // 年数
    ) {
        fun getExpertiseDescription(): String {
            return when (expertiseLevel) {
                in 1..2 -> "初学者"
                in 3..4 -> "基础了解"
                in 5..6 -> "专业水平"
                in 7..8 -> "专家水平"
                in 9..9 -> "权威专家"
                else -> "世界级权威"
            }
        }
    }
    
    data class PhysicalAttributes(
        val strength: Int = 5,        // 力量 1-10
        val agility: Int = 5,         // 敏捷 1-10
        val endurance: Int = 5,       // 耐力 1-10
        val vitality: Int = 5,        // 体力 1-10
        val dexterity: Int = 5,       // 灵巧 1-10
        val appearance: Int = 5,       // 外貌 1-10
        val specialTraits: List<String> = emptyList() // 特殊体质特征
    ) {
        fun getAttributeLevel(attribute: Int): String {
            return when (attribute) {
                in 1..2 -> "很差"
                in 3..4 -> "较差"
                in 5..6 -> "一般"
                in 7..8 -> "良好"
                in 9..9 -> "优秀"
                else -> "卓越"
            }
        }
    }
    
    data class MentalAttributes(
        val intelligence: Int = 5,     // 智力 1-10
        val wisdom: Int = 5,          // 智慧 1-10
        val perception: Int = 5,      // 感知 1-10
        val willpower: Int = 5,       // 意志力 1-10
        val memory: Int = 5,          // 记忆力 1-10
        val creativity: Int = 5,       // 创造力 1-10
        val mentalStability: Int = 5,  // 精神稳定性 1-10
        val specialMentalTraits: List<String> = emptyList() // 特殊精神特征
    ) {
        fun getAttributeLevel(attribute: Int): String {
            return when (attribute) {
                in 1..2 -> "很差"
                in 3..4 -> "较差"
                in 5..6 -> "一般"
                in 7..8 -> "良好"
                in 9..9 -> "优秀"
                else -> "卓越"
            }
        }
    }
    
    data class SequencePathway(
        val pathwayName: String,
        val sequenceNumber: Int,
        val sequenceName: String,
        val digestionProgress: Int = 0, // 0-100
        val abilities: List<String> = emptyList(),
        val weaknesses: List<String> = emptyList()
    ) {
        fun getSequenceRank(): String {
            return when (sequenceNumber) {
                9 -> "序列9"
                8 -> "序列8"
                7 -> "序列7"
                6 -> "序列6"
                5 -> "序列5"
                4 -> "序列4"
                3 -> "序列3"
                2 -> "序列2"
                1 -> "序列1"
                0 -> "序列0（真神）"
                else -> "未知序列"
            }
        }
    }
    
    data class ExtraordinaryAbility(
        val name: String,
        val description: String,
        val source: String, // 来源：魔药、仪式、诅咒、天赋等
        val powerLevel: Int, // 1-10
        val usageCost: String,
        val cooldown: String,
        val limitations: List<String> = emptyList(),
        val sideEffects: List<String> = emptyList()
    )
}

/**
 * 角色详细背景故事
 */
data class DetailedCharacterBackground(
    val basicInfo: BasicInfo,
    val familyBackground: FamilyBackground,
    val educationHistory: EducationHistory,
    val careerHistory: CareerHistory,
    val majorLifeEvents: List<LifeEvent>,
    val personalityTraits: List<PersonalityTrait>,
    val secrets: List<Secret>,
    val goalsAndMotivations: List<GoalAndMotivation>
) {
    data class BasicInfo(
        val fullName: String,
        val aliases: List<String> = emptyList(),
        val age: Int,
        val birthplace: String,
        val currentResidence: String,
        val occupation: String,
        val socialClass: SocialClass,
        val economicStatus: EconomicStatus,
        val reputation: Reputation
    ) {
        enum class SocialClass {
            NOBILITY,      // 贵族
            UPPER_CLASS,    // 上流社会
            MIDDLE_CLASS,   // 中产阶级
            WORKING_CLASS,  // 工人阶级
            LOWER_CLASS,     // 下层阶级
            UNDERCLASS       // 底层阶级
        }
        
        enum class EconomicStatus {
            WEALTHY,        // 富有
            COMFORTABLE,    // 宽裕
            MIDDLE_INCOME,   // 中等收入
            LOW_INCOME,      // 低收入
            POOR,           // 贫困
            DESTITUTE       // 极度贫困
        }
        
        enum class Reputation {
            RENOWNED,       // 著名
            RESPECTED,      // 受尊敬
            NEUTRAL,        // 中立
            QUESTIONABLE,    // 有争议
            NOTORIOUS,      // 声名狼藉
            INFAMOUS        // 声名扫地
        }
    }
    
    data class FamilyBackground(
        val familyName: String,
        val familyStatus: FamilyStatus,
        val parentalInfo: ParentalInfo,
        val siblings: List<SiblingInfo> = emptyList(),
        val extendedFamily: List<ExtendedFamilyMember> = emptyList(),
        val familyReputation: String,
        val familySecrets: List<String> = emptyList()
    ) {
        enum class FamilyStatus {
            NOBLE_FAMILY,     // 贵族家庭
            MERCHANT_FAMILY,   // 商人家庭
            SCHOLAR_FAMILY,    // 学者家庭
            MILITARY_FAMILY,   // 军人家庭
            WORKING_FAMILY,    // 工人家庭
            FARMING_FAMILY,    // 农民家庭
            ORPHAN,           // 孤儿
            UNKNOWN           // 未知
        }
        
        data class ParentalInfo(
            val fatherInfo: ParentInfo? = null,
            val motherInfo: ParentInfo? = null,
            val guardianInfo: ParentInfo? = null
        ) {
            data class ParentInfo(
                val name: String,
                val occupation: String,
                val status: String, // 在世、去世、失踪等
                val relationship: String // 与角色的关系
            )
        }
        
        data class SiblingInfo(
            val name: String,
            val age: Int,
            val gender: String,
            val occupation: String,
            val relationship: String
        )
        
        data class ExtendedFamilyMember(
            val name: String,
            val relation: String, // 叔叔、阿姨、表亲等
            val occupation: String,
            val contact: String // 联系频率
        )
    }
    
    data class EducationHistory(
        val formalEducation: List<FormalEducation> = emptyList(),
        val informalEducation: List<InformalEducation> = emptyList(),
        val specialTraining: List<SpecialTraining> = emptyList(),
        val academicAchievements: List<String> = emptyList()
    ) {
        data class FormalEducation(
            val institution: String,
            val degree: String,
            val field: String,
            val yearsAttended: String,
            val graduationStatus: String,
            val notableAchievements: List<String> = emptyList()
        )
        
        data class InformalEducation(
            val type: String, // 自学、师徒制、私人教师等
            val subject: String,
            val duration: String,
            val instructor: String = "",
            val skillsGained: List<String> = emptyList()
        )
        
        data class SpecialTraining(
            val trainingType: String,
            val provider: String,
            val duration: String,
            val skillsLearned: List<String>,
            val certification: String = ""
        )
    }
    
    data class CareerHistory(
        val currentJob: JobInfo,
        val previousJobs: List<JobInfo> = emptyList(),
        val careerAchievements: List<String> = emptyList(),
        val careerFailures: List<String> = emptyList(),
        val professionalReputation: String
    ) {
        data class JobInfo(
            val position: String,
            val employer: String,
            val duration: String,
            val responsibilities: List<String>,
            val achievements: List<String> = emptyList(),
            val reasonForLeaving: String = ""
        )
    }
    
    data class LifeEvent(
        val title: String,
        val age: Int,
        val description: String,
        val impact: ImpactLevel,
        val consequences: List<String>,
        val relatedCharacters: List<String> = emptyList()
    ) {
        enum class ImpactLevel {
            LIFE_CHANGING,  // 改变人生
            SIGNIFICANT,     // 重大影响
            MODERATE,        // 中等影响
            MINOR,           // 轻微影响
            TRIVIAL          // 微不足道
        }
    }
    
    data class PersonalityTrait(
        val trait: String,
        val description: String,
        val intensity: Int, // 1-10
        val manifestation: String // 如何表现
    )
    
    data class Secret(
        val description: String,
        val severity: SeverityLevel,
        val whoKnows: List<String> = emptyList(),
        val consequenceIfRevealed: String,
        val relatedTo: List<String> = emptyList()
    ) {
        enum class SeverityLevel {
            TRIVIAL,      // 微不足道
            MINOR,         // 轻微
            MODERATE,      // 中等
            SERIOUS,        // 严重
            CRITICAL,       // 关键
            CATASTROPHIC    // 灾难性
        }
    }
    
    data class GoalAndMotivation(
        val goal: String,
        val motivation: String,
        val priority: Priority,
        val timeline: String,
        val obstacles: List<String> = emptyList(),
        val resourcesNeeded: List<String> = emptyList()
    ) {
        enum class Priority {
            SURVIVAL,      // 生存
            URGENT,        // 紧急
            IMPORTANT,      // 重要
            DESIRABLE,      // 期望
            OPTIONAL        // 可选
        }
    }
}

/**
 * 角色随身物品配置
 */
data class CharacterInventory(
    val equipment: Equipment,
    val personalItems: List<PersonalItem>,
    val specialItems: List<SpecialItem>,
    val consumables: List<Consumable>,
    val documents: List<Document>,
    val currency: Currency
) {
    data class Equipment(
        val weapons: List<Weapon>,
        val armor: List<Armor>,
        val tools: List<Tool>,
        val accessories: List<Accessory>,
        val clothing: List<Clothing>
    ) {
        data class Weapon(
            val name: String,
            val type: WeaponType,
            val description: String,
            val damage: String,
            val range: String,
            val specialProperties: List<String> = emptyList(),
            val condition: Condition,
            val origin: String = ""
        ) {
            enum class WeaponType {
                MELEE,      // 近战武器
                RANGED,      // 远程武器
                FIREARM,     // 火器
                MAGICAL,     // 魔法武器
                CONCEALABLE  // 隐藏武器
            }
            
            enum class Condition {
                PRISTINE,    // 完好如新
                EXCELLENT,   // 优秀
                GOOD,        // 良好
                WORN,        // 磨损
                DAMAGED,     // 损坏
                BROKEN       // 破碎
            }
        }
        
        data class Armor(
            val name: String,
            val type: ArmorType,
            val description: String,
            val protection: String,
            val coverage: String,
            val specialProperties: List<String> = emptyList(),
            val condition: Condition,
            val comfort: Int // 1-10
        ) {
            enum class ArmorType {
                LIGHT,       // 轻甲
                MEDIUM,      // 中甲
                HEAVY,       // 重甲
                CLOTHING,    // 衣物
                MAGICAL      // 魔法护甲
            }
            
            enum class Condition {
                PRISTINE, EXCELLENT, GOOD, WORN, DAMAGED, BROKEN
            }
        }
        
        data class Tool(
            val name: String,
            val type: ToolType,
            val description: String,
            val uses: List<String>,
            val quality: Quality,
            val maintenance: String = ""
        ) {
            enum class ToolType {
                INVESTIGATIVE,  // 侦查工具
                MECHANICAL,     // 机械工具
                MEDICAL,        // 医疗工具
                SURVIVAL,       // 生存工具
                CRAFTING,       // 制作工具
                MAGICAL         // 魔法工具
            }
            
            enum class Quality {
                POOR, FAIR, GOOD, EXCELLENT, MASTERWORK
            }
        }
        
        data class Accessory(
            val name: String,
            val type: AccessoryType,
            val description: String,
            val effects: List<String>,
            val condition: Condition,
            val significance: String = ""
        ) {
            enum class AccessoryType {
                JEWELRY,       // 珠宝
                AMULET,        // 护身符
                RING,           // 戒指
                POCKET_WATCH,    // 怀表
                GLASSES,        // 眼镜
                CANE,           // 手杖
                OTHER           // 其他
            }
            
            enum class Condition {
                PRISTINE, EXCELLENT, GOOD, WORN, DAMAGED, BROKEN
            }
        }
        
        data class Clothing(
            val name: String,
            val type: ClothingType,
            val description: String,
            val material: String,
            val quality: Quality,
            val specialFeatures: List<String> = emptyList(),
            val condition: Condition
        ) {
            enum class ClothingType {
                OUTERWEAR,     // 外套
                UNDERGARMENTS,  // 内衣
                FOOTWEAR,       // 鞋子
                HEADWEAR,       // 头饰
                GLOVES,         // 手套
                ACCESSORIES,     // 配饰
                UNIFORM         // 制服
            }
            
            enum class Quality {
                POOR, FAIR, GOOD, EXCELLENT, LUXURY
            }
            
            enum class Condition {
                PRISTINE, EXCELLENT, GOOD, WORN, DAMAGED, TORN
            }
        }
    }
    
    data class PersonalItem(
        val name: String,
        val description: String,
        val category: ItemCategory,
        val sentimentalValue: Int, // 1-10
        val practicalUse: String,
        val origin: String = "",
        val condition: String
    ) {
        enum class ItemCategory {
            KEEPSAKE,      // 纪念品
            HOBBY,         // 爱好用品
            DAILY_USE,     // 日常用品
            LUXURY,        // 奢侈品
            SENTIMENTAL,   // 情感物品
            UTILITY        // 实用品
        }
    }
    
    data class SpecialItem(
        val name: String,
        val description: String,
        val itemType: SpecialItemType,
        val powers: List<String>,
        val restrictions: List<String> = emptyList(),
        val activationMethod: String,
        val origin: String,
        val curseOrBlessing: String = ""
    ) {
        enum class SpecialItemType {
            ARTIFACT,       // 神器
            RELIC,          // 圣物
                CURSED_ITEM,    // 诅咒物品
                BLESSED_ITEM,   // 祝福物品
                MAGICAL_TOOL,   // 魔法工具
                DIVINATION_TOOL, // 占卜工具
                ALCHEMY_ITEM,   // 炼金物品
                UNIQUE_ITEM     // 独特物品
        }
    }
    
    data class Consumable(
        val name: String,
        val type: ConsumableType,
        val description: String,
        val effects: List<String>,
        val duration: String,
        val sideEffects: List<String> = emptyList(),
        val quantity: Int,
        val quality: Quality
    ) {
        enum class ConsumableType {
            POTION,         // 药水
            FOOD,           // 食物
            DRINK,          // 饮料
            MEDICINE,       // 药品
            POISON,         // 毒药
            ANTIDOTE,       // 解毒剂
            STIMULANT,      // 兴奋剂
            SEDATIVE        // 镇静剂
        }
        
        enum class Quality {
            POOR, FAIR, GOOD, EXCELLENT, MASTERWORK
        }
    }
    
    data class Document(
        val name: String,
        val type: DocumentType,
        val description: String,
        val contents: String,
        val importance: Importance,
        val authenticity: Authenticity,
        val relatedInformation: List<String> = emptyList()
    ) {
        enum class DocumentType {
            IDENTIFICATION,  // 身份证明
            CERTIFICATE,    // 证书
            LETTER,         // 信件
            DIARY,          // 日记
            RESEARCH_NOTE,   // 研究笔记
            MAP,            // 地图
            CONTRACT,        // 合同
            PERMIT,         // 许可证
            OTHER           // 其他
        }
        
        enum class Importance {
            TRIVIAL,        // 微不足道
            MINOR,          // 轻微重要
            MODERATE,        // 中等重要
            IMPORTANT,       // 重要
            CRITICAL,        // 关键
            VITAL           // 至关重要
        }
        
        enum class Authenticity {
            GENUINE,        // 真实
            FORGERY,        // 伪造
            SUSPICIOUS,      // 可疑
            UNKNOWN         // 未知
        }
    }
    
    data class Currency(
        val goldPounds: Int = 0,
        val silverSoles: Int = 0,
        val copperPence: Int = 0,
        val otherCurrencies: Map<String, Int> = emptyMap(),
        val valuableItems: List<String> = emptyList()
    ) {
        fun getTotalInPence(): Int {
            return goldPounds * 240 + silverSoles * 12 + copperPence
        }
        
        fun getFormattedAmount(): String {
            return when {
                goldPounds > 0 -> "${goldPounds}金镑 ${silverSoles}苏勒 ${copperPence}便士"
                silverSoles > 0 -> "${silverSoles}苏勒 ${copperPence}便士"
                copperPence > 0 -> "${copperPence}便士"
                else -> "无"
            }
        }
    }
}

/**
 * 完整的角色详细信息
 */
data class DetailedCharacter(
    val id: String,
    val basicInfo: DetailedCharacterBackground.BasicInfo,
    val relationships: List<CharacterRelationship>,
    val abilities: CharacterAbilities,
    val background: DetailedCharacterBackground,
    val inventory: CharacterInventory,
    val currentStatus: CharacterStatus,
    val plotRelevance: PlotRelevance
) {
    data class CharacterStatus(
        val healthStatus: HealthStatus,
        val mentalStatus: MentalStatus,
        val location: String,
        val currentActivity: String,
        val immediateGoals: List<String>,
        val emotionalState: String,
        val physicalCondition: String
    ) {
        enum class HealthStatus {
            EXCELLENT,    // 极好
            GOOD,          // 良好
            FAIR,          // 一般
            POOR,          // 较差
            INJURED,       // 受伤
            CRITICAL,      // 危急
            DYING,         // 濒死
            DEAD           // 死亡
        }
        
        enum class MentalStatus {
            STABLE,        // 稳定
            CALM,          // 平静
            ANXIOUS,       // 焦虑
            STRESSED,       // 压力
            DEPRESSED,     // 抑郁
            AGITATED,      // 激动
            CONFUSED,       // 困惑
            PARANOID,      // 偏执
            INSANE         // 疯狂
        }
    }
    
    data class PlotRelevance(
        val roleInStory: StoryRole,
        val importanceToPlot: PlotImportance,
        val keyPlotPoints: List<String>,
        val playerInteractions: List<String>,
        val storyArcs: List<StoryArc>
    ) {
        enum class StoryRole {
            PROTAGONIST,     // 主角
            ANTAGONIST,      // 反派
            MENTOR,          // 导师
            ALLY,            // 盟友
            NEUTRAL,         // 中立角色
            VILLAIN,         // 恶棍
            VICTIM,          // 受害者
            WITNESS,         // 目击者
            INFORMATION_BROKER, // 信息贩子
            QUEST_GIVER,     // 任务发布者
            OBSTACLE,        // 障碍
            SUPPORTING        // 配角
        }
        
        enum class PlotImportance {
            CRITICAL,        // 关键
            MAJOR,           // 主要
            MODERATE,        // 中等
            MINOR,           // 次要
            INCIDENTAL        // 偶然
        }
        
        data class StoryArc(
            val arcName: String,
            val description: String,
            val characterRole: String,
            val resolution: String = ""
        )
    }
}
