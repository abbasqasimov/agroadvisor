package com.ram.agroadvisor.data.model

data class ChatRequestDto(
    val sessionId: String?,
    val message: String
)

data class ChatMessageDto(
    val role: String,
    val content: String,
    val createdAtUtc: String
)

data class ChatResponseDto(
    val sessionId: String,
    val answer: String,
    val history: List<ChatMessageDto>
)
