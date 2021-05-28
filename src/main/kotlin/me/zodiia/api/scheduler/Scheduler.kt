package me.zodiia.api.scheduler

import me.zodiia.api.ApiPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalDateTime
import java.time.ZoneId

object Scheduler {
    private val scheduledTasks = hashSetOf<Task>()

    init {
        Bukkit.getScheduler().runTaskTimer(JavaPlugin.getPlugin(ApiPlugin::class.java), Runnable {
            val ranTasks = hashSetOf<Task>()

            synchronized(scheduledTasks) {
                for (task in scheduledTasks) {
                    if (task.tryExecute()) {
                        ranTasks.add(task)
                    }
                }
                for (task in ranTasks) {
                    scheduledTasks.remove(task)
                }
            }
        }, 20L, 20L)
    }

    fun schedule(time: LocalDateTime, executor: Runnable, identifier: String) {
        synchronized(scheduledTasks) {
            if (time.isAfter(LocalDateTime.now(ZoneId.systemDefault()))) {
                scheduledTasks.add(Task(time, executor, identifier))
            }
        }
    }

    fun cancelTasks(vararg identifiers: String) {
        val cancelledTasks = hashSetOf<Task>()

        synchronized(scheduledTasks) {
            identifiers.forEach {
                scheduledTasks.forEach { task ->
                    if (task.identifier.startsWith(it)) {
                        cancelledTasks.add(task)
                    }
                }
                cancelledTasks.forEach { task ->
                    scheduledTasks.remove(task)
                }
            }
        }
    }
}