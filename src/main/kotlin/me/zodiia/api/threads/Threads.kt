package me.zodiia.api.threads

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

typealias TaskExecutor<T> = () -> T

object Threads {
    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) as ThreadPoolExecutor
    private var syncTask: BukkitTask? = null
    private val syncTaskQueue: Queue<TaskExecutor<Unit>> = LinkedList()

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
        executor.awaitTermination(30000, TimeUnit.MILLISECONDS)
    }
}
