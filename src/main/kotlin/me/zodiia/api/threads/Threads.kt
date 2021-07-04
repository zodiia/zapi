package me.zodiia.api.threads

import me.zodiia.api.ApiPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

typealias TaskExecutor<T> = () -> T

object Threads {
    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) as ThreadPoolExecutor
    private var syncTask: BukkitTask? = null
    private val syncTaskQueue: Queue<TaskExecutor<Unit>> = LinkedList()
    private const val TERMINATION_TIMEOUT = 30000L

    init {
        Threads.startSyncTask(ApiPlugin.plugin)
    }

    init {
        startSyncTask(ApiPlugin.plugin)
    }

    fun runAsync(fct: TaskExecutor<Unit>): CompletablePromise<*> {
        return CompletablePromise(executor.submit(fct, Unit))
    }

    @JvmName("submitAsync")
    fun <T: Any> runAsync(fct: TaskExecutor<T>): CompletablePromise<T> {
        return CompletablePromise(executor.submit(fct))
    }

    fun runSync(fct: TaskExecutor<Unit>) {
        synchronized(syncTaskQueue) {
            syncTaskQueue.add(fct)
        }
    }

    fun startSyncTask(plugin: Plugin) {
        syncTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            synchronized(syncTaskQueue) {
                syncTaskQueue.forEach { it() }
                syncTaskQueue.clear()
            }
        }, 1L, 1L)
    }

    fun close() {
        syncTask?.cancel()
        executor.shutdown()
        executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS)
    }
}
