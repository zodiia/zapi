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

object Console {
    private val timers = mutableMapOf<String, Long>()

    fun print(vararg obj: Any) {
        obj.forEach {
            Bukkit.getConsoleSender().sendMessage(it.toString())
        }
    }

    fun log(vararg obj: Any) {
        obj.forEach {
            Bukkit.getLogger().info(it.toString())
        }
    }

    fun warn(vararg obj: Any) {
        obj.forEach {
            Bukkit.getLogger().warning(it.toString())
        }
    }

    fun error(vararg obj: Any) {
        obj.forEach {
            Bukkit.getLogger().severe(it.toString())
        }
    }

    fun error(th: Throwable): String {
        val id = ThreadLocalRandom.current().nextString(7, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

        Bukkit.getLogger().severe("Error with id #${id}:")
        th.printStackTrace()
        return id
    }

    fun time(id: String) {
        synchronized(timers) {
            timers.remove(id)
            timers.put(id, System.nanoTime())
        }
    }

    fun timeEnd(id: String) {
        val endTime = System.nanoTime()
        val startTime: Long?

        synchronized(timers) {
            startTime = timers[id]
        }
        if (startTime == null) {
            return
        }
        val millis = (endTime - startTime) / 1000.0
        Bukkit.getLogger().info("${id}: ${millis}ms.")
    }
}
