package me.zodiia.api.threads

import me.zodiia.api.ApiPlugin
import me.zodiia.api.hooks.usePlugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.LinkedList
import java.util.Queue

class SyncBukkitExecutor {
    private var syncTask: BukkitTask? = null
    private val syncTaskQueue: Queue<TaskExecutor<Unit>> = LinkedList()

    private val plugin by usePlugin(ApiPlugin::class.java)

    init {
        syncTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            synchronized(syncTaskQueue) {
                syncTaskQueue.forEach { it() }
                syncTaskQueue.clear()
            }
        }, 1L, 1L)
    }

    fun run(fct: TaskExecutor<Unit>) {
        synchronized(syncTaskQueue) {
            syncTaskQueue.add(fct)
        }
    }

    fun close() {
        syncTask?.cancel()
    }
}
