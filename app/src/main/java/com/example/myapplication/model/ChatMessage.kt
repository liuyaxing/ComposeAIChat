package com.example.myapplication.model

/**
 * 聊天消息数据类
 * @param text 消息文本
 * @param isUser 是否是用户发送（false = AI 回复）
 * @param isTyping 是否正在打字效果中（AI 回复进行中）
 */
data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false
)
