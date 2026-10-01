package com.mysteriousjourney.domain.model

data class GameStatus(
    val id: String,
    val name: String,
    val description: String,
    val type: StatusType,
    val duration: Int = -1,
    val remainingTurns: Int = -1,
    val effects: List<StatusEffect> = emptyList(),
    val source: String = "",
    val isPermanent: Boolean = false
) {
    enum class StatusType {
        BUFF,
        DEBUFF,
        NEUTRAL,
        SPECIAL,
        RELATIONSHIP
    }
    
    data class StatusEffect(
        val type: EffectType,
        val value: Int,
        val description: String = ""
    ) {
        enum class EffectType {
            SPIRITUALITY_MOD,
            MADNESS_MOD,
            COMBAT_MOD,
            SOCIAL_MOD,
            STEALTH_MOD,
            PERCEPTION_MOD,
            SPECIAL
        }
    }
}

data class Equipment(
    val id: String,
    val name: String,
    val type: EquipmentType,
    val rarity: Rarity,
    val description: String,
    val effects: List<EquipmentEffect>,
    val requirements: List<EquipmentRequirement> = emptyList(),
    val isEquipped: Boolean = false,
    val icon: String = ""
) {
    enum class EquipmentType {
        WEAPON,
        ARMOR,
        ACCESSORY,
        ARTIFACT,
        CONSUMABLE,
        SPECIAL
    }
    
    enum class Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY,
        DIVINE
    }
    
    data class EquipmentEffect(
        val type: EffectType,
        val value: Int,
        val description: String = ""
    ) {
        enum class EffectType {
            SPIRITUALITY_MAX,
            SPIRITUALITY_REGEN,
            MADNESS_RESIST,
            COMBAT_POWER,
            DEFENSE,
            PERCEPTION,
            STEALTH,
            SOCIAL,
            SPECIAL
        }
    }
    
    data class EquipmentRequirement(
        val type: RequirementType,
        val value: Any
    ) {
        enum class RequirementType {
            SEQUENCE_LEVEL,
            PATHWAY,
            SPIRITUALITY,
            FACTION_RELATION
        }
    }
}

data class HealthStatus(
    val currentHealth: Int = 100,
    val maxHealth: Int = 100,
    val fatigue: Int = 0,
    val maxFatigue: Int = 100,
    val injuries: List<Injury> = emptyList(),
    val diseases: List<Disease> = emptyList(),
    val mentalState: MentalState = MentalState.NORMAL,
    val sanity: Int = 100,
    val maxSanity: Int = 100,
    val stress: Int = 0,
    val maxStress: Int = 100
) {
    enum class MentalState {
        NORMAL,
        STRESSED,
        ANXIOUS,
        PARANOID,
        UNSTABLE,
        CRITICAL
    }
    
    data class Injury(
        val id: String,
        val name: String,
        val severity: Severity,
        val description: String,
        val healingTime: Int,
        val effects: List<GameStatus.StatusEffect>
    ) {
        enum class Severity {
            MINOR,
            MODERATE,
            SEVERE,
            CRITICAL
        }
    }
    
    data class Disease(
        val id: String,
        val name: String,
        val description: String,
        val duration: Int,
        val effects: List<GameStatus.StatusEffect>
    )
    
    fun getHealthPercentage(): Float = currentHealth.toFloat() / maxHealth
    fun getFatiguePercentage(): Float = fatigue.toFloat() / maxFatigue
    fun isHealthy(): Boolean = currentHealth == maxHealth && fatigue == 0 && injuries.isEmpty() && diseases.isEmpty()
}

data class CharacterRelation(
    val characterId: String,
    val characterName: String,
    val relationType: RelationType,
    val affection: Int = 0,
    val trust: Int = 0,
    val description: String = "",
    val firstMet: String = "",
    val lastInteraction: String = "",
    val status: String = "",
    val notes: List<String> = emptyList()
) {
    enum class RelationType {
        ALLY,
        ENEMY,
        NEUTRAL,
        FRIEND,
        RIVAL,
        MENTOR,
        STUDENT,
        FAMILY,
        LOVER,
        ACQUAINTANCE
    }
    
    fun getOverallRelation(): Int = (affection + trust) / 2
}

data class CharacterBackground(
    val surfaceIdentity: String,
    val hiddenIdentity: String = "",
    val surfaceStory: String = "",
    val hiddenStory: String = "",
    val birthplace: String = "",
    val age: Int = 0,
    val occupation: String = "",
    val education: String = "",
    val familyBackground: String = "",
    val secrets: List<String> = emptyList()
)

