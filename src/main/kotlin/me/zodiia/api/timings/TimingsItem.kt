package me.zodiia.api.timings

data class TimingsItem(
    val name: String,
    val count: Long,
    val startTime: Long,
    val totalTime: Long,
    val currentTickTotal: Long,
    val violations: Long,
    val depth: Long,
)
