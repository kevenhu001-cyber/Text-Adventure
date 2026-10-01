package com.mysteriousjourney.domain.model

/**
 * 世界状态数据类
 * 包含游戏世界的所有状态信息
 */
data class WorldState(
    val currentTime: String = "1349年11月3日 周一 上午9点",
    val currentLocation: String = "霍伊大学历史系学生宿舍",
    val weather: String = "阴冷，薄雾",
    val visitedLocations: List<String> = listOf("霍伊大学历史系学生宿舍"),
    val openQuests: List<Quest> = emptyList(),
    val npcStates: Map<String, NpcState> = emptyMap()
)

/**
 * 任务
 * @param id 任务ID
 * @param name 任务名称
 * @param description 任务描述
 * @param status 任务状态
 */
data class Quest(
    val id: String,
    val name: String,
    val description: String,
    val status: String
)

/**
 * NPC状态
 * @param name NPC名称
 * @param location 当前位置
 * @param attitude 态度
 * @param memories 记忆列表
 */
data class NpcState(
    val name: String,
    val location: String,
    val attitude: String,
    val memories: List<String>
)
