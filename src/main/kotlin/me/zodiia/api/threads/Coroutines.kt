package me.zodiia.api.threads

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object Coroutines {
    fun spawn(dispatcher: CoroutineDispatcher, fct: suspend CoroutineScope.() -> Unit) = runBlocking {
        launch(dispatcher) {
            fct.invoke(this)
        }
    }

    fun <T : Any> run(fct: suspend CoroutineScope.() -> T) = runBlocking {
        fct.invoke(this)
    }
}
