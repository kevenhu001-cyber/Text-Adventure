# 增强角色详情页面

## 概述

增强角色详情页面是一个全面的角色管理系统，提供了更详细和直观的角色状态展示。

## 功能特性

### 1. 多标签页设计
- **状态** - 核心状态指标和进度
- **属性** - 基础和精神属性
- **能力** - 技能树和能力列表
- **物品** - 装备和背包管理
- **关系** - 势力和人物关系
- **历史** - 历史日志和命运节点
- **神秘** - 神秘点和命运路径

### 2. 数据可视化
- 圆形进度环 - 灵性、疯狂、健康等核心指标
- 进度条 - 状态恢复和进度追踪
- 统计卡片 - 关键数据展示
- 属性网格 - 能力属性分布

### 3. 详细信息展示
- 状态效果 - 显示当前的增益和减益效果
- 关系网络 - 势力和人物关系图
- 历史日志 - 事件记录和影响
- 命运节点 - 关键事件和选择

## 数据模型

### EnhancedCharacterData
主要数据容器，包含：
- `baseState`: 基础玩家状态
- `health`: 健康状态
- `equippedItems`: 装备列表
- `activeStatuses`: 活动状态效果
- `skillTree`: 技能树
- `destinyPath`: 命运路径
- `mysteryPoints`: 神秘点
- `progressMetrics`: 进度指标

### PlayerState
玩家基础状态：
- 基本信息（姓名、身份、序列）
- 属性（力量、智力、敏捷等）
- 状态（灵性、疯狂、健康）
- 物品（装备、背包）
- 关系（势力、人物）
- 历史（日志、事件）

### HealthStatus
健康状态：
- 生理状态（生命、疲劳）
- 精神状态（ sanity、压力）
- 创伤和疾病

### ProgressMetrics
进度指标：
- 序列进度
- 灵性恢复进度
- 角色扮演进度
- 神秘解锁进度

## 使用示例

```kotlin
// 创建示例数据
val characterData = createSampleEnhancedCharacterData()

// 显示角色详情页面
EnhancedCharacterDetailScreen(
    characterData = characterData,
    onDismiss = { /* 关闭回调 */ }
)
```

## 组件说明

### StatCard
统计卡片组件，显示关键数据：
```kotlin
StatCard(
    title = "战斗力",
    value = "150",
    subtitle = "综合战力",
    icon = Icons.Default.Sword
)
```

### ProgressBarWithLabel
带标签的进度条：
```kotlin
ProgressBarWithLabel(
    label = "灵性恢复",
    current = 50f,
    max = 100f,
    color = Color(0xFFFFD700)
)
```

### CircularStatRing
圆形统计环：
```kotlin
CircularStatRing(
    "灵性",
    50f,
    100f,
    Color(0xFFFFD700),
    size = 80
)
```

### StatusEffectBadge
状态效果徽章：
```kotlin
StatusEffectBadge(
    name = "专注",
    type = GameStatus.StatusType.BUFF,
    duration = 3
)
```

### RelationshipNode
关系节点：
```kotlin
RelationshipNode(
    name = "邓恩·史密斯",
    type = CharacterRelation.RelationType.MENTOR,
    score = 75
)
```

### LogEntry
历史日志条目：
```kotlin
LogEntry(
    log = DailyLog(
        timestamp = System.currentTimeMillis(),
        type = DailyLog.LogType.INVESTIGATION,
        title = "调查案件",
        description = "调查廷根市的连续自杀案",
        impact = 10
    )
)
```

## 扩展性

### 添加新的状态类型
在`GameStatus.StatusType`中添加新的状态类型。

### 添加新的日志类型
在`DailyLog.LogType`中添加新的日志类型。

### 添加新的技能路径
在`SkillNode.SkillPath`中添加新的技能路径。

## 性能优化

- 使用`remember`缓存计算结果
- 使用`AnimatedVisibility`实现平滑动画
- 优化状态更新频率
- 避免不必要的重组

## 主题和样式

默认使用深色主题，金色作为强调色：
- 背景：`Color(0xFF16161A)`
- 强调色：`Color(0xFFFFD700)`
- 文字：`Color(0xFFFFFFFF)`
- 次要文字：`Color(0xFF9CA3AF)`

## 未来改进

- [ ] 添加更多数据可视化图表
- [ ] 实现角色培养建议
- [ ] 添加成就系统
- [ ] 实现历史回溯功能
- [ ] 添加多语言支持
