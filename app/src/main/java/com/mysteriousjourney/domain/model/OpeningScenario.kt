package com.mysteriousjourney.domain.model

import com.mysteriousjourney.domain.model.Pathway

data class OpeningScenario(
    val id: String,
    val title: String,
    val description: String,
    val narrative: String,
    val initialPlayerState: PlayerState,
    val initialWorldState: WorldState,
    val tags: List<String>,
    val difficulty: Difficulty,
    val isEasterEgg: Boolean = false
) {
    enum class Difficulty {
        EASY,
        NORMAL,
        HARD,
        NIGHTMARE
    }
}

object OpeningScenarios {
    
    val ALL_SCENARIOS: List<OpeningScenario> = listOf(
        OpeningScenario(
            id = "klein_awakening",
            title = "意外的穿越",
            description = "穿越者刚刚在异世界苏醒",
            narrative = """
**意外的穿越**

你猛地睁开眼睛，发现自己躺在一张陌生的木床上。周围是维多利亚风格的房间，空气中弥漫着淡淡的煤烟味。

记忆如同潮水般涌来——你不再是原来的自己，而是成为了克莱恩·莫雷蒂，一个刚刚穿越到这个世界的年轻人。

房间的桌子上放着一枚古老的占卜吊坠，它似乎在微微发光，仿佛在召唤着什么。

窗外传来马车的辚辚声，远处教堂的钟声悠扬回荡。这个世界的一切都如此陌生，却又莫名熟悉。

【选择】拿起占卜吊坠仔细观察
【选择】查看房间里的其他物品
【选择】尝试回忆更多关于这个世界的记忆
【选择】先走出房间看看外面的情况
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "克莱恩·莫雷蒂",
                surfaceIdentity = "历史系学生",
                currentSequence = SequenceInfo("占卜家", 9, 5),
                spirituality = Spirituality(50, 100),
                healthStatus = "健康",
                sanity = Sanity(10, 0),
                money = Money(5, 10, 3),
                inventory = listOf("占卜吊坠", "古老笔记本", "几枚硬币", "学生证"),
                knowledge = listOf("基础占卜知识", "穿越者记忆", "地球历史"),
                factionRelations = mapOf(
                    "值夜者" to FactionRelation("未知", 0),
                    "教会" to FactionRelation("中立", 0)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年11月3日 周一 上午9点",
                currentLocation = "贝克兰德，租住房间",
                weather = "阴冷，薄雾",
                visitedLocations = listOf("贝克兰德，租住房间")
            ),
            tags = listOf("穿越", "占卜家", "原著线", "新手友好"),
            difficulty = OpeningScenario.Difficulty.EASY,
            isEasterEgg = true
        ),
        
        OpeningScenario(
            id = "outer_god_descent",
            title = "外神的凝视",
            description = "古老的外神降临到这个世界",
            narrative = """
**外神的凝视**

在无尽的虚空中，你感受到了来自物质世界的呼唤。作为堕落母神的化身，你决定降临到这个充满神秘的世界。

你选择了一个刚刚接触超凡力量的年轻人作为宿主，他的意识正在慢慢苏醒，而你将引导他走向神之道路。

透过宿主的眼睛，你看到了这个世界的真实面貌——灵性如潮水般涌动，命运的丝线交织成网。你感受到了来自源堡和混沌海的威胁——诡秘之主和上帝正在注视着这个世界。

作为外神，你拥有超越凡人的力量，但在这片土地上，你仍需小心应对那两位支柱的存在。

【选择】完全掌控宿主的身体，展现神威
【选择】让宿主保持自主，在暗中积蓄力量
【选择】寻找其他外神碎片的踪迹，联合对抗
【选择】先了解诡秘之主和上帝的当前状态
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "外神化身",
                surfaceIdentity = "神秘降临者",
                currentSequence = SequenceInfo("月亮", 0, 100),
                spirituality = Spirituality(9999, 9999),
                healthStatus = "神灵之躯·不灭",
                sanity = Sanity(0, 0),
                money = Money(999, 99, 99),
                inventory = listOf("外神权柄", "母巢碎片", "堕落之血", "宇宙真知", "神性护盾", "外神眷族召唤卷轴"),
                knowledge = listOf("宇宙奥秘", "外神知识", "22条途径真相", "源质秘密", "旧日支配者名录"),
                factionRelations = mapOf(
                    "外神势力" to FactionRelation("主宰", 100),
                    "堕落母神" to FactionRelation("本体", 100),
                    "正神教会" to FactionRelation("敌对", -100),
                    "诡秘之主" to FactionRelation("宿敌", -100),
                    "上帝" to FactionRelation("宿敌", -100)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年9月15日 凌晨3点",
                currentLocation = "神秘祭坛·星空之下",
                weather = "诡异，星空异常，外神气息弥漫",
                visitedLocations = listOf("神秘祭坛", "星空裂隙")
            ),
            tags = listOf("外神", "神灵开局", "绝对力量", "宇宙线", "对抗支柱"),
            difficulty = OpeningScenario.Difficulty.NIGHTMARE,
            isEasterEgg = true
        ),
        
        OpeningScenario(
            id = "lord_mysteries_rebirth",
            title = "成神之路",
            description = "诡秘之主从历史的长河中苏醒",
            narrative = """
**成神之路**

作为诡秘之主，你从历史的长河中苏醒。世界已经发生了巨大的变化，但你的力量依然存在。

你决定重新开始，以一个普通人的身份体验这个世界，同时暗中布局，重新登上神之宝座。

记忆中，你是一个刚刚获得占卜家序列魔药的历史系学生，前路充满未知与危险。

灰雾在意识深处涌动，源堡的力量在你体内流淌。你知道，这只是开始。

【选择】开始制作占卜家魔药
【选择】寻找昔日的眷属和信徒
【选择】了解当前时代的世界格局
【选择】重新建立塔罗会
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "周明瑞",
                surfaceIdentity = "历史系学生",
                currentSequence = SequenceInfo("占卜家", 9, 5),
                spirituality = Spirituality(100, 100),
                healthStatus = "准神状态",
                sanity = Sanity(0, 0),
                money = Money(100, 0, 0),
                inventory = listOf("源堡钥匙", "塔罗牌", "神秘左轮", "灰雾"),
                knowledge = listOf("22条神之途径", "历史真相", "塔罗会运作"),
                factionRelations = mapOf(
                    "塔罗会" to FactionRelation("掌控", 100),
                    "正神教会" to FactionRelation("复杂", 0),
                    "外神势力" to FactionRelation("敌对", -80)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年12月25日 午夜",
                currentLocation = "源堡之上",
                weather = "灰雾弥漫",
                visitedLocations = listOf("源堡", "贝克兰德", "廷根市")
            ),
            tags = listOf("诡秘之主", "高难度", "原著线", "神灵线"),
            difficulty = OpeningScenario.Difficulty.NIGHTMARE,
            isEasterEgg = true
        ),
        
        OpeningScenario(
            id = "creator_awakening",
            title = "造物主的苏醒",
            description = "曾经的造物主选择重新开始",
            narrative = """
**造物主的苏醒**

在混沌海的深处，你缓缓睁开眼睛。作为曾经的造物主——上帝的化身，你感受到了世界的呼唤。

你选择了一个新的身份降临，准备重新登上神之宝座，但你知道，诡秘之主也在注视着这个世界。

作为造物主的化身，你拥有创造与毁灭的力量，全知全能的权柄在你手中流转。但源堡的力量与你相抗，你必须小心应对那位诡秘之主。

古老的造物主印记在你胸口微微发热，提醒着你曾经的辉煌与使命——统一混沌海，对抗外神与诡秘之主。

【选择】展现造物主权柄，开始创造眷属
【选择】寻找混沌海的入口，恢复完整力量
【选择】潜入正神教会，了解当前局势
【选择】寻找诡秘之主的弱点，准备对抗
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "造物主化身",
                surfaceIdentity = "神秘降临者",
                currentSequence = SequenceInfo("倒吊人", 0, 100),
                spirituality = Spirituality(9999, 9999),
                healthStatus = "神灵之躯·永恒",
                sanity = Sanity(0, 0),
                money = Money(999, 99, 99),
                inventory = listOf("造物主权柄", "混沌海碎片", "全知之书", "创造圣杯", "神性护盾", "天使军团召唤卷轴"),
                knowledge = listOf("创造法则", "全知全能", "22条途径真相", "源质秘密", "天使名录"),
                factionRelations = mapOf(
                    "混沌海势力" to FactionRelation("主宰", 100),
                    "上帝" to FactionRelation("本体", 100),
                    "正神教会" to FactionRelation("复杂", 50),
                    "诡秘之主" to FactionRelation("宿敌", -100),
                    "外神势力" to FactionRelation("敌对", -100)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年10月1日 黄昏",
                currentLocation = "混沌海边缘·白日城遗迹",
                weather = "神圣光芒笼罩，混沌气息涌动",
                visitedLocations = listOf("混沌海边缘", "白日城遗迹")
            ),
            tags = listOf("造物主", "神灵开局", "绝对力量", "神圣线", "对抗支柱"),
            difficulty = OpeningScenario.Difficulty.NIGHTMARE,
            isEasterEgg = true
        ),
        
        OpeningScenario(
            id = "student_fate",
            title = "命运的转折",
            description = "作为普通学生，你意外接触到了超凡世界",
            narrative = """
**命运的转折**

1349年11月的伦敦被浓雾笼罩，煤气灯在石板路上投下摇曳的光影。你站在霍伊大学历史系的宿舍门前，手中紧握着那封改变命运的信件。

"占卜家序列"——这五个字在你脑海中回响。作为一名普通的历史系学生，你从未想过自己会与这些超凡的名词产生联系。

信封里还有一张纸条，上面写着一个地址和今晚的时间。这是机遇，还是陷阱？

远处传来教堂的钟声，提醒你时间正在流逝。这个世界的神秘面纱，正等待你去揭开。

【选择】打开信件查看内容
【选择】先回宿舍整理思绪
【选择】寻找关于占卜家的资料
【选择】联系寄信人询问详情
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "旅行者",
                surfaceIdentity = "霍伊大学历史系学生",
                currentSequence = SequenceInfo("占卜家", 9, 0),
                spirituality = Spirituality(100, 100),
                healthStatus = "健康",
                sanity = Sanity(0, 0),
                money = Money(15, 12, 6),
                inventory = listOf("学生证", "钢笔", "笔记本", "几枚硬币", "神秘信件"),
                knowledge = listOf("基础历史知识", "贝克兰德地理"),
                factionRelations = mapOf(
                    "霍伊大学" to FactionRelation("中立", 0),
                    "贝克兰德警方" to FactionRelation("未知", 0)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年11月3日 周一 上午9点",
                currentLocation = "霍伊大学历史系学生宿舍",
                weather = "阴冷，薄雾",
                visitedLocations = listOf("霍伊大学历史系学生宿舍", "贝克兰德广场")
            ),
            tags = listOf("学生", "占卜家", "新手友好", "经典线"),
            difficulty = OpeningScenario.Difficulty.EASY,
            isEasterEgg = true
        ),
        
        OpeningScenario(
            id = "night_watchman_recruit",
            title = "值夜者新人",
            description = "作为值夜者组织的新成员，你开始执行第一个任务",
            narrative = """
**值夜者新人**

廷根市值夜者小队的队长邓恩·史密斯坐在你对面，用那双灰色的眼睛审视着你。

"欢迎加入值夜者，"他说道，声音平静而威严，"从今天起，你就是我们的一员了。记住，我们的职责是守护黑夜，对抗那些普通人无法理解的威胁。"

他递给你一份档案，上面记录着最近发生的一起离奇案件——有人在深夜失踪，现场只留下诡异的黑色痕迹。

"这是你的第一个任务，"邓恩说，"去调查这起案件，找出真相。记住，小心行事，不要轻信任何人。"

窗外，夜幕正在降临，廷根市的灯火渐渐亮起。你的值夜者生涯，正式开始。

【选择】仔细研究档案中的细节
【选择】询问邓恩关于案件的更多信息
【选择】直接前往案发现场调查
【选择】先去值夜者小队的资料室查阅相关资料
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "值夜者新人",
                surfaceIdentity = "值夜者小队成员",
                currentSequence = SequenceInfo("不眠者", 9, 5),
                spirituality = Spirituality(60, 100),
                healthStatus = "健康",
                sanity = Sanity(5, 0),
                money = Money(10, 5, 0),
                inventory = listOf("值夜者徽章", "左轮手枪", "银制匕首", "案件档案"),
                knowledge = listOf("值夜者守则", "基础灵视", "常见超凡生物"),
                factionRelations = mapOf(
                    "值夜者" to FactionRelation("成员", 30),
                    "黑夜女神教会" to FactionRelation("友好", 20),
                    "廷根市警方" to FactionRelation("合作", 10)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年10月15日 周二 傍晚6点",
                currentLocation = "廷根市，值夜者小队总部",
                weather = "阴沉，即将入夜",
                visitedLocations = listOf("廷根市，值夜者小队总部")
            ),
            tags = listOf("值夜者", "不眠者", "调查", "新手友好"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "tarot_club_member",
            title = "塔罗会邀请",
            description = "你收到了来自塔罗会的神秘邀请",
            narrative = """
**塔罗会邀请**

午夜时分，你独自坐在房间里，面前摆放着那副神秘的塔罗牌。今晚，你将第一次参加塔罗会的聚会。

据说，这是一个由"愚者"大人创立的秘密组织，成员们来自世界各地，各自拥有不同的身份和能力。

你轻轻触碰那张"星星"牌，感受着其中蕴含的神秘力量。灰雾开始在眼前涌动，古老的青铜长桌逐渐显现。

长桌旁已经坐着几个人影，他们的面孔被灰雾遮蔽，只能看到模糊的轮廓。在长桌的最上方，端坐着一位身影——那便是传说中的"愚者"大人。

"欢迎新成员的加入，"一个威严而神秘的声音响起，"请自我介绍，并说明你能够提供的帮助。"

【选择】恭敬地自我介绍
【选择】先观察其他成员的反应
【选择】询问塔罗会的具体规则
【选择】直接提出你想要交换的物品或知识
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "星星",
                surfaceIdentity = "神秘商人",
                currentSequence = SequenceInfo("偷盗者", 8, 15),
                spirituality = Spirituality(70, 100),
                healthStatus = "健康",
                sanity = Sanity(10, 0),
                money = Money(30, 0, 0),
                inventory = listOf("星星牌", "神秘面具", "情报网络笔记", "各种魔药材料"),
                knowledge = listOf("地下交易规则", "贝克兰德情报", "基础偷盗技巧"),
                factionRelations = mapOf(
                    "塔罗会" to FactionRelation("新成员", 10),
                    "地下世界" to FactionRelation("中立", 0)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年11月20日 午夜",
                currentLocation = "塔罗会聚会（灰雾之上）",
                weather = "灰雾弥漫",
                visitedLocations = listOf("塔罗会聚会", "贝克兰德")
            ),
            tags = listOf("塔罗会", "偷盗者", "神秘", "交易"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "hunter_path",
            title = "猎人之路",
            description = "作为猎人途径的非凡者，你追踪着危险的猎物",
            narrative = """
**猎人之路**

德索尔省的森林深处，你蹲伏在灌木丛中，追踪着那只传说中的"火焰狼人"。

作为猎人途径的非凡者，狩猎超凡生物是你的职责，也是你晋升的途径。这只狼人已经造成了多起伤亡事件，当地教会悬赏重金缉拿。

空气中弥漫着硫磺的气息，远处传来野兽的低吼。你的火焰在掌心跳动，随时准备发动攻击。

突然，一阵破风声从背后袭来——有人也在追踪这只狼人，而且来者不善！

【选择】转身应对身后的威胁
【选择】继续追踪狼人，无视来者
【选择】大声警告来者离开
【选择】隐匿身形，观察情况
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "猎人",
                surfaceIdentity = "自由猎人",
                currentSequence = SequenceInfo("猎人", 7, 20),
                spirituality = Spirituality(80, 100),
                healthStatus = "健康",
                sanity = Sanity(15, 0),
                money = Money(25, 10, 5),
                inventory = listOf("猎枪", "火焰符文", "追踪药剂", "狼人情报"),
                knowledge = listOf("狩猎技巧", "火焰操控", "超凡生物弱点"),
                factionRelations = mapOf(
                    "猎人公会" to FactionRelation("成员", 20),
                    "当地教会" to FactionRelation("合作", 15)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年9月8日 下午3点",
                currentLocation = "德索尔省，深林",
                weather = "晴朗，微风",
                visitedLocations = listOf("德索尔省，深林", "猎人公会")
            ),
            tags = listOf("猎人", "战斗", "追踪", "冒险"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "spectator_detective",
            title = "心灵侦探",
            description = "作为观众途径的非凡者，你调查一起离奇案件",
            narrative = """
**心灵侦探**

贝克兰德的富人区发生了一起离奇的密室杀人案。受害者是一位知名商人，死因成谜，现场没有任何打斗痕迹。

作为观众途径的非凡者，你被受害者家属聘请来调查此案。你的能力让你能够洞察人心，发现常人无法察觉的线索。

站在案发现场，你闭上眼睛，让灵性蔓延开来。残留的情绪波动如同蛛丝马迹，指引着真相的方向。

恐惧、愤怒、绝望……受害者在死前经历了剧烈的情绪波动。而更诡异的是，你感受到了一丝不属于任何人的冷漠——那是真正的凶手留下的痕迹。

【选择】追踪那丝冷漠的情绪痕迹
【选择】询问受害者的家属和仆人
【选择】检查房间里的每一个角落
【选择】尝试重现案发时的情景
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "心灵侦探",
                surfaceIdentity = "私人侦探",
                currentSequence = SequenceInfo("观众", 7, 18),
                spirituality = Spirituality(75, 100),
                healthStatus = "健康",
                sanity = Sanity(8, 0),
                money = Money(40, 5, 0),
                inventory = listOf("侦探徽章", "放大镜", "案件笔记", "情绪感应水晶"),
                knowledge = listOf("心理学", "犯罪学", "情绪感知", "贝克兰德地理"),
                factionRelations = mapOf(
                    "贝克兰德警方" to FactionRelation("合作", 10),
                    "上流社会" to FactionRelation("中立", 0)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年12月1日 上午10点",
                currentLocation = "贝克兰德，富人区",
                weather = "阴天，有雾",
                visitedLocations = listOf("贝克兰德，富人区", "侦探事务所")
            ),
            tags = listOf("观众", "侦探", "推理", "调查"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "apprentice_wanderer",
            title = "学徒的旅程",
            description = "作为学徒途径的非凡者，你探索世界的奥秘",
            narrative = """
**学徒的旅程**

你站在古老的传送门前，手中握着师父留给你的星图。作为学徒途径的非凡者，探索未知是你永恒的追求。

师父在最后一次出行后再也没有回来，只留下了这张星图和一封信。信中提到了一个名为"星之钥"的神秘存在，以及通往更高序列的线索。

传送门缓缓开启，一道星光从中涌出。门后是一个你从未见过的世界——或者说，是一个你从未见过的维度。

你的旅程即将开始，而终点，或许就是师父失踪的真相。

【选择】踏入传送门，开始探索
【选择】先研究星图上的标记
【选择】留下标记，以便随时返回
【选择】感知门后的危险程度
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "学徒",
                surfaceIdentity = "神秘学徒",
                currentSequence = SequenceInfo("学徒", 6, 25),
                spirituality = Spirituality(85, 100),
                healthStatus = "健康",
                sanity = Sanity(12, 0),
                money = Money(15, 8, 3),
                inventory = listOf("星图", "传送门钥匙", "师父的信", "空间符文"),
                knowledge = listOf("空间魔法", "维度知识", "古代遗迹", "星象学"),
                factionRelations = mapOf(
                    "神秘学会" to FactionRelation("成员", 15),
                    "其他学徒" to FactionRelation("竞争", -5)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年8月20日 正午",
                currentLocation = "古代遗迹，传送门前",
                weather = "星光闪烁",
                visitedLocations = listOf("古代遗迹", "神秘学会")
            ),
            tags = listOf("学徒", "探索", "空间", "冒险"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "moon_alchemist",
            title = "月亮炼金师",
            description = "作为药师途径的非凡者，你研究禁忌的炼金术",
            narrative = """
**月亮炼金师**

月圆之夜，你的炼金实验室中弥漫着奇异的香气。作为药师途径的非凡者，你一直在研究一种禁忌的炼金术——生命创造。

你的研究已经接近突破，但需要的最后一种材料极其罕见：月之泪。据说只有在满月之夜，特定地点才会出现这种神秘物质。

今晚就是满月，而你手中的地图指向了贝克兰德郊外的一座古老庄园。传说那里曾是某位吸血鬼伯爵的居所。

窗外，月光如水银般倾泻而下。你的血液在沸腾，月亮的力量在召唤着你。

【选择】前往古老庄园寻找月之泪
【选择】继续完善炼金配方
【选择】寻找其他获取月之泪的方法
【选择】研究吸血鬼伯爵的历史
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "月亮炼金师",
                surfaceIdentity = "药剂师",
                currentSequence = SequenceInfo("药师", 6, 22),
                spirituality = Spirituality(70, 100),
                healthStatus = "月之眷顾",
                sanity = Sanity(18, 0),
                money = Money(35, 0, 0),
                inventory = listOf("炼金设备", "月之泪配方", "吸血鬼伯爵地图", "各种药剂"),
                knowledge = listOf("炼金术", "生命创造", "月亮知识", "吸血鬼传说"),
                factionRelations = mapOf(
                    "炼金协会" to FactionRelation("成员", 10),
                    "堕落母神信徒" to FactionRelation("潜在接触", 5)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年10月15日 满月之夜",
                currentLocation = "贝克兰德，私人炼金实验室",
                weather = "月光皎洁",
                visitedLocations = listOf("贝克兰德", "炼金协会")
            ),
            tags = listOf("药师", "炼金", "月亮", "禁忌"),
            difficulty = OpeningScenario.Difficulty.HARD
        ),
        
        OpeningScenario(
            id = "reader_scholar",
            title = "知识追寻者",
            description = "作为阅读者途径的非凡者，你追寻失落的古籍",
            narrative = """
**知识追寻者**

大英图书馆的深处，你翻阅着一本古老的典籍。作为阅读者途径的非凡者，知识就是你的力量。

这本典籍记载着一个失落文明的秘密——白塔。据说那里保存着世界上最完整的知识，是所有阅读者途径非凡者的终极追求。

然而，你并不是唯一追寻这个秘密的人。最近，你总感觉有人在暗中跟踪你，似乎也对白塔的秘密感兴趣。

书页上的一行字引起了你的注意："当星辰归位，白塔将在时间的尽头显现。"

【选择】研究星辰归位的含义
【选择】调查跟踪你的人
【选择】寻找其他关于白塔的线索
【选择】前往图书馆的禁书区查阅更多资料
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "知识追寻者",
                surfaceIdentity = "学者",
                currentSequence = SequenceInfo("阅读者", 6, 20),
                spirituality = Spirituality(65, 100),
                healthStatus = "健康",
                sanity = Sanity(6, 0),
                money = Money(20, 12, 8),
                inventory = listOf("古籍", "学者证", "白塔笔记", "放大镜"),
                knowledge = listOf("古代历史", "失落文明", "白塔传说", "多国语言"),
                factionRelations = mapOf(
                    "学术界" to FactionRelation("成员", 20),
                    "大英图书馆" to FactionRelation("读者", 15)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年11月10日 下午2点",
                currentLocation = "贝克兰德，大英图书馆",
                weather = "阴雨绵绵",
                visitedLocations = listOf("贝克兰德", "大英图书馆", "霍伊大学")
            ),
            tags = listOf("阅读者", "知识", "调查", "学者"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "warrior_dawn",
            title = "黎明骑士",
            description = "作为战士途径的非凡者，你守护着边境",
            narrative = """
**黎明骑士**

边境要塞的城墙上，你凝视着远方的黑暗。作为战士途径的非凡者，守护人类世界是你的使命。

最近，边境频繁出现异常——有报告称看到了从未见过的怪物，它们似乎来自某个未知的维度。

你的骑士团已经损失了三名成员，而敌人的真面目至今成谜。今晚，你决定亲自带队巡逻，找出真相。

黎明即将到来，但在这之前，你必须熬过最黑暗的时刻。

【选择】带队巡逻边境
【选择】研究之前战斗中收集的怪物样本
【选择】向教会请求支援
【选择】调查怪物出现的规律
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "黎明骑士",
                surfaceIdentity = "边境骑士团成员",
                currentSequence = SequenceInfo("战士", 6, 18),
                spirituality = Spirituality(90, 100),
                healthStatus = "健康",
                sanity = Sanity(8, 0),
                money = Money(12, 5, 0),
                inventory = listOf("骑士剑", "黎明符文", "怪物样本", "边境地图"),
                knowledge = listOf("战斗技巧", "边境地理", "怪物知识", "骑士守则"),
                factionRelations = mapOf(
                    "边境骑士团" to FactionRelation("成员", 25),
                    "永恒烈阳教会" to FactionRelation("友好", 15)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年9月25日 凌晨4点",
                currentLocation = "边境要塞",
                weather = "黑暗，寒风",
                visitedLocations = listOf("边境要塞", "边境村庄")
            ),
            tags = listOf("战士", "战斗", "守护", "边境"),
            difficulty = OpeningScenario.Difficulty.NORMAL
        ),
        
        OpeningScenario(
            id = "assassin_shadow",
            title = "暗影刺客",
            description = "作为刺客途径的非凡者，你执行着危险的任务",
            narrative = """
**暗影刺客**

贝克兰德的暗巷中，你如同幽灵般穿行。作为刺客途径的非凡者，你的身影从未被人真正看清。

今晚的任务目标是一位腐败的官员，他出卖了无数无辜者，现在轮到他付出代价了。

目标正在一家私人俱乐部中，周围有大量护卫。但对你来说，这不过是另一个挑战。

你的匕首在黑暗中闪着寒光，女巫的力量在你体内涌动。今晚，正义将以另一种方式降临。

【选择】潜入俱乐部，寻找最佳时机
【选择】制造混乱，趁乱行动
【选择】调查目标的护卫配置
【选择】寻找目标的其他弱点
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "暗影刺客",
                surfaceIdentity = "神秘人",
                currentSequence = SequenceInfo("刺客", 7, 15),
                spirituality = Spirituality(75, 100),
                healthStatus = "健康",
                sanity = Sanity(20, 0),
                money = Money(50, 0, 0),
                inventory = listOf("暗影匕首", "毒药", "伪装工具", "目标情报"),
                knowledge = listOf("暗杀技巧", "贝克兰德暗巷", "毒药知识", "隐匿术"),
                factionRelations = mapOf(
                    "地下组织" to FactionRelation("成员", 20),
                    "贝克兰德警方" to FactionRelation("敌对", -30)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年11月28日 深夜11点",
                currentLocation = "贝克兰德，暗巷",
                weather = "阴冷，无月",
                visitedLocations = listOf("贝克兰德", "地下组织据点")
            ),
            tags = listOf("刺客", "暗杀", "黑暗", "任务"),
            difficulty = OpeningScenario.Difficulty.HARD
        ),
        
        OpeningScenario(
            id = "monster_fate",
            title = "命运怪物",
            description = "作为怪物途径的非凡者，你与命运纠缠",
            narrative = """
**命运怪物**

你从未想过自己会成为"怪物"。但自从服下那份魔药后，命运就与你纠缠不清。

有时候，你能看到未来的片段；有时候，厄运会毫无征兆地降临在你周围。你既被命运眷顾，又被命运诅咒。

今天，你看到了一个可怕的预兆——贝克兰德将会发生一场大灾难。而你，似乎是这场灾难的关键。

命运之轮在你眼前转动，无数可能性交织在一起。你能否改变命运的轨迹？

【选择】尝试解读预兆的更多细节
【选择】前往预兆中出现的地点
【选择】寻找能够帮助你的其他非凡者
【选择】接受命运的安排，等待灾难降临
            """.trimIndent(),
            initialPlayerState = PlayerState(
                name = "命运怪物",
                surfaceIdentity = "流浪者",
                currentSequence = SequenceInfo("怪物", 7, 10),
                spirituality = Spirituality(55, 100),
                healthStatus = "命运纠缠",
                sanity = Sanity(25, 0),
                money = Money(3, 2, 5),
                inventory = listOf("命运硬币", "预言笔记", "幸运护符"),
                knowledge = listOf("命运感知", "预兆解读", "城市地理"),
                factionRelations = mapOf(
                    "命运教派" to FactionRelation("潜在成员", 5),
                    "正神教会" to FactionRelation("警惕", -10)
                )
            ),
            initialWorldState = WorldState(
                currentTime = "1349年12月5日 清晨",
                currentLocation = "贝克兰德，贫民区",
                weather = "阴沉，有雨",
                visitedLocations = listOf("贝克兰德", "贫民区")
            ),
            tags = listOf("怪物", "命运", "预言", "混沌"),
            difficulty = OpeningScenario.Difficulty.HARD
        )
    )
    
    fun getRandomScenario(): OpeningScenario = ALL_SCENARIOS.random()
    
    fun getScenarioById(id: String): OpeningScenario? = ALL_SCENARIOS.find { it.id == id }
    
    fun getScenariosByDifficulty(difficulty: OpeningScenario.Difficulty): List<OpeningScenario> =
        ALL_SCENARIOS.filter { it.difficulty == difficulty }
    
    fun getScenariosByTag(tag: String): List<OpeningScenario> =
        ALL_SCENARIOS.filter { tag in it.tags }
    
    fun getEasterEggScenarios(): List<OpeningScenario> =
        ALL_SCENARIOS.filter { it.isEasterEgg }
    
    fun getNonEasterEggScenarios(): List<OpeningScenario> =
        ALL_SCENARIOS.filter { !it.isEasterEgg }
    
    fun getRandomNonEasterEggScenario(): OpeningScenario = 
        getNonEasterEggScenarios().ifEmpty { ALL_SCENARIOS }.random()
}
