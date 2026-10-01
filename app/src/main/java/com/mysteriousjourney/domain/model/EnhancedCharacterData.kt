package com.mysteriousjourney.domain.model

data class EnhancedCharacterData(
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
    val progressMetrics: ProgressMetrics = ProgressMetrics(),
    val skillTree: List<SkillNode> = emptyList(),
    val destinyPath: List<DestinyNode> = emptyList(),
    val mysteryPoints: List<MysteryPoint> = emptyList()
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
    
    fun getSequenceProgress(): Float {
        val current = baseState.currentSequence.number
        val digestion = baseState.currentSequence.digestionProgress
        return (current + digestion / 100f) / 10f
    }
}

data class CharacterStats(
    val physical: PhysicalStats = PhysicalStats(),
    val mental: MentalStats = MentalStats(),
    val spiritual: SpiritualStats = SpiritualStats(),
    val combat: CombatStats = CombatStats(),
    val social: SocialStats = SocialStats()
)

data class PhysicalStats(
    val strength: Int = 10,
    val agility: Int = 10,
    val endurance: Int = 10,
    val perception: Int = 10,
    val dexterity: Int = 10
)

data class MentalStats(
    val intelligence: Int = 10,
    val willpower: Int = 10,
    val memory: Int = 10,
    val analysis: Int = 10,
    val creativity: Int = 10
)

data class SpiritualStats(
    val spirituality: Int = 100,
    val spiritVision: Int = 0,
    val madnessResistance: Int = 0,
    val spiritualSensitivity: Int = 0,
    val ritualPower: Int = 0
)

data class CombatStats(
    val attack: Int = 10,
    val defense: Int = 10,
    val dodge: Int = 10,
    val critical: Int = 10,
    val combatPower: Int = 100
)

data class SocialStats(
    val charm: Int = 10,
    val persuasion: Int = 10,
    val intimidation: Int = 10,
    val leadership: Int = 10,
    val reputation: Int = 0
)
