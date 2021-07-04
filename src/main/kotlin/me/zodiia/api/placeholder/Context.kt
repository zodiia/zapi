package me.zodiia.api.placeholder

import org.bukkit.entity.Player
import java.time.Instant

class Context(
    val player: Player?,
    val instant: Instant,
)
