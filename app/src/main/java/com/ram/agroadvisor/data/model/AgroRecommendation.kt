package com.ram.agroadvisor.data.model

enum class AgroStatus { GOOD, WARNING, BAD }

data class AgroRecommendation(
    val title: String,
    val emoji: String,
    val status: AgroStatus,
    val reason: String
)