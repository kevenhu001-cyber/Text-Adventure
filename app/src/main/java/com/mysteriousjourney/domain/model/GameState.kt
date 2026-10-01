package com.mysteriousjourney.domain.model

/**
 * 完整游戏状态
 * 包含玩家状态、世界状态和聊天历史
 */
data class GameState(
    val player: PlayerState = PlayerState(),
    val world: WorldState = WorldState(),
    val chatHistory: List<ChatMessage> = emptyList(),
    val gameMemory: List<String> = emptyList(), // 关键剧情记忆/知识库
    val isInitialized: Boolean = false
)

/**
 * 聊天消息
 * @param role 角色（user/assistant/system）
 * @param content 消息内容
 * @param timestamp 时间戳
 */
data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
