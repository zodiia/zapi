package me.zodiia.api.timings

import java.time.Instant

class TimingsCapture {
    val items = hashSetOf<TimingsItem>()
    val captureTime = Instant.now()

    fun captureItem(item: TimingsItem) {
        items.add(item)
    }

    override fun toString(): String {
        val builder = StringBuilder()

        builder.appendLine("Timings capture at ${captureTime}:")
        items.forEach {
            builder.appendLine("  - ${it.name} -> Start time: ${it.startTime}, Total time: ${it.totalTime}, Count: ${it.count}," +
                "Violations: ${it.violations}, Depth: ${it.depth}, Current tick total: ${it.currentTickTotal}")
        }
        return builder.toString()
    }
}