data class ExtendedPlayerState(
    val baseState: PlayerState,
    val background: CharacterBackground = CharacterBackground(
        surfaceIdentity = baseState.surfaceIdentity
    ),
    val health: HealthStatus = HealthStatus(),
    val equippedItems: Map<Equipment.EquipmentType, Equipment?> = emptyMap(),
    val inventoryEquipments: List<Equipment> = emptyList(),
    val activeStatuses: List<GameStatus> = emptyList(),
    val characterRelations: List<CharacterRelation> = emptyList(),
    val achievements: List<String> = emptyList(),
    val titles: List<String> = emptyList(),
    val dailyLog: List<DailyLog> = emptyList(),
    val skillTree: List<SkillNode> = emptyList(),
    val destinyPath: List<DestinyNode> = emptyList(),
    val mysteryPoints: List<MysteryPoint> = emptyList(),
    val progressMetrics: ProgressMetrics = ProgressMetrics()
) {
    fun getTotalCombatPower(): Int {
        var power = 100
        equippedItems.values.filterNotNull().forEach { equipment ->
            equipment.effects.filter { 
                it.type == Equipment.EquipmentEffect.EffectType.COMBAT_POWER 
            }.forEach { power += it.value }
        }
        activeStatuses.forEach { status ->
            status.effects.filter { 
                it.type == GameStatus.StatusEffect.EffectType.COMBAT_MOD 
            }.forEach { power += it.value }
        }
        return power
    }
    
    fun getTotalDefense(): Int {
        var defense = 0
        equippedItems.values.filterNotNull().forEach { equipment ->
            equipment.effects.filter { 
                it.type == Equipment.EquipmentEffect.EffectType.DEFENSE 
            }.forEach { defense += it.value }
        }
        return defense
    }
    
    fun getEffectiveSpiritualityMax(): Int {
        var max = baseState.spirituality.max
        equippedItems.values.filterNotNull().forEach { equipment ->
            equipment.effects.filter { 
                it.type == Equipment.EquipmentEffect.EffectType.SPIRITUALITY_MAX 
            }.forEach { max += it.value }
        }
        return max
    }
    
    fun getMadnessResistance(): Int {
        var resistance = 0
        equippedItems.values.filterNotNull().forEach { equipment ->
            equipment.effects.filter { 
                it.type == Equipment.EquipmentEffect.EffectType.MADNESS_RESIST 
            }.forEach { resistance += it.value }
        }
        activeStatuses.forEach { status ->
            status.effects.filter { 
                it.type == GameStatus.StatusEffect.EffectType.MADNESS_MOD 
            }.forEach { resistance += it.value }
        }
        return resistance
    }
}

data class DailyLog(
    val timestamp: Long,
    val type: LogType,
    val title: String,
    val description: String,
    val impact: Int = 0
) {
    enum class LogType {
        COMBAT,
        INVESTIGATION,
        SOCIAL,
        TRAINING,
        MYSTERY,
        ACHIEVEMENT,
        RELATIONSHIP,
        OTHER
    }
}

data class SkillNode(
    val id: String,
    val name: String,
    val description: String,
    val level: Int,
    val maxLevel: Int,
    val path: SkillPath,
    val unlocked: Boolean,
    val requirements: List<String> = emptyList()
) {
    enum class SkillPath {
        COMBAT,
        MYSTERY,
        SOCIAL,
        STEALTH,
        SPECIAL
    }
}

data class DestinyNode(
    val id: String,
    val name: String,
    val description: String,
    val stage: Int,
    val totalStages: Int,
    val currentEffect: String,
    val nextEffect: String,
    val triggered: Boolean,
    val timestamp: Long
)

data class MysteryPoint(
    val id: String,
    val name: String,
    val category: MysteryCategory,
    val description: String,
    val discovered: Boolean,
    val clues: List<MysteryClue> = emptyList()
) {
    enum class MysteryCategory {
        PERSON,
        LOCATION,
        ORGANIZATION,
        EVENT,
        MYTHOS
    }
}

data class MysteryClue(
    val id: String,
    val content: String,
    val discovered: Boolean
)

data class ProgressMetrics(
    val sequenceProgress: Int = 0,
    val sequenceGoal: Int = 100,
    val spiritualityRegenProgress: Int = 0,
    val spiritualityRegenGoal: Int = 100,
    val rolePlayProgress: Int = 0,
    val rolePlayGoal: Int = 100,
    val mysteryUnlocked: Int = 0,
    val mysteryTotal: Int = 10
) {
    fun getSequencePercentage(): Float = sequenceProgress.toFloat() / sequenceGoal
    fun getSpiritualityPercentage(): Float = spiritualityRegenProgress.toFloat() / spiritualityRegenGoal
    fun getRolePlayPercentage(): Float = rolePlayProgress.toFloat() / rolePlayGoal
    fun getMysteryPercentage(): Float = mysteryUnlocked.toFloat() / mysteryTotal
}
