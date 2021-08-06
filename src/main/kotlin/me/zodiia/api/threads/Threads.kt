package me.zodiia.api.threads

import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object Threads {
    private const val TERMINATION_TIMEOUT = 30000L

    internal val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) as ThreadPoolExecutor
    private val syncExecutor = SyncBukkitExecutor()

    fun runAsync(fct: TaskExecutor<Unit>): CompletablePromise<*> {
        return CompletablePromise(executor.submit(fct, Unit))
    }

    @JvmName("submitAsync")
    fun <T: Any> runAsync(fct: TaskExecutor<T>): CompletablePromise<T> {
        return CompletablePromise(executor.submit(fct))
    }

    fun runSync(fct: TaskExecutor<Unit>) {
        syncExecutor.run(fct)
    }

    fun close() {
        syncExecutor.close()
        executor.shutdown()
        executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS)
    }
}
