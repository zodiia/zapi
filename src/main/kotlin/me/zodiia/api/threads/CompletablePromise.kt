package me.zodiia.api.threads

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

class CompletablePromise<V: Any> constructor(
    private val future: Future<V>
): CompletableFuture<V>() {
    private fun tryToComplete() {
        if (future.isDone) {
            try {
                complete(future.get())
            } catch (ex: InterruptedException) {
                completeExceptionally(ex)
            } catch (ex: ExecutionException) {
                completeExceptionally(ex.cause)
            }
            return
        }
        if (future.isCancelled) {
            cancel(true)
            return
        }
        CompletablePromiseContext.schedule(this::tryToComplete)
    }
}