package me.zodiia.api.logger

import org.bukkit.Bukkit

object Console: LoggerWrapper(Bukkit.getLogger())
