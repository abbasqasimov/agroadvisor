package com.ram.agroadvisor.data

data class CalculatorRequest(
    val cropType: String,
    val growthStage: String,
    val soilType: String,
    val fieldSizeHectares: Double
)

data class CalculatorResponse(
    val cropType: String,
    val growthStage: String,
    val soilType: String,
    val fieldSizeHectares: Double,
    val baseNPerHectare: Double,
    val basePPerHectare: Double,
    val baseKPerHectare: Double,
    val appliedNMultiplier: Double,
    val appliedPMultiplier: Double,
    val appliedKMultiplier: Double,
    val totalNRequired: Double,
    val totalPRequired: Double,
    val totalKRequired: Double,
    val aiSummary: String,
    val aiSuggestions: List<String>
)