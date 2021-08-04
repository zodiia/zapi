package me.zodiia.api.threads

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

object SpigotDispatchers {
    val Main = Dispatchers.Main
    val Async = Threads.executor.asCoroutineDispatcher()
    val Reactor = Schedulers.fromExecutor(Threads.executor)
}
