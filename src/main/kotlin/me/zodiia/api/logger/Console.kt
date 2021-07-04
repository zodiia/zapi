package me.zodiia.api.logger

import me.zodiia.api.util.nextString
import me.zodiia.api.util.send
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.RuntimeException
import java.util.concurrent.ThreadLocalRandom
import java.util.logging.Logger

object Console: LoggerWrapper(Bukkit.getLogger())
