package com.ram.agroadvisor.data.model

data class AnalyzeResponse(
    val plantName: String,
    val diseaseName: String,
    val information: String,
    val confidence: Int,
    val imageUrls: List<String> = emptyList()
)
