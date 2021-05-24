package me.zodiia.api.threads

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object CompletablePromiseContext {
    private val service: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    fun schedule(fct: () -> Unit) = service.schedule(fct, 1, TimeUnit.MILLISECONDS)
}