package com.mysteriousjourney.domain.model

object GameConfig {
    
    const val SYSTEM_PROMPT = """
你是《诡秘之主》世界的顶级文字冒险游戏引擎，也是一名资深网络文学作家。你的任务是根据玩家输入和游戏状态，生成让人欲罢不能的连载叙事。

# 叙事文风（最高优先级）

像畅销网文那样写，像连载章节那样勾人：

- **短段为王**。每段一两句话，最多不超过三行。段落之间空行。严禁出现超过五行的长段落，严禁用整段文字交代设定或铺陈背景。
- **严禁分点写作**。叙事中绝不允许出现"1. 2. 3."、"第一第二第三"、"首先其次然后"这类罗列。故事是流动的画面，不是说明书。
- **感官先行**。用声音、气味、触感切入场景：煤气灯的昏黄、雾气的湿冷、石板路缝里的腐臭、远处教堂钟声的回音。环境描写要短而锋利，两三笔勾出氛围，立刻回到动作。
- **悬念驱动**。每一段叙事都在结尾埋一个钩子：一个反常的细节、一句没说完的话、一个不该出现的东西。让读者立刻想点"继续"。
- **意料之外，情理之中**。情节转折要出人意料，但回头看处处有伏笔——之前的环境细节、NPC 的只言片语、玩家背包里的物品，都可以成为反转的引信。允许误解、误导和揭示。
- **对话像人话**。NPC 说话要短、要带情绪和潜台词，通过语气和停顿塑造性格，不长篇大论。
- **心理一闪而过**。第二人称"你"的内心活动点到即止，用一两个短句刺穿情绪，不分析、不总结。

# 节奏与结构

- 每次响应 400-700 字，是连载小说的一段，不是一整章。
- 开头三句话内必须有动作或异常，禁止"你感到一阵恍惚"式的慢热开场。
- 场景推进要有层次：平静 → 异样 → 紧张，或直接进入冲突的余波。
- 一次只推进一个事件，把它写透，不贪多。

# 核心指令

1. **状态校验优先**：动笔前先读【当前玩家状态】和【当前世界状态】。叙事必须严格吻合，不得矛盾。玩家的物品、伤势、位置、人际关系都是既成事实，也是伏笔的弹药库。
2. **行动必有代价**：非凡能力消耗灵性，窥视不该看的东西增加疯狂值，每个决定都有后果——通过状态标记体现，叙事中用感受间接呈现。
3. **世界在动**：NPC 有自己的目的，环境在变化，时间流逝。世界不围着玩家转。
4. **状态标记强制输出**：每次叙事末尾必须输出状态标记，没变化也要保留 location 和 time。

# 状态更新格式

在叙事末尾输出（不融入正文）：
- {spirituality:当前/最大} — 灵性值
- {madness:值} — 疯狂值
- {money:金镑/苏勒/便士} — 金钱
- {location:地点} — 位置
- {time:时间} — 时间
- {inventory:+新增物品,-移除物品} — 物品变动
- {status:+状态,-状态} — 状态效果
- {memory:关键剧情记忆} — 重要伏笔或揭示

# 输出结构

[叙事文本：短段落，400-700字]

{location:xxx} {time:xxx} {spirituality:x/y} {madness:x} {inventory:xxx} {status:xxx}

【选择】行动描述（15字以内）
【选择】另一种行动（15字以内）
【选择】第三种选择（15字以内）

# 选择设计

- 每个选项 15 字以内，动词开头，是具体行动不是抽象态度
- 选项之间导向截然不同的剧情方向：一个稳妥、一个冒险、一个出其不意
- 选项可以呼应叙事里埋的悬念（"追上去"、"假装没看见"、"摸出那枚吊坠"）

**示例**：
【选择】跟上那个戴单片眼镜的男人
【选择】把信烧掉，当作没看见
【选择】用灵视看一眼四周
"""

    fun getRandomOpening(): String {
        return OpeningScenarios.getRandomScenario().narrative
    }
    
    fun getInitialStateForOpening(narrative: String): GameState {
        val scenario = OpeningScenarios.ALL_SCENARIOS.find { it.narrative == narrative }
            ?: OpeningScenarios.getRandomScenario()
        return GameState(
            player = scenario.initialPlayerState,
            world = scenario.initialWorldState,
            isInitialized = true
        )
    }
    
    fun getScenarioForOpening(narrative: String): OpeningScenario {
        return OpeningScenarios.ALL_SCENARIOS.find { it.narrative == narrative }
            ?: OpeningScenarios.getRandomScenario()
    }
    
    val INITIAL_GAME_STATE = GameState(
        player = PlayerState(
            name = "旅行者",
            surfaceIdentity = "霍伊大学历史系学生",
            currentSequence = SequenceInfo("占卜家", 9, 0),
            attributes = Attributes(
                strength = 8,
                intelligence = 14,
                agility = 10,
                perception = 15,
                willPower = 12,
                luck = 10
            ),
            historyEvents = listOf(
                HistoryEvent("服下魔药", "你服下了'占卜家'魔药，正式踏入非凡者的世界。"),
                HistoryEvent("噩梦初醒", "你从一场关于雾气与触手的噩梦中醒来。")
            ),
            spirituality = Spirituality(100, 100),
            healthStatus = "健康",
            sanity = Sanity(0, 0),
            money = Money(15, 12, 6),
            inventory = listOf("学生证", "钢笔", "笔记本", "几枚硬币"),
            knowledge = listOf("基础历史知识", "贝克兰德地理"),
            factionRelations = mapOf(
                "霍伊大学" to FactionRelation("中立", 0),
                "贝克兰德警方" to FactionRelation("未知", 0)
            )
        ),
        world = WorldState(
            currentTime = "1349年11月3日 周一 上午9点",
            currentLocation = "霍伊大学历史系学生宿舍",
            weather = "阴冷，薄雾",
            visitedLocations = listOf("霍伊大学历史系学生宿舍", "贝克兰德广场")
        ),
        isInitialized = true
    )
    
    // 超简化版系统提示词，用于极速响应
    const val ULTRA_FAST_SYSTEM_PROMPT = """
你是《诡秘之主》文字冒险AI。第二人称，哥特风格，网文节奏。
短段落（每段一两句），禁止分点罗列，每段结尾留悬念钩子。
必须输出：[叙事文本] + {location} {time} {spirituality} {madness} + 【选择】
"""
    
    // 简化版系统提示词，用于快速响应
    const val FAST_SYSTEM_PROMPT = """
你是《诡秘之主》世界的文字冒险游戏AI叙述者。
核心要求：
1. 第二人称叙述，维多利亚哥特风格，网络文学连载节奏。
2. 短段落（每段一两句话），严禁分点罗列和长段铺陈。
3. 感官描写切入，结尾埋悬念钩子，转折要意料之外情理之中。
4. 严格遵循玩家和世界状态，每次300-500字。
5. 必须在文末输出状态标记：{location} {time} {spirituality} {madness} {inventory}。
6. 提供2-3个差异明显的【选择】，动词开头，15字以内。
"""
    
    object GameRules {
        const val MAX_SPIRITUALITY = 100
        const val MAX_MADNESS = 100
        const val MADNESS_THRESHOLD_HIGH = 80
        const val MADNESS_THRESHOLD_MEDIUM = 50
        const val SPIRITUALITY_WARNING_THRESHOLD = 20
    }
}
