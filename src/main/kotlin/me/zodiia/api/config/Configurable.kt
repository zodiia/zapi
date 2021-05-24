package me.zodiia.api.config

interface Configurable {
    fun fromConfig(cfg: ConfigSection)
    fun toConfig(cfg: ConfigSection)
}
