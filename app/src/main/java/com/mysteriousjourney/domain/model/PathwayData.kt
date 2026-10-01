package com.mysteriousjourney.domain.model

data class Pathway(
    val id: String,
    val name: String,
    val sequences: List<Sequence>,
    val source: PathwaySource,
    val oldOne: OldOne?,
    val substance: String?,
    val description: String,
    val themes: List<String>,
    val abilities: List<String>
) {
    data class Sequence(
        val number: Int,
        val name: String,
        val tier: SequenceTier,
        val description: String = "",
        val keyAbilities: List<String> = emptyList()
    )
    
    enum class SequenceTier {
        LOW,
        MIDDLE,
        HIGH,
        DEMIGOD,
        TRUE_GOD
    }
}

enum class PathwaySource {
    ORIGINAL_CREATOR,
    OUTER_GOD,
    UNKNOWN
}

data class OldOne(
    val name: String,
    val titles: List<String>,
    val description: String
)

object PathwayData {
    
    val ALL_PATHWAYS: List<Pathway> = listOf(
        Pathway(
            id = "seer",
            name = "占卜家途径",
            sequences = listOf(
                Pathway.Sequence(9, "占卜家", Pathway.SequenceTier.LOW, "能够进行简单的占卜和预言", listOf("占卜", "灵视", "星象解读")),
                Pathway.Sequence(8, "小丑", Pathway.SequenceTier.LOW, "获得身体控制和表演能力", listOf("完美身体控制", "纸片化", "滑稽表演")),
                Pathway.Sequence(7, "魔术师", Pathway.SequenceTier.MIDDLE, "掌握各种魔术技巧", listOf("火焰跳跃", "空气弹", "水下呼吸")),
                Pathway.Sequence(6, "无面人", Pathway.SequenceTier.MIDDLE, "能够改变外貌", listOf("容貌改变", "声音模仿", "气息伪装")),
                Pathway.Sequence(5, "秘偶大师", Pathway.SequenceTier.MIDDLE, "操控秘偶进行战斗", listOf("秘偶操控", "历史迷雾", "灵体线")),
                Pathway.Sequence(4, "诡法师", Pathway.SequenceTier.HIGH, "半神级别，掌握诡异力量", listOf("空间置换", "纸人替身", "诡异能力")),
                Pathway.Sequence(3, "古代学者", Pathway.SequenceTier.HIGH, "能够借用历史力量", listOf("历史投影", "古代知识", "时间回溯")),
                Pathway.Sequence(2, "奇迹师", Pathway.SequenceTier.DEMIGOD, "天使级别，创造奇迹", listOf("奇迹创造", "命运操控", "时空扭曲")),
                Pathway.Sequence(1, "诡秘侍者", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("诡秘领域", "命运编织", "历史篡改")),
                Pathway.Sequence(0, "愚者", Pathway.SequenceTier.TRUE_GOD, "真神，掌控命运与诡秘", listOf("命运主宰", "诡秘权柄", "时空支配"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "诡秘之主",
                titles = listOf("时空之王", "命运道标", "源堡化身", "灵界支配者", "福生玄黄天尊"),
                description = "最初造物主分裂出的支柱之一，掌控源堡"
            ),
            substance = "源堡",
            description = "占卜家途径是与命运、诡秘、时空相关的途径，能够窥视命运、操控历史、创造奇迹。",
            themes = listOf("命运", "诡秘", "时空", "历史", "表演"),
            abilities = listOf("占卜预言", "身体控制", "容貌改变", "秘偶操控", "历史借用", "奇迹创造", "命运操控")
        ),
        
        Pathway(
            id = "apprentice",
            name = "学徒途径",
            sequences = listOf(
                Pathway.Sequence(9, "学徒", Pathway.SequenceTier.LOW, "掌握基础魔法知识", listOf("开门", "戏法", "知识获取")),
                Pathway.Sequence(8, "戏法大师", Pathway.SequenceTier.LOW, "精通各种戏法", listOf("戏法精通", "物品操控", "幻术")),
                Pathway.Sequence(7, "占星人", Pathway.SequenceTier.MIDDLE, "能够进行星象占卜", listOf("星象占卜", "命运窥视", "预知")),
                Pathway.Sequence(6, "记录官", Pathway.SequenceTier.MIDDLE, "记录和重现信息", listOf("信息记录", "完美记忆", "知识重现")),
                Pathway.Sequence(5, "旅行家", Pathway.SequenceTier.MIDDLE, "掌握空间移动能力", listOf("空间跳跃", "传送门", "位置感知")),
                Pathway.Sequence(4, "秘法师", Pathway.SequenceTier.HIGH, "半神级别，掌握秘术", listOf("秘术施展", "空间操控", "封印")),
                Pathway.Sequence(3, "漫游者", Pathway.SequenceTier.HIGH, "自由穿梭各处", listOf("自由漫游", "空间穿越", "维度行走")),
                Pathway.Sequence(2, "旅法师", Pathway.SequenceTier.DEMIGOD, "天使级别，掌控旅行", listOf("旅行法则", "空间主宰", "传送掌控")),
                Pathway.Sequence(1, "星之匙", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("星辰之力", "空间钥匙", "维度开启")),
                Pathway.Sequence(0, "门", Pathway.SequenceTier.TRUE_GOD, "真神，掌控空间与门", listOf("空间权柄", "门之法则", "维度主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "诡秘之主",
                titles = listOf("时空之王", "命运道标", "源堡化身", "灵界支配者"),
                description = "与占卜家、偷盗者途径相邻，共同归属诡秘之主"
            ),
            substance = "源堡",
            description = "学徒途径是与空间、旅行、门相关的途径，能够自由穿梭空间，掌控传送与维度。",
            themes = listOf("空间", "旅行", "门", "知识", "戏法"),
            abilities = listOf("空间跳跃", "传送门", "信息记录", "星象占卜", "维度行走", "空间操控")
        ),
        
        Pathway(
            id = "marauder",
            name = "偷盗者途径",
            sequences = listOf(
                Pathway.Sequence(9, "偷盗者", Pathway.SequenceTier.LOW, "能够偷盗物品和概念", listOf("偷盗", "隐匿", "开锁")),
                Pathway.Sequence(8, "诈骗师", Pathway.SequenceTier.LOW, "精通欺骗和诈骗", listOf("欺骗", "伪装", "话术")),
                Pathway.Sequence(7, "解密学者", Pathway.SequenceTier.MIDDLE, "破解各种密码和谜题", listOf("解密", "密码学", "谜题破解")),
                Pathway.Sequence(6, "盗火人", Pathway.SequenceTier.MIDDLE, "能够盗取能力和火焰", listOf("能力盗取", "火焰操控", "力量窃取")),
                Pathway.Sequence(5, "窃梦家", Pathway.SequenceTier.MIDDLE, "进入和操控梦境", listOf("梦境进入", "梦境操控", "记忆窃取")),
                Pathway.Sequence(4, "寄生者", Pathway.SequenceTier.HIGH, "能够寄生他人", listOf("寄生", "意识操控", "身体夺取")),
                Pathway.Sequence(3, "欺瞒导师", Pathway.SequenceTier.HIGH, "精通各种欺骗手段", listOf("欺骗法则", "真相扭曲", "认知操控")),
                Pathway.Sequence(2, "命运木马", Pathway.SequenceTier.DEMIGOD, "天使级别，操控命运", listOf("命运篡改", "因果操控", "命运窃取")),
                Pathway.Sequence(1, "时之虫", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("时间操控", "时间窃取", "历史篡改")),
                Pathway.Sequence(0, "错误", Pathway.SequenceTier.TRUE_GOD, "真神，掌控错误与欺诈", listOf("错误权柄", "欺诈法则", "命运主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "诡秘之主",
                titles = listOf("时空之王", "命运道标", "源堡化身", "灵界支配者"),
                description = "与占卜家、学徒途径相邻，共同归属诡秘之主"
            ),
            substance = "源堡",
            description = "偷盗者途径是与偷盗、欺骗、错误相关的途径，能够窃取物品、能力甚至命运。",
            themes = listOf("偷盗", "欺骗", "错误", "命运", "时间"),
            abilities = listOf("偷盗", "欺骗", "解密", "能力窃取", "梦境操控", "命运篡改", "时间操控")
        ),
        
        Pathway(
            id = "secrets_supplicant",
            name = "秘祈人途径",
            sequences = listOf(
                Pathway.Sequence(9, "秘祈人", Pathway.SequenceTier.LOW, "能够向神灵祈祷", listOf("祈祷", "神恩", "信仰之力")),
                Pathway.Sequence(8, "倾听者", Pathway.SequenceTier.LOW, "能够倾听神灵的声音", listOf("倾听", "神谕", "心灵感应")),
                Pathway.Sequence(7, "隐修士", Pathway.SequenceTier.MIDDLE, "隐居修行获得力量", listOf("隐匿", "冥想", "精神修行")),
                Pathway.Sequence(6, "蔷薇主教", Pathway.SequenceTier.MIDDLE, "掌握蔷薇之力", listOf("蔷薇魔法", "守护", "祝福")),
                Pathway.Sequence(5, "牧羊人", Pathway.SequenceTier.MIDDLE, "引导和守护他人", listOf("引导", "守护", "牧养")),
                Pathway.Sequence(4, "黑骑士", Pathway.SequenceTier.HIGH, "半神级别，黑暗骑士", listOf("黑暗之力", "骑士技能", "战斗精通")),
                Pathway.Sequence(3, "三首圣堂", Pathway.SequenceTier.HIGH, "拥有三首的圣堂", listOf("三首之力", "圣堂守护", "多重意识")),
                Pathway.Sequence(2, "秽语长老", Pathway.SequenceTier.DEMIGOD, "天使级别，秽语之力", listOf("秽语诅咒", "语言力量", "禁忌知识")),
                Pathway.Sequence(1, "暗天使", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("黑暗之力", "天使之力", "神恩")),
                Pathway.Sequence(0, "倒吊人", Pathway.SequenceTier.TRUE_GOD, "真神，掌控牺牲与救赎", listOf("牺牲权柄", "救赎法则", "倒吊人之力"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "上帝",
                titles = listOf("创造者", "造物主", "全知全能者", "星界之主"),
                description = "最初造物主分裂出的支柱之一，掌控混沌海"
            ),
            substance = "混沌海",
            description = "秘祈人途径是与祈祷、牺牲、救赎相关的途径，能够获得神恩，掌控黑暗与光明。",
            themes = listOf("祈祷", "牺牲", "救赎", "黑暗", "信仰"),
            abilities = listOf("祈祷", "神恩", "隐匿", "守护", "黑暗之力", "天使之力")
        ),
        
        Pathway(
            id = "spectator",
            name = "观众途径",
            sequences = listOf(
                Pathway.Sequence(9, "观众", Pathway.SequenceTier.LOW, "能够观察他人", listOf("观察", "情绪感知", "心理分析")),
                Pathway.Sequence(8, "读心者", Pathway.SequenceTier.LOW, "能够读取他人心思", listOf("读心", "心灵感应", "思维窥视")),
                Pathway.Sequence(7, "心理医生", Pathway.SequenceTier.MIDDLE, "治疗心理疾病", listOf("心理治疗", "情绪操控", "精神引导")),
                Pathway.Sequence(6, "催眠师", Pathway.SequenceTier.MIDDLE, "精通催眠术", listOf("催眠", "暗示", "潜意识操控")),
                Pathway.Sequence(5, "梦境行者", Pathway.SequenceTier.MIDDLE, "能够进入梦境", listOf("梦境进入", "梦境操控", "潜意识行走")),
                Pathway.Sequence(4, "操纵师", Pathway.SequenceTier.HIGH, "半神级别，操控他人", listOf("心灵操控", "意识操纵", "精神控制")),
                Pathway.Sequence(3, "织梦人", Pathway.SequenceTier.HIGH, "编织梦境", listOf("梦境编织", "现实幻境", "意识创造")),
                Pathway.Sequence(2, "洞察者", Pathway.SequenceTier.DEMIGOD, "天使级别，洞察一切", listOf("洞察", "全知", "真相揭示")),
                Pathway.Sequence(1, "作家", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("书写命运", "故事创造", "现实改写")),
                Pathway.Sequence(0, "空想家", Pathway.SequenceTier.TRUE_GOD, "真神，掌控思想与想象", listOf("思想权柄", "想象法则", "现实塑造"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "上帝",
                titles = listOf("创造者", "造物主", "全知全能者", "星界之主"),
                description = "与秘祈人、水手、歌颂者、阅读者途径相邻，共同归属上帝"
            ),
            substance = "混沌海",
            description = "观众途径是与心灵、梦境、思想相关的途径，能够操控心灵，编织梦境，洞察真相。",
            themes = listOf("心灵", "梦境", "思想", "观察", "操控"),
            abilities = listOf("读心", "催眠", "梦境操控", "心灵操控", "洞察", "现实改写")
        ),
        
        Pathway(
            id = "sailor",
            name = "水手途径",
            sequences = listOf(
                Pathway.Sequence(9, "水手", Pathway.SequenceTier.LOW, "精通航海技能", listOf("航海", "游泳", "天气感知")),
                Pathway.Sequence(8, "暴怒之民", Pathway.SequenceTier.LOW, "能够操控暴怒之力", listOf("暴怒之力", "力量增强", "战斗狂暴")),
                Pathway.Sequence(7, "航海家", Pathway.SequenceTier.MIDDLE, "精通航海导航", listOf("导航", "天气操控", "海洋感知")),
                Pathway.Sequence(6, "风眷者", Pathway.SequenceTier.MIDDLE, "掌控风的力量", listOf("风操控", "飞行", "风暴召唤")),
                Pathway.Sequence(5, "海洋歌者", Pathway.SequenceTier.MIDDLE, "歌唱操控海洋", listOf("海洋之歌", "海浪操控", "海洋生物沟通")),
                Pathway.Sequence(4, "灾难主祭", Pathway.SequenceTier.HIGH, "半神级别，主祭灾难", listOf("灾难召唤", "天气操控", "祭祀之力")),
                Pathway.Sequence(3, "海王", Pathway.SequenceTier.HIGH, "海洋之王", listOf("海洋主宰", "海神之力", "海洋领域")),
                Pathway.Sequence(2, "天灾", Pathway.SequenceTier.DEMIGOD, "天使级别，天灾化身", listOf("天灾之力", "灾难操控", "自然之力")),
                Pathway.Sequence(1, "雷神", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("雷霆之力", "闪电操控", "风暴主宰")),
                Pathway.Sequence(0, "暴君", Pathway.SequenceTier.TRUE_GOD, "真神，掌控风暴与海洋", listOf("风暴权柄", "海洋法则", "雷霆主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "上帝",
                titles = listOf("创造者", "造物主", "全知全能者", "星界之主"),
                description = "与秘祈人、观众、歌颂者、阅读者途径相邻，共同归属上帝"
            ),
            substance = "混沌海",
            description = "水手途径是与海洋、风暴、雷霆相关的途径，能够操控天气，主宰海洋，召唤天灾。",
            themes = listOf("海洋", "风暴", "雷霆", "灾难", "力量"),
            abilities = listOf("航海", "风操控", "海洋操控", "雷霆之力", "灾难召唤", "天气操控")
        ),
        
        Pathway(
            id = "sun",
            name = "歌颂者途径",
            sequences = listOf(
                Pathway.Sequence(9, "歌颂者", Pathway.SequenceTier.LOW, "歌颂神灵获得力量", listOf("歌颂", "祝福", "净化")),
                Pathway.Sequence(8, "祈光人", Pathway.SequenceTier.LOW, "祈求光明之力", listOf("光明祈求", "光之祝福", "黑暗驱散")),
                Pathway.Sequence(7, "太阳神官", Pathway.SequenceTier.MIDDLE, "侍奉太阳神", listOf("太阳之力", "光明魔法", "神圣仪式")),
                Pathway.Sequence(6, "公证人", Pathway.SequenceTier.MIDDLE, "公正裁决", listOf("公正", "裁决", "真相揭示")),
                Pathway.Sequence(5, "光之祭司", Pathway.SequenceTier.MIDDLE, "光明祭司", listOf("光明祭祀", "神圣之光", "净化之力")),
                Pathway.Sequence(4, "无暗者", Pathway.SequenceTier.HIGH, "半神级别，无暗之光", listOf("无暗之光", "光明领域", "黑暗驱散")),
                Pathway.Sequence(3, "正义导师", Pathway.SequenceTier.HIGH, "正义的导师", listOf("正义之力", "审判", "道德引导")),
                Pathway.Sequence(2, "逐光者", Pathway.SequenceTier.DEMIGOD, "天使级别，追逐光明", listOf("光明追逐", "光之化身", "神圣之力")),
                Pathway.Sequence(1, "纯白天使", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("纯白之光", "天使之力", "神圣化身")),
                Pathway.Sequence(0, "太阳", Pathway.SequenceTier.TRUE_GOD, "真神，掌控光明与太阳", listOf("太阳权柄", "光明法则", "神圣主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "上帝",
                titles = listOf("创造者", "造物主", "全知全能者", "星界之主"),
                description = "与秘祈人、观众、水手、阅读者途径相邻，共同归属上帝"
            ),
            substance = "混沌海",
            description = "歌颂者途径是与光明、太阳、正义相关的途径，能够操控光明，净化黑暗，审判邪恶。",
            themes = listOf("光明", "太阳", "正义", "净化", "神圣"),
            abilities = listOf("歌颂", "光明之力", "净化", "审判", "神圣之光", "太阳之力")
        ),
        
        Pathway(
            id = "reader",
            name = "阅读者途径",
            sequences = listOf(
                Pathway.Sequence(9, "阅读者", Pathway.SequenceTier.LOW, "快速阅读和理解", listOf("快速阅读", "知识获取", "记忆增强")),
                Pathway.Sequence(8, "推理学员", Pathway.SequenceTier.LOW, "逻辑推理能力", listOf("推理", "逻辑分析", "谜题破解")),
                Pathway.Sequence(7, "守知者", Pathway.SequenceTier.MIDDLE, "守护知识", listOf("知识守护", "秘密保护", "信息屏蔽")),
                Pathway.Sequence(6, "博学者", Pathway.SequenceTier.MIDDLE, "博学多才", listOf("博学", "知识运用", "技能掌握")),
                Pathway.Sequence(5, "秘术导师", Pathway.SequenceTier.MIDDLE, "秘术导师", listOf("秘术教学", "知识传授", "能力引导")),
                Pathway.Sequence(4, "预言家", Pathway.SequenceTier.HIGH, "半神级别，预言未来", listOf("预言", "未来窥视", "命运感知")),
                Pathway.Sequence(3, "洞悉者", Pathway.SequenceTier.HIGH, "洞悉一切", listOf("洞悉", "全知", "真相揭示")),
                Pathway.Sequence(2, "智天使", Pathway.SequenceTier.DEMIGOD, "天使级别，智慧天使", listOf("智慧之力", "天使之力", "知识主宰")),
                Pathway.Sequence(1, "全知之眼", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("全知", "真相洞察", "知识掌控")),
                Pathway.Sequence(0, "白塔", Pathway.SequenceTier.TRUE_GOD, "真神，掌控知识与智慧", listOf("知识权柄", "智慧法则", "全知全能"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "上帝",
                titles = listOf("创造者", "造物主", "全知全能者", "星界之主"),
                description = "与秘祈人、观众、水手、歌颂者途径相邻，共同归属上帝"
            ),
            substance = "混沌海",
            description = "阅读者途径是与知识、智慧、预言相关的途径，能够获取知识，洞悉真相，预言未来。",
            themes = listOf("知识", "智慧", "预言", "阅读", "真相"),
            abilities = listOf("快速阅读", "推理", "知识获取", "预言", "洞悉", "全知")
        ),
        
        Pathway(
            id = "warrior",
            name = "战士途径",
            sequences = listOf(
                Pathway.Sequence(9, "战士", Pathway.SequenceTier.LOW, "战斗专家", listOf("战斗精通", "武器使用", "体魄强化")),
                Pathway.Sequence(8, "格斗家", Pathway.SequenceTier.LOW, "格斗高手", listOf("格斗技巧", "身体强化", "战斗直觉")),
                Pathway.Sequence(7, "武器大师", Pathway.SequenceTier.MIDDLE, "精通各种武器", listOf("武器精通", "武器强化", "战斗策略")),
                Pathway.Sequence(6, "黎明骑士", Pathway.SequenceTier.MIDDLE, "黎明骑士", listOf("骑士之力", "光明加持", "战斗荣耀")),
                Pathway.Sequence(5, "守护者", Pathway.SequenceTier.MIDDLE, "守护他人", listOf("守护", "防御强化", "保护之力")),
                Pathway.Sequence(4, "猎魔者", Pathway.SequenceTier.HIGH, "半神级别，猎杀恶魔", listOf("恶魔猎杀", "神圣之力", "净化")),
                Pathway.Sequence(3, "银骑士", Pathway.SequenceTier.HIGH, "银色骑士", listOf("银色之力", "骑士荣耀", "神圣骑士")),
                Pathway.Sequence(2, "荣耀者", Pathway.SequenceTier.DEMIGOD, "天使级别，荣耀化身", listOf("荣耀之力", "战斗主宰", "神圣之力")),
                Pathway.Sequence(1, "神明之手", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("神明之力", "战斗神力", "神圣之手")),
                Pathway.Sequence(0, "黄昏巨人", Pathway.SequenceTier.TRUE_GOD, "真神，掌控战斗与黄昏", listOf("战斗权柄", "黄昏法则", "力量主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "万物奇点",
                titles = listOf("时空归一者"),
                description = "掌控永暗之河"
            ),
            substance = "永暗之河",
            description = "战士途径是与战斗、守护、荣耀相关的途径，能够强化战斗能力，守护他人，获得荣耀。",
            themes = listOf("战斗", "守护", "荣耀", "力量", "黄昏"),
            abilities = listOf("战斗精通", "武器使用", "守护", "猎魔", "荣耀之力", "神圣之力")
        ),
        
        Pathway(
            id = "sleepless",
            name = "不眠者途径",
            sequences = listOf(
                Pathway.Sequence(9, "不眠者", Pathway.SequenceTier.LOW, "不需要睡眠", listOf("不眠", "夜间视觉", "精神强化")),
                Pathway.Sequence(8, "午夜诗人", Pathway.SequenceTier.LOW, "午夜诗人", listOf("诗歌", "情绪操控", "夜间力量")),
                Pathway.Sequence(7, "梦魇", Pathway.SequenceTier.MIDDLE, "操控噩梦", listOf("噩梦操控", "梦境进入", "恐惧之力")),
                Pathway.Sequence(6, "安魂师", Pathway.SequenceTier.MIDDLE, "安抚灵魂", listOf("安魂", "精神治疗", "灵魂操控")),
                Pathway.Sequence(5, "灵巫", Pathway.SequenceTier.MIDDLE, "灵体巫师", listOf("灵体操控", "灵魂魔法", "灵视")),
                Pathway.Sequence(4, "守夜人", Pathway.SequenceTier.HIGH, "半神级别，守护夜晚", listOf("夜间守护", "黑暗之力", "光明对抗")),
                Pathway.Sequence(3, "恐惧主教", Pathway.SequenceTier.HIGH, "恐惧主教", listOf("恐惧之力", "黑暗操控", "心灵恐惧")),
                Pathway.Sequence(2, "隐秘之仆", Pathway.SequenceTier.DEMIGOD, "天使级别，隐秘仆人", listOf("隐秘之力", "黑暗领域", "神秘之力")),
                Pathway.Sequence(1, "厄难骑士", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("厄难之力", "黑暗骑士", "灾难之力")),
                Pathway.Sequence(0, "黑暗", Pathway.SequenceTier.TRUE_GOD, "真神，掌控黑暗与夜晚", listOf("黑暗权柄", "夜晚法则", "隐秘主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "万物奇点",
                titles = listOf("时空归一者"),
                description = "与战士、收尸人途径相邻，共同归属永暗之河"
            ),
            substance = "永暗之河",
            description = "不眠者途径是与夜晚、黑暗、恐惧相关的途径，能够操控黑暗，引发恐惧，守护夜晚。",
            themes = listOf("夜晚", "黑暗", "恐惧", "梦境", "隐秘"),
            abilities = listOf("不眠", "噩梦操控", "安魂", "灵体操控", "恐惧之力", "黑暗操控")
        ),
        
        Pathway(
            id = "corpse_collector",
            name = "收尸人途径",
            sequences = listOf(
                Pathway.Sequence(9, "收尸人", Pathway.SequenceTier.LOW, "处理尸体", listOf("尸体处理", "死亡感知", "亡者沟通")),
                Pathway.Sequence(8, "掘墓人", Pathway.SequenceTier.LOW, "挖掘坟墓", listOf("坟墓挖掘", "尸体操控", "死亡之力")),
                Pathway.Sequence(7, "通灵者", Pathway.SequenceTier.MIDDLE, "与亡灵沟通", listOf("通灵", "亡者召唤", "灵魂沟通")),
                Pathway.Sequence(6, "死灵导师", Pathway.SequenceTier.MIDDLE, "死灵魔法导师", listOf("死灵魔法", "亡者操控", "死亡领域")),
                Pathway.Sequence(5, "看门人", Pathway.SequenceTier.MIDDLE, "看守生死之门", listOf("生死之门", "灵魂引导", "冥界之力")),
                Pathway.Sequence(4, "不死者", Pathway.SequenceTier.HIGH, "半神级别，不死之身", listOf("不死", "再生", "死亡免疫")),
                Pathway.Sequence(3, "摆渡人", Pathway.SequenceTier.HIGH, "摆渡灵魂", listOf("灵魂摆渡", "冥河操控", "生死引导")),
                Pathway.Sequence(2, "死亡执政官", Pathway.SequenceTier.DEMIGOD, "天使级别，死亡执政", listOf("死亡执政", "死神之力", "冥界主宰")),
                Pathway.Sequence(1, "苍白皇帝", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("苍白之力", "死亡皇帝", "冥界皇帝")),
                Pathway.Sequence(0, "死神", Pathway.SequenceTier.TRUE_GOD, "真神，掌控死亡与冥界", listOf("死亡权柄", "冥界法则", "生死主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "万物奇点",
                titles = listOf("时空归一者"),
                description = "与战士、不眠者途径相邻，共同归属永暗之河"
            ),
            substance = "永暗之河",
            description = "收尸人途径是与死亡、亡灵、冥界相关的途径，能够操控亡灵，引导灵魂，掌控生死。",
            themes = listOf("死亡", "亡灵", "冥界", "灵魂", "生死"),
            abilities = listOf("尸体处理", "通灵", "死灵魔法", "灵魂引导", "不死", "死亡之力")
        ),
        
        Pathway(
            id = "hunter",
            name = "猎人途径",
            sequences = listOf(
                Pathway.Sequence(9, "猎人", Pathway.SequenceTier.LOW, "狩猎专家", listOf("狩猎", "追踪", "陷阱")),
                Pathway.Sequence(8, "挑衅者", Pathway.SequenceTier.LOW, "挑衅他人", listOf("挑衅", "情绪操控", "战斗引导")),
                Pathway.Sequence(7, "纵火家", Pathway.SequenceTier.MIDDLE, "操控火焰", listOf("火焰操控", "火焰创造", "燃烧")),
                Pathway.Sequence(6, "阴谋家", Pathway.SequenceTier.MIDDLE, "策划阴谋", listOf("阴谋策划", "策略", "计谋")),
                Pathway.Sequence(5, "收割者", Pathway.SequenceTier.MIDDLE, "收割生命", listOf("生命收割", "死亡之力", "战斗强化")),
                Pathway.Sequence(4, "铁血骑士", Pathway.SequenceTier.HIGH, "半神级别，铁血战士", listOf("铁血之力", "战斗强化", "意志强化")),
                Pathway.Sequence(3, "战争主教", Pathway.SequenceTier.HIGH, "战争主教", listOf("战争之力", "战斗祝福", "军队强化")),
                Pathway.Sequence(2, "天气术士", Pathway.SequenceTier.DEMIGOD, "天使级别，操控天气", listOf("天气操控", "天灾召唤", "自然之力")),
                Pathway.Sequence(1, "征服者", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("征服之力", "战争主宰", "胜利法则")),
                Pathway.Sequence(0, "红祭司", Pathway.SequenceTier.TRUE_GOD, "真神，掌控战争与火焰", listOf("战争权柄", "火焰法则", "征服主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "根祸之源",
                titles = listOf(),
                description = "掌控灾祸之城"
            ),
            substance = "灾祸之城",
            description = "猎人途径是与战争、火焰、征服相关的途径，能够操控火焰，策划战争，征服一切。",
            themes = listOf("战争", "火焰", "征服", "狩猎", "阴谋"),
            abilities = listOf("狩猎", "火焰操控", "阴谋策划", "战争之力", "天气操控", "征服之力")
        ),
        
        Pathway(
            id = "assassin",
            name = "刺客途径",
            sequences = listOf(
                Pathway.Sequence(9, "刺客", Pathway.SequenceTier.LOW, "暗杀专家", listOf("暗杀", "隐匿", "毒药")),
                Pathway.Sequence(8, "教唆者", Pathway.SequenceTier.LOW, "教唆他人", listOf("教唆", "情绪操控", "言语蛊惑")),
                Pathway.Sequence(7, "女巫", Pathway.SequenceTier.MIDDLE, "女巫之力", listOf("女巫魔法", "诅咒", "黑魔法")),
                Pathway.Sequence(6, "欢愉", Pathway.SequenceTier.MIDDLE, "欢愉之力", listOf("欢愉操控", "情绪操控", "欲望引导")),
                Pathway.Sequence(5, "痛苦", Pathway.SequenceTier.MIDDLE, "痛苦之力", listOf("痛苦操控", "折磨", "精神攻击")),
                Pathway.Sequence(4, "绝望", Pathway.SequenceTier.HIGH, "半神级别，绝望之力", listOf("绝望之力", "精神崩溃", "希望毁灭")),
                Pathway.Sequence(3, "不老", Pathway.SequenceTier.HIGH, "不老不死", listOf("不老", "青春永驻", "时间抵抗")),
                Pathway.Sequence(2, "灾难", Pathway.SequenceTier.DEMIGOD, "天使级别，灾难化身", listOf("灾难之力", "灾祸召唤", "毁灭")),
                Pathway.Sequence(1, "末日", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("末日之力", "毁灭法则", "终结")),
                Pathway.Sequence(0, "魔女", Pathway.SequenceTier.TRUE_GOD, "真神，掌控灾难与魔女", listOf("魔女权柄", "灾难法则", "毁灭主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "根祸之源",
                titles = listOf(),
                description = "与猎人途径相邻，共同归属灾祸之城"
            ),
            substance = "灾祸之城",
            description = "刺客途径是与暗杀、灾难、魔女相关的途径，能够操控灾难，引发毁灭，成为魔女。",
            themes = listOf("暗杀", "灾难", "魔女", "痛苦", "毁灭"),
            abilities = listOf("暗杀", "教唆", "女巫魔法", "痛苦操控", "灾难之力", "毁灭之力")
        ),
        
        Pathway(
            id = "savant",
            name = "通识者途径",
            sequences = listOf(
                Pathway.Sequence(9, "通识者", Pathway.SequenceTier.LOW, "博学多才", listOf("知识获取", "技能学习", "信息处理")),
                Pathway.Sequence(8, "考古学家", Pathway.SequenceTier.LOW, "考古专家", listOf("考古", "历史知识", "遗迹探索")),
                Pathway.Sequence(7, "鉴定师", Pathway.SequenceTier.MIDDLE, "鉴定物品", listOf("物品鉴定", "价值评估", "真伪辨别")),
                Pathway.Sequence(6, "机械专家", Pathway.SequenceTier.MIDDLE, "机械制造", listOf("机械制造", "机械操控", "发明创造")),
                Pathway.Sequence(5, "天文学家", Pathway.SequenceTier.MIDDLE, "天文学专家", listOf("星象观测", "天体知识", "宇宙感知")),
                Pathway.Sequence(4, "炼金术士", Pathway.SequenceTier.HIGH, "半神级别，炼金术", listOf("炼金术", "物质转化", "生命创造")),
                Pathway.Sequence(3, "奥秘学者", Pathway.SequenceTier.HIGH, "奥秘学者", listOf("奥秘知识", "神秘学", "真理探索")),
                Pathway.Sequence(2, "知识导师", Pathway.SequenceTier.DEMIGOD, "天使级别，知识导师", listOf("知识传授", "智慧引导", "真理揭示")),
                Pathway.Sequence(1, "启蒙者", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("启蒙", "智慧之光", "真理化身")),
                Pathway.Sequence(0, "完美者", Pathway.SequenceTier.TRUE_GOD, "真神，掌控知识与完美", listOf("知识权柄", "完美法则", "真理主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "知识之妖",
                titles = listOf("疯狂奥秘"),
                description = "掌控知识荒野"
            ),
            substance = "知识荒野",
            description = "通识者途径是与知识、科技、炼金相关的途径，能够获取知识，创造发明，掌控真理。",
            themes = listOf("知识", "科技", "炼金", "发明", "真理"),
            abilities = listOf("知识获取", "考古", "机械制造", "炼金术", "奥秘知识", "真理揭示")
        ),
        
        Pathway(
            id = "mystery_pryer",
            name = "窥秘人途径",
            sequences = listOf(
                Pathway.Sequence(9, "窥秘人", Pathway.SequenceTier.LOW, "窥探秘密", listOf("秘密窥探", "隐秘感知", "神秘感知")),
                Pathway.Sequence(8, "格斗学者", Pathway.SequenceTier.LOW, "格斗学者", listOf("格斗", "学术研究", "战斗学习")),
                Pathway.Sequence(7, "巫师", Pathway.SequenceTier.MIDDLE, "巫师之力", listOf("巫术", "魔法", "神秘力量")),
                Pathway.Sequence(6, "卷轴教授", Pathway.SequenceTier.MIDDLE, "卷轴教授", listOf("卷轴制作", "知识传授", "魔法记录")),
                Pathway.Sequence(5, "星象师", Pathway.SequenceTier.MIDDLE, "星象专家", listOf("星象占卜", "命运窥视", "宇宙感知")),
                Pathway.Sequence(4, "神秘学家", Pathway.SequenceTier.HIGH, "半神级别，神秘学", listOf("神秘学", "禁忌知识", "奥秘掌控")),
                Pathway.Sequence(3, "预言大师", Pathway.SequenceTier.HIGH, "预言大师", listOf("预言", "未来窥视", "命运感知")),
                Pathway.Sequence(2, "贤者", Pathway.SequenceTier.DEMIGOD, "天使级别，贤者", listOf("智慧", "真理", "全知")),
                Pathway.Sequence(1, "知识皇帝", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("知识主宰", "真理皇帝", "智慧化身")),
                Pathway.Sequence(0, "隐者", Pathway.SequenceTier.TRUE_GOD, "真神，掌控隐秘与知识", listOf("隐秘权柄", "知识法则", "真理主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "知识之妖",
                titles = listOf("疯狂奥秘"),
                description = "与通识者途径相邻，共同归属知识荒野"
            ),
            substance = "知识荒野",
            description = "窥秘人途径是与隐秘、神秘学、预言相关的途径，能够窥探秘密，预言未来，掌控真理。",
            themes = listOf("隐秘", "神秘学", "预言", "知识", "真理"),
            abilities = listOf("秘密窥探", "巫术", "星象占卜", "神秘学", "预言", "智慧")
        ),
        
        Pathway(
            id = "monster",
            name = "怪物途径",
            sequences = listOf(
                Pathway.Sequence(9, "怪物", Pathway.SequenceTier.LOW, "怪物之力", listOf("怪物化", "异变", "力量增强")),
                Pathway.Sequence(8, "机器", Pathway.SequenceTier.LOW, "机器之力", listOf("机械化", "精确", "计算")),
                Pathway.Sequence(7, "幸运者", Pathway.SequenceTier.MIDDLE, "幸运之力", listOf("幸运", "概率操控", "好运")),
                Pathway.Sequence(6, "灾祸教士", Pathway.SequenceTier.MIDDLE, "灾祸教士", listOf("灾祸", "灾难引导", "厄运")),
                Pathway.Sequence(5, "赢家", Pathway.SequenceTier.MIDDLE, "赢家之力", listOf("胜利", "成功", "好运")),
                Pathway.Sequence(4, "厄运法师", Pathway.SequenceTier.HIGH, "半神级别，厄运法师", listOf("厄运", "诅咒", "灾难")),
                Pathway.Sequence(3, "混乱行者", Pathway.SequenceTier.HIGH, "混乱行者", listOf("混乱", "无序", "混沌")),
                Pathway.Sequence(2, "先知", Pathway.SequenceTier.DEMIGOD, "天使级别，先知", listOf("预言", "未来窥视", "命运感知")),
                Pathway.Sequence(1, "水银之蛇", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("水银之力", "命运操控", "时间扭曲")),
                Pathway.Sequence(0, "命运之轮", Pathway.SequenceTier.TRUE_GOD, "真神，掌控命运与混沌", listOf("命运权柄", "混沌法则", "轮回主宰"))
            ),
            source = PathwaySource.ORIGINAL_CREATOR,
            oldOne = OldOne(
                name = "无尽的混乱",
                titles = listOf("命运化身"),
                description = "掌控光之钥"
            ),
            substance = "光之钥",
            description = "怪物途径是与命运、混沌、幸运相关的途径，能够操控命运，引发混乱，掌控轮回。",
            themes = listOf("命运", "混沌", "幸运", "灾祸", "轮回"),
            abilities = listOf("怪物化", "幸运", "灾祸", "厄运", "混乱", "命运操控")
        ),
        
        Pathway(
            id = "moon",
            name = "药师途径",
            sequences = listOf(
                Pathway.Sequence(9, "药师", Pathway.SequenceTier.LOW, "药剂专家", listOf("药剂制作", "治疗", "药物知识")),
                Pathway.Sequence(8, "驯兽师", Pathway.SequenceTier.LOW, "驯服野兽", listOf("驯兽", "动物沟通", "野兽控制")),
                Pathway.Sequence(7, "吸血鬼", Pathway.SequenceTier.MIDDLE, "吸血鬼之力", listOf("吸血", "血液操控", "夜行")),
                Pathway.Sequence(6, "魔药教授", Pathway.SequenceTier.MIDDLE, "魔药教授", listOf("魔药教学", "知识传授", "药剂精通")),
                Pathway.Sequence(5, "深红学者", Pathway.SequenceTier.MIDDLE, "深红学者", listOf("深红之力", "血液知识", "生命研究")),
                Pathway.Sequence(4, "巫王", Pathway.SequenceTier.HIGH, "半神级别，巫王", listOf("巫术", "诅咒", "黑暗之力")),
                Pathway.Sequence(3, "召唤大师", Pathway.SequenceTier.HIGH, "召唤大师", listOf("召唤", "异界生物", "契约")),
                Pathway.Sequence(2, "创生者", Pathway.SequenceTier.DEMIGOD, "天使级别，创生者", listOf("生命创造", "创生", "生命操控")),
                Pathway.Sequence(1, "美神", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("美丽", "魅力", "诱惑")),
                Pathway.Sequence(0, "月亮", Pathway.SequenceTier.TRUE_GOD, "真神，掌控月亮与生命", listOf("月亮权柄", "生命法则", "美丽主宰"))
            ),
            source = PathwaySource.OUTER_GOD,
            oldOne = OldOne(
                name = "堕落母神",
                titles = listOf("邪恶之始", "不灭者", "现实世界的主宰"),
                description = "来自星空的外神，掌控母巢"
            ),
            substance = "母巢",
            description = "药师途径是与月亮、生命、血液相关的途径，来自外神堕落母神，能够操控生命，创造生物。",
            themes = listOf("月亮", "生命", "血液", "驯兽", "创生"),
            abilities = listOf("药剂制作", "驯兽", "吸血", "巫术", "召唤", "生命创造")
        ),
        
        Pathway(
            id = "mother",
            name = "耕种者途径",
            sequences = listOf(
                Pathway.Sequence(9, "耕种者", Pathway.SequenceTier.LOW, "耕种专家", listOf("耕种", "植物操控", "农业知识")),
                Pathway.Sequence(8, "医师", Pathway.SequenceTier.LOW, "医疗专家", listOf("医疗", "治疗", "诊断")),
                Pathway.Sequence(7, "丰收祭司", Pathway.SequenceTier.MIDDLE, "丰收祭司", listOf("丰收", "作物祝福", "自然之力")),
                Pathway.Sequence(6, "生物学家", Pathway.SequenceTier.MIDDLE, "生物学专家", listOf("生物研究", "生命操控", "基因改造")),
                Pathway.Sequence(5, "德鲁伊", Pathway.SequenceTier.MIDDLE, "德鲁伊之力", listOf("自然之力", "动物变形", "自然魔法")),
                Pathway.Sequence(4, "古代炼金师", Pathway.SequenceTier.HIGH, "半神级别，古代炼金", listOf("古代炼金", "生命转化", "物质创造")),
                Pathway.Sequence(3, "抬棺人", Pathway.SequenceTier.HIGH, "抬棺人", listOf("死亡引导", "灵魂摆渡", "冥界之力")),
                Pathway.Sequence(2, "荒芜主母", Pathway.SequenceTier.DEMIGOD, "天使级别，荒芜主母", listOf("荒芜", "生命剥夺", "自然毁灭")),
                Pathway.Sequence(1, "自然行者", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("自然之力", "生命操控", "世界行走")),
                Pathway.Sequence(0, "母亲", Pathway.SequenceTier.TRUE_GOD, "真神，掌控生命与自然", listOf("生命权柄", "自然法则", "母亲之力"))
            ),
            source = PathwaySource.OUTER_GOD,
            oldOne = OldOne(
                name = "堕落母神",
                titles = listOf("邪恶之始", "不灭者", "现实世界的主宰"),
                description = "来自星空的外神，与药师途径相邻，共同归属母巢"
            ),
            substance = "母巢",
            description = "耕种者途径是与自然、生命、耕种相关的途径，来自外神堕落母神，能够操控自然，创造生命。",
            themes = listOf("自然", "生命", "耕种", "丰收", "母亲"),
            abilities = listOf("耕种", "医疗", "自然之力", "德鲁伊", "炼金", "生命操控")
        ),
        
        Pathway(
            id = "arbiter",
            name = "仲裁人途径",
            sequences = listOf(
                Pathway.Sequence(9, "仲裁人", Pathway.SequenceTier.LOW, "仲裁裁决", listOf("仲裁", "裁决", "公正")),
                Pathway.Sequence(8, "治安官", Pathway.SequenceTier.LOW, "维护治安", listOf("治安维护", "法律执行", "秩序")),
                Pathway.Sequence(7, "审讯者", Pathway.SequenceTier.MIDDLE, "审讯专家", listOf("审讯", "真相揭示", "心理攻势")),
                Pathway.Sequence(6, "法官", Pathway.SequenceTier.MIDDLE, "法官裁决", listOf("裁决", "法律", "公正")),
                Pathway.Sequence(5, "惩戒骑士", Pathway.SequenceTier.MIDDLE, "惩戒骑士", listOf("惩戒", "正义", "战斗")),
                Pathway.Sequence(4, "律令法师", Pathway.SequenceTier.HIGH, "半神级别，律令法师", listOf("律令", "法则操控", "规则制定")),
                Pathway.Sequence(3, "混乱猎手", Pathway.SequenceTier.HIGH, "混乱猎手", listOf("混乱猎杀", "秩序维护", "无序清除")),
                Pathway.Sequence(2, "平衡者", Pathway.SequenceTier.DEMIGOD, "天使级别，平衡者", listOf("平衡", "调和", "中庸")),
                Pathway.Sequence(1, "秩序之手", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("秩序", "法则", "规则")),
                Pathway.Sequence(0, "审判者", Pathway.SequenceTier.TRUE_GOD, "真神，掌控秩序与审判", listOf("审判权柄", "秩序法则", "公正主宰"))
            ),
            source = PathwaySource.OUTER_GOD,
            oldOne = OldOne(
                name = "秩序阴影",
                titles = listOf(),
                description = "来自星空的外神混沌之子，掌控失序之国"
            ),
            substance = "失序之国",
            description = "仲裁人途径是与秩序、审判、法律相关的途径，来自外神混沌之子，能够掌控秩序，审判一切。",
            themes = listOf("秩序", "审判", "法律", "公正", "平衡"),
            abilities = listOf("仲裁", "审讯", "裁决", "律令", "秩序", "平衡")
        ),
        
        Pathway(
            id = "lawyer",
            name = "律师途径",
            sequences = listOf(
                Pathway.Sequence(9, "律师", Pathway.SequenceTier.LOW, "法律专家", listOf("法律知识", "辩论", "契约")),
                Pathway.Sequence(8, "野蛮人", Pathway.SequenceTier.LOW, "野蛮之力", listOf("力量", "野蛮", "战斗")),
                Pathway.Sequence(7, "贿赂者", Pathway.SequenceTier.MIDDLE, "贿赂操控", listOf("贿赂", "利益交换", "交易")),
                Pathway.Sequence(6, "腐化男爵", Pathway.SequenceTier.MIDDLE, "腐化之力", listOf("腐化", "堕落", "诱惑")),
                Pathway.Sequence(5, "混乱导师", Pathway.SequenceTier.MIDDLE, "混乱引导", listOf("混乱", "无序", "混乱引导")),
                Pathway.Sequence(4, "堕落伯爵", Pathway.SequenceTier.HIGH, "半神级别，堕落伯爵", listOf("堕落", "黑暗之力", "腐化")),
                Pathway.Sequence(3, "狂乱法师", Pathway.SequenceTier.HIGH, "狂乱法师", listOf("狂乱", "混乱魔法", "无序")),
                Pathway.Sequence(2, "熵之公爵", Pathway.SequenceTier.DEMIGOD, "天使级别，熵之公爵", listOf("熵", "混乱", "无序")),
                Pathway.Sequence(1, "弑序亲王", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("弑序", "秩序破坏", "混乱")),
                Pathway.Sequence(0, "黑皇帝", Pathway.SequenceTier.TRUE_GOD, "真神，掌控混乱与黑暗", listOf("混乱权柄", "黑暗法则", "弑序主宰"))
            ),
            source = PathwaySource.OUTER_GOD,
            oldOne = OldOne(
                name = "秩序阴影",
                titles = listOf(),
                description = "与仲裁人途径相邻，共同归属失序之国"
            ),
            substance = "失序之国",
            description = "律师途径是与混乱、腐化、弑序相关的途径，来自外神混沌之子，能够引发混乱，破坏秩序。",
            themes = listOf("混乱", "腐化", "弑序", "黑暗", "熵"),
            abilities = listOf("法律", "贿赂", "腐化", "混乱", "堕落", "弑序")
        ),
        
        Pathway(
            id = "prisoner",
            name = "囚犯途径",
            sequences = listOf(
                Pathway.Sequence(9, "囚犯", Pathway.SequenceTier.LOW, "囚禁之力", listOf("囚禁", "束缚", "限制")),
                Pathway.Sequence(8, "疯子", Pathway.SequenceTier.LOW, "疯狂之力", listOf("疯狂", "混乱", "无序")),
                Pathway.Sequence(7, "狼人", Pathway.SequenceTier.MIDDLE, "狼人变身", listOf("狼人变身", "野性之力", "月圆之力")),
                Pathway.Sequence(6, "活尸", Pathway.SequenceTier.MIDDLE, "活尸之力", listOf("不死", "尸体操控", "死亡之力")),
                Pathway.Sequence(5, "怨魂", Pathway.SequenceTier.MIDDLE, "怨魂之力", listOf("怨念", "灵魂攻击", "诅咒")),
                Pathway.Sequence(4, "木偶", Pathway.SequenceTier.HIGH, "半神级别，木偶之力", listOf("木偶操控", "身体控制", "意识剥夺")),
                Pathway.Sequence(3, "沉默门徒", Pathway.SequenceTier.HIGH, "沉默门徒", listOf("沉默", "封印", "禁言")),
                Pathway.Sequence(2, "古代邪物", Pathway.SequenceTier.DEMIGOD, "天使级别，古代邪物", listOf("古代之力", "邪物", "禁忌")),
                Pathway.Sequence(1, "神孽", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("神孽", "亵渎", "禁忌")),
                Pathway.Sequence(0, "被缚者", Pathway.SequenceTier.TRUE_GOD, "真神，掌控束缚与诅咒", listOf("束缚权柄", "诅咒法则", "囚禁主宰"))
            ),
            source = PathwaySource.OUTER_GOD,
            oldOne = OldOne(
                name = "恶魔之父",
                titles = listOf("异类之主", "诅咒之源"),
                description = "来自星空的外神欲望母树，掌控暗影世界"
            ),
            substance = "暗影世界",
            description = "囚犯途径是与束缚、诅咒、疯狂相关的途径，来自外神欲望母树，能够操控束缚，施加诅咒。",
            themes = listOf("束缚", "诅咒", "疯狂", "囚禁", "禁忌"),
            abilities = listOf("囚禁", "疯狂", "狼人变身", "怨魂", "木偶操控", "诅咒")
        ),
        
        Pathway(
            id = "criminal",
            name = "罪犯途径",
            sequences = listOf(
                Pathway.Sequence(9, "罪犯", Pathway.SequenceTier.LOW, "犯罪专家", listOf("犯罪", "偷盗", "欺诈")),
                Pathway.Sequence(8, "折翼天使", Pathway.SequenceTier.LOW, "堕落天使", listOf("堕落", "黑暗之力", "天使之力")),
                Pathway.Sequence(7, "连环杀手", Pathway.SequenceTier.MIDDLE, "连环杀戮", listOf("杀戮", "暗杀", "追踪")),
                Pathway.Sequence(6, "恶魔", Pathway.SequenceTier.MIDDLE, "恶魔之力", listOf("恶魔", "黑暗", "诱惑")),
                Pathway.Sequence(5, "欲望使徒", Pathway.SequenceTier.MIDDLE, "欲望操控", listOf("欲望", "诱惑", "欲望引导")),
                Pathway.Sequence(4, "魔鬼", Pathway.SequenceTier.HIGH, "半神级别，魔鬼之力", listOf("魔鬼", "契约", "诱惑")),
                Pathway.Sequence(3, "呓语者", Pathway.SequenceTier.HIGH, "呓语之力", listOf("呓语", "疯狂", "混乱")),
                Pathway.Sequence(2, "鲜血大公", Pathway.SequenceTier.DEMIGOD, "天使级别，鲜血大公", listOf("鲜血", "血液操控", "生命之力")),
                Pathway.Sequence(1, "污秽君王", Pathway.SequenceTier.DEMIGOD, "接近真神的存在", listOf("污秽", "堕落", "黑暗")),
                Pathway.Sequence(0, "深渊", Pathway.SequenceTier.TRUE_GOD, "真神，掌控深渊与欲望", listOf("深渊权柄", "欲望法则", "堕落主宰"))
            ),
            source = PathwaySource.OUTER_GOD,
            oldOne = OldOne(
                name = "恶魔之父",
                titles = listOf("异类之主", "诅咒之源"),
                description = "与囚犯途径相邻，共同归属暗影世界"
            ),
            substance = "暗影世界",
            description = "罪犯途径是与犯罪、欲望、深渊相关的途径，来自外神欲望母树，能够操控欲望，堕入深渊。",
            themes = listOf("犯罪", "欲望", "深渊", "堕落", "鲜血"),
            abilities = listOf("犯罪", "堕落", "杀戮", "恶魔之力", "欲望操控", "鲜血之力")
        )
    )
    
    fun getPathwayById(id: String): Pathway? = ALL_PATHWAYS.find { it.id == id }
    
    fun getPathwayByName(name: String): Pathway? = ALL_PATHWAYS.find { 
        it.name.contains(name) || it.sequences.any { seq -> seq.name == name }
    }
    
    fun getSequenceByName(sequenceName: String): Pair<Pathway, Pathway.Sequence>? {
        for (pathway in ALL_PATHWAYS) {
            val sequence = pathway.sequences.find { it.name == sequenceName }
            if (sequence != null) {
                return Pair(pathway, sequence)
            }
        }
        return null
    }
    
    fun getAdjacentPathways(pathwayId: String): List<Pathway> {
        val pathway = getPathwayById(pathwayId) ?: return emptyList()
        return ALL_PATHWAYS.filter { 
            it != pathway && it.substance == pathway.substance 
        }
    }
    
    fun getPathwaysBySource(source: PathwaySource): List<Pathway> {
        return ALL_PATHWAYS.filter { it.source == source }
    }
    
    fun getPathwaysBySubstance(substance: String): List<Pathway> {
        return ALL_PATHWAYS.filter { it.substance == substance }
    }
}
