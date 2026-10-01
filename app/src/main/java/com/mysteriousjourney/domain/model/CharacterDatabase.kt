package com.mysteriousjourney.domain.model

/**
 * 游戏角色数据库
 * 包含所有主要角色的详细信息
 */
object CharacterDatabase {
    
    /**
     * 外神化身 - 噩梦难度角色
     */
    val outerGodIncarnation = DetailedCharacter(
        id = "outer_god_incarnation",
        basicInfo = DetailedCharacterBackground.BasicInfo(
            fullName = "外神化身",
            aliases = listOf("堕落母神之子", "虚空行者", "混沌使者"),
            age = 999999,
            birthplace = "宇宙虚空",
            currentResidence = "物质世界临时宿主",
            occupation = "外神代理人",
            socialClass = DetailedCharacterBackground.BasicInfo.SocialClass.NOBILITY,
            economicStatus = DetailedCharacterBackground.BasicInfo.EconomicStatus.WEALTHY,
            reputation = DetailedCharacterBackground.BasicInfo.Reputation.NEUTRAL
        ),
        relationships = listOf(
            CharacterRelationship(
                targetCharacterId = "fall_mother",
                targetCharacterName = "堕落母神",
                relationshipType = CharacterRelationship.RelationshipType.FAMILY,
                relationshipLevel = 100,
                description = "外神的本体，与化身有精神连接",
                knownSecrets = listOf("宇宙起源", "外神战争历史"),
                interactionHistory = listOf("精神连接", "力量传承")
            )
        ),
        abilities = CharacterAbilities(
            specialSkills = listOf(
                CharacterAbilities.SpecialSkill(
                    name = "宇宙权柄",
                    description = "掌控宇宙法则，扭曲现实",
                    proficiencyLevel = 10,
                    usageFrequency = CharacterAbilities.SpecialSkill.UsageFrequency.DAILY,
                    learnedFrom = "外神传承",
                    notableUses = listOf("改变现实", "扭曲空间")
                )
            ),
            professionalKnowledge = listOf(
                CharacterAbilities.ProfessionalKnowledge(
                    field = "宇宙奥秘",
                    description = "对宇宙法则和维度的深刻理解",
                    expertiseLevel = 10,
                    educationBackground = "神启传承",
                    practicalExperience = 999999
                )
            ),
            physicalAttributes = CharacterAbilities.PhysicalAttributes(
                strength = 10,
                agility = 10,
                endurance = 10,
                vitality = 10
            ),
            mentalAttributes = CharacterAbilities.MentalAttributes(
                intelligence = 10,
                perception = 10,
                willpower = 10,
                mentalStability = 10
            ),
            extraordinaryAbilities = listOf(
                CharacterAbilities.ExtraordinaryAbility(
                    name = "神性降临",
                    description = "短暂展现真正的神性，威压周围所有生灵",
                    powerLevel = 10,
                    source = "外神传承",
                    cooldown = "无限制",
                    sideEffects = listOf("可能吸引其他外神注意"),
                    usageCost = "无"
                )
            )
        ),
        background = DetailedCharacterBackground(
            basicInfo = DetailedCharacterBackground.BasicInfo(
                fullName = "外神化身",
                aliases = listOf("虚空使者"),
                age = 999999,
                birthplace = "宇宙虚空",
                currentResidence = "物质世界",
                occupation = "外神代理人",
                socialClass = DetailedCharacterBackground.BasicInfo.SocialClass.NOBILITY,
                economicStatus = DetailedCharacterBackground.BasicInfo.EconomicStatus.WEALTHY,
                reputation = DetailedCharacterBackground.BasicInfo.Reputation.NEUTRAL
            ),
            familyBackground = DetailedCharacterBackground.FamilyBackground(
                familyName = "外神",
                familyStatus = DetailedCharacterBackground.FamilyBackground.FamilyStatus.UNKNOWN,
                parentalInfo = DetailedCharacterBackground.FamilyBackground.ParentalInfo(),
                familyReputation = "未知存在",
                familySecrets = listOf("宇宙起源")
            ),
            educationHistory = DetailedCharacterBackground.EducationHistory(
                formalEducation = emptyList(),
                informalEducation = listOf(
                    DetailedCharacterBackground.EducationHistory.InformalEducation(
                        type = "神启",
                        subject = "宇宙法则",
                        duration = "永恒",
                        instructor = "堕落母神",
                        skillsGained = listOf("维度理论", "现实扭曲")
                    )
                ),
                specialTraining = emptyList(),
                academicAchievements = listOf("掌控宇宙法则")
            ),
            careerHistory = DetailedCharacterBackground.CareerHistory(
                currentJob = DetailedCharacterBackground.CareerHistory.JobInfo(
                    position = "外神代理人",
                    employer = "堕落母神",
                    duration = "永恒",
                    responsibilities = listOf("征服世界", "传播混沌"),
                    achievements = listOf("降临物质世界"),
                    reasonForLeaving = ""
                ),
                previousJobs = emptyList(),
                careerAchievements = listOf("宇宙权柄掌控"),
                careerFailures = emptyList(),
                professionalReputation = "未知存在"
            ),
            majorLifeEvents = listOf(
                DetailedCharacterBackground.LifeEvent(
                    title = "从混沌海诞生",
                    age = 0,
                    description = "作为堕落母神的一部分诞生",
                    impact = DetailedCharacterBackground.LifeEvent.ImpactLevel.LIFE_CHANGING,
                    consequences = listOf("获得神性", "降临物质世界"),
                    relatedCharacters = listOf("堕落母神")
                )
            ),
            personalityTraits = listOf(
                DetailedCharacterBackground.PersonalityTrait(
                    trait = "绝对理性",
                    description = "完全不受情感影响，只按逻辑行事",
                    intensity = 10,
                    manifestation = "绝对的逻辑思维"
                )
            ),
            secrets = listOf(
                DetailedCharacterBackground.Secret(
                    description = "外神的真实目的是吞噬整个物质世界",
                    severity = DetailedCharacterBackground.Secret.SeverityLevel.CRITICAL,
                    whoKnows = listOf("堕落母神"),
                    consequenceIfRevealed = "引起世界恐慌",
                    relatedTo = listOf("世界命运")
                )
            ),
            goalsAndMotivations = listOf(
                DetailedCharacterBackground.GoalAndMotivation(
                    goal = "征服物质世界",
                    motivation = "完成堕落母神的意志",
                    priority = DetailedCharacterBackground.GoalAndMotivation.Priority.URGENT,
                    timeline = "无限制",
                    obstacles = listOf("诡秘之主"),
                    resourcesNeeded = listOf("混沌海力量")
                )
            )
        ),
        inventory = CharacterInventory(
            equipment = CharacterInventory.Equipment(
                weapons = listOf(
                    CharacterInventory.Equipment.Weapon(
                        name = "外神权柄",
                        type = CharacterInventory.Equipment.Weapon.WeaponType.MAGICAL,
                        description = "蕴含宇宙法则的神器",
                        damage = "无法估量",
                        range = "无限",
                        specialProperties = listOf("现实扭曲", "精神支配"),
                        condition = CharacterInventory.Equipment.Weapon.Condition.PRISTINE,
                        origin = "外神传承"
                    )
                ),
                armor = emptyList(),
                tools = emptyList(),
                accessories = emptyList(),
                clothing = emptyList()
            ),
            personalItems = emptyList(),
            specialItems = listOf(
                CharacterInventory.SpecialItem(
                    name = "堕落之血",
                    description = "外神的本源血液，蕴含强大力量",
                    itemType = CharacterInventory.SpecialItem.SpecialItemType.ARTIFACT,
                    powers = listOf("增强神性", "恢复力量"),
                    restrictions = listOf("不可轻易使用"),
                    activationMethod = "血液激活",
                    origin = "外神本体",
                    curseOrBlessing = "既是诅咒也是祝福"
                )
            ),
            consumables = emptyList(),
            documents = emptyList(),
            currency = CharacterInventory.Currency(
                goldPounds = 999999,
                silverSoles = 999999,
                copperPence = 999999
            )
        ),
        currentStatus = DetailedCharacter.CharacterStatus(
            healthStatus = DetailedCharacter.CharacterStatus.HealthStatus.EXCELLENT,
            mentalStatus = DetailedCharacter.CharacterStatus.MentalStatus.STABLE,
            location = "物质世界",
            currentActivity = "观察世界",
            immediateGoals = listOf("征服世界"),
            emotionalState = "绝对理性",
            physicalCondition = "神性状态"
        ),
        plotRelevance = DetailedCharacter.PlotRelevance(
            roleInStory = DetailedCharacter.PlotRelevance.StoryRole.ANTAGONIST,
            importanceToPlot = DetailedCharacter.PlotRelevance.PlotImportance.CRITICAL,
            keyPlotPoints = listOf("降临物质世界", "与主角对抗"),
            playerInteractions = listOf("战斗", "对话", "影响剧情"),
            storyArcs = listOf(
                DetailedCharacter.PlotRelevance.StoryArc(
                    arcName = "外神降临",
                    description = "外神化身降临物质世界的故事线",
                    characterRole = "主要反派",
                    resolution = "被击败或成功"
                )
            )
        )
    )

    /**
     * 克莱恩·莫雷蒂 - 主角
     */
    val kleinMoretti = DetailedCharacter(
        id = "klein_moretti",
        basicInfo = DetailedCharacterBackground.BasicInfo(
            fullName = "克莱恩·莫雷蒂",
            aliases = listOf("周明瑞", "格尔曼·斯帕罗", "梅林·赫尔墨斯", "道恩·唐泰斯", "愚者"),
            age = 20,
            birthplace = "地球中国（穿越前）",
            currentResidence = "廷根市，霍伊大学附近出租屋",
            occupation = "历史系学生（伪装），值夜者（真实）",
            socialClass = DetailedCharacterBackground.BasicInfo.SocialClass.MIDDLE_CLASS,
            economicStatus = DetailedCharacterBackground.BasicInfo.EconomicStatus.LOW_INCOME,
            reputation = DetailedCharacterBackground.BasicInfo.Reputation.NEUTRAL
        ),
        relationships = listOf(
            CharacterRelationship(
                targetCharacterId = "dunn_smith",
                targetCharacterName = "邓恩·史密斯",
                relationshipType = CharacterRelationship.RelationshipType.SUPERIOR,
                relationshipLevel = 60,
                description = "值夜者小队队长，对克莱恩有关照和指导",
                knownSecrets = listOf("克莱恩是穿越者", "克莱恩拥有占卜家途径"),
                interactionHistory = listOf("初次面试", "第一次任务", "多次指导")
            )
        ),
        abilities = CharacterAbilities(
            specialSkills = listOf(
                CharacterAbilities.SpecialSkill(
                    name = "占卜",
                    description = "通过各种方式进行占卜预测",
                    proficiencyLevel = 7,
                    usageFrequency = CharacterAbilities.SpecialSkill.UsageFrequency.DAILY,
                    learnedFrom = "天赋+训练",
                    notableUses = listOf("预测危险", "寻找线索")
                )
            ),
            professionalKnowledge = listOf(
                CharacterAbilities.ProfessionalKnowledge(
                    field = "历史学",
                    description = "对历史事件的深入研究和分析",
                    expertiseLevel = 5,
                    educationBackground = "霍伊大学历史系",
                    practicalExperience = 2
                ),
                CharacterAbilities.ProfessionalKnowledge(
                    field = "占卜学",
                    description = "通过各种方式进行占卜预测",
                    expertiseLevel = 7,
                    educationBackground = "天赋+自学",
                    practicalExperience = 1
                )
            ),
            physicalAttributes = CharacterAbilities.PhysicalAttributes(
                strength = 6,
                agility = 6,
                endurance = 6,
                vitality = 6
            ),
            mentalAttributes = CharacterAbilities.MentalAttributes(
                intelligence = 8,
                perception = 7,
                willpower = 7,
                mentalStability = 6
            ),
            extraordinaryAbilities = listOf(
                CharacterAbilities.ExtraordinaryAbility(
                    name = "灵视",
                    description = "能够看到灵界和超凡生物",
                    powerLevel = 3,
                    source = "占卜家序列",
                    cooldown = "被动",
                    sideEffects = emptyList(),
                    usageCost = "无"
                )
            )
        ),
        background = DetailedCharacterBackground(
            basicInfo = DetailedCharacterBackground.BasicInfo(
                fullName = "克莱恩·莫雷蒂",
                aliases = listOf("周明瑞"),
                age = 20,
                birthplace = "地球中国",
                currentResidence = "廷根市",
                occupation = "历史系学生",
                socialClass = DetailedCharacterBackground.BasicInfo.SocialClass.MIDDLE_CLASS,
                economicStatus = DetailedCharacterBackground.BasicInfo.EconomicStatus.LOW_INCOME,
                reputation = DetailedCharacterBackground.BasicInfo.Reputation.NEUTRAL
            ),
            familyBackground = DetailedCharacterBackground.FamilyBackground(
                familyName = "莫雷蒂",
                familyStatus = DetailedCharacterBackground.FamilyBackground.FamilyStatus.ORPHAN,
                parentalInfo = DetailedCharacterBackground.FamilyBackground.ParentalInfo(),
                familyReputation = "普通家庭",
                familySecrets = listOf("穿越事实")
            ),
            educationHistory = DetailedCharacterBackground.EducationHistory(
                formalEducation = listOf(
                    DetailedCharacterBackground.EducationHistory.FormalEducation(
                        institution = "霍伊大学",
                        degree = "学士学位",
                        field = "历史系",
                        yearsAttended = "1348-1350",
                        graduationStatus = "在读",
                        notableAchievements = listOf("优秀学生", "历史研究奖")
                    )
                ),
                informalEducation = listOf(
                    DetailedCharacterBackground.EducationHistory.InformalEducation(
                        type = "自学",
                        subject = "神秘学",
                        duration = "1年",
                        instructor = "老尼尔",
                        skillsGained = listOf("基础占卜", "神秘仪式")
                    )
                ),
                specialTraining = emptyList(),
                academicAchievements = listOf("历史研究奖")
            ),
            careerHistory = DetailedCharacterBackground.CareerHistory(
                currentJob = DetailedCharacterBackground.CareerHistory.JobInfo(
                    position = "值夜者",
                    employer = "黑夜女神教会",
                    duration = "1349年至今",
                    responsibilities = listOf("调查超凡事件", "保护民众"),
                    achievements = listOf("成功解决多起案件"),
                    reasonForLeaving = ""
                ),
                previousJobs = emptyList(),
                careerAchievements = listOf("成为值夜者"),
                careerFailures = emptyList(),
                professionalReputation = "新人但很有潜力"
            ),
            majorLifeEvents = listOf(
                DetailedCharacterBackground.LifeEvent(
                    title = "穿越到诡秘世界",
                    age = 20,
                    description = "从地球穿越成为克莱恩·莫雷蒂",
                    impact = DetailedCharacterBackground.LifeEvent.ImpactLevel.LIFE_CHANGING,
                    consequences = listOf("获得新身份", "接触超凡世界"),
                    relatedCharacters = listOf("邓恩·史密斯", "老尼尔")
                )
            ),
            personalityTraits = listOf(
                DetailedCharacterBackground.PersonalityTrait(
                    trait = "谨慎",
                    description = "面对未知时保持警惕",
                    intensity = 7,
                    manifestation = "行动前仔细思考"
                )
            ),
            secrets = listOf(
                DetailedCharacterBackground.Secret(
                    description = "穿越者的身份",
                    severity = DetailedCharacterBackground.Secret.SeverityLevel.CRITICAL,
                    whoKnows = listOf("邓恩·史密斯"),
                    consequenceIfRevealed = "可能被视为异类",
                    relatedTo = listOf("身世之谜")
                )
            ),
            goalsAndMotivations = listOf(
                DetailedCharacterBackground.GoalAndMotivation(
                    goal = "适应新世界",
                    motivation = "生存下去",
                    priority = DetailedCharacterBackground.GoalAndMotivation.Priority.SURVIVAL,
                    timeline = "持续",
                    obstacles = listOf("超凡世界的危险"),
                    resourcesNeeded = listOf("知识", "力量", "盟友")
                )
            )
        ),
        inventory = CharacterInventory(
            equipment = CharacterInventory.Equipment(
                weapons = emptyList(),
                armor = emptyList(),
                tools = listOf(
                    CharacterInventory.Equipment.Tool(
                        name = "占卜吊坠",
                        type = CharacterInventory.Equipment.Tool.ToolType.MAGICAL,
                        description = "神秘的占卜吊坠",
                        uses = listOf("占卜", "灵性连接"),
                        quality = CharacterInventory.Equipment.Tool.Quality.EXCELLENT,
                        maintenance = "定期清洁"
                    )
                ),
                accessories = emptyList(),
                clothing = emptyList()
            ),
            personalItems = emptyList(),
            specialItems = emptyList(),
            consumables = emptyList(),
            documents = emptyList(),
            currency = CharacterInventory.Currency(
                goldPounds = 5,
                silverSoles = 10,
                copperPence = 3
            )
        ),
        currentStatus = DetailedCharacter.CharacterStatus(
            healthStatus = DetailedCharacter.CharacterStatus.HealthStatus.GOOD,
            mentalStatus = DetailedCharacter.CharacterStatus.MentalStatus.STABLE,
            location = "廷根市",
            currentActivity = "学习历史",
            immediateGoals = listOf("完成学业", "了解超凡世界"),
            emotionalState = "适应中",
            physicalCondition = "健康"
        ),
        plotRelevance = DetailedCharacter.PlotRelevance(
            roleInStory = DetailedCharacter.PlotRelevance.StoryRole.PROTAGONIST,
            importanceToPlot = DetailedCharacter.PlotRelevance.PlotImportance.CRITICAL,
            keyPlotPoints = listOf("穿越", "成为非凡者", "加入值夜者"),
            playerInteractions = listOf("直接控制", "决策影响", "成长发展"),
            storyArcs = listOf(
                DetailedCharacter.PlotRelevance.StoryArc(
                    arcName = "新人成长",
                    description = "从普通人成长为非凡者的过程",
                    characterRole = "主角",
                    resolution = "成为合格的值夜者"
                )
            )
        )
    )

    /**
     * 根据角色ID获取角色信息
     */
    fun getCharacterById(characterId: String): DetailedCharacter? {
        return when (characterId) {
            "klein_moretti" -> kleinMoretti
            "outer_god_incarnation" -> outerGodIncarnation
            else -> null
        }
    }
    
    /**
     * 根据玩家名称获取角色信息
     */
    fun getCharacterByName(playerName: String): DetailedCharacter? {
        return when (playerName) {
            "克莱恩·莫雷蒂", "周明瑞" -> kleinMoretti
            "外神化身" -> outerGodIncarnation
            else -> null
        }
    }
}
