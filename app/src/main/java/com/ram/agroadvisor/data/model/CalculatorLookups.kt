package com.ram.agroadvisor.data.model

/** Single row from `GET /api/CropRequirements` — one (crop, stage) combination. */
data class CropRequirement(
    val id: Int,
    val cropType: String,
    val growthStage: String,
    val n: Double,
    val p: Double,
    val k: Double
)

/** Single row from `GET /api/SoilMultipliers`. */
data class SoilMultiplier(
    val id: Int,
    val soilType: String,
    val nMultiplier: Double,
    val pMultiplier: Double,
    val kMultiplier: Double
)
