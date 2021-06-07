package me.zodiia.api.scheduler

import java.time.LocalDateTime
import java.time.ZoneId

class Task(
    private val executionTime: LocalDateTime,
    private val executor: Runnable,
    val identifier: String,
) {
    fun tryExecute(): Boolean {
        if (LocalDateTime.now(ZoneId.systemDefault()).isAfter(executionTime)) {
            executor.run()
            return true
        }
        return false
    }
}
