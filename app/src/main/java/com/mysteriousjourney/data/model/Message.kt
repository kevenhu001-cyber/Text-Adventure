package com.mysteriousjourney.data.model

/**
 * 消息数据类
 * 用于API通信的消息格式
 */
data class Message(
    val role: String,
    val content: String
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"
    }
}