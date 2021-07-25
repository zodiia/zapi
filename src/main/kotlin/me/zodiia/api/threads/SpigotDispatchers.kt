package me.zodiia.api.threads

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher

object SpigotDispatchers {
    val Main = Dispatchers.Main
    val Async = Threads.executor.asCoroutineDispatcher()
}
