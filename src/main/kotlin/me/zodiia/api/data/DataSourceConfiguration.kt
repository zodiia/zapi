package me.zodiia.api.data

import org.bukkit.configuration.ConfigurationSection

data class DataSourceConfiguration(
    val storageType: DataSourceType,
    internal val host: String,
    internal val database: String,
    internal val username: String,
    internal val password: String,
    val poolSize: Int,
    val keepAliveTime: Int,
    val connectionTimeout: Long,
    val maxLifetime: Long,
    val tablePrefix: String,
)
