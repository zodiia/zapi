package me.zodiia.api.timings

class TimingsRange(
    val start: TimingsCapture,
    val end: TimingsCapture,
) {
    val items = hashSetOf<TimingsItem>()

    init {
        end.items.forEach {
            val startItem = start.items.find { s -> it.name == s.name } ?: return@forEach

            items.add(TimingsItem(
                it.name,
                it.count - startItem.count,
                it.startTime,
                it.totalTime - startItem.totalTime,
                it.currentTickTotal,
                it.violations - startItem.violations,
                it.depth - startItem.depth,
            ))
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()

        builder.appendLine("Timings ranged capture between ${start.captureTime} and ${end.captureTime}:")
        items.forEach {
            builder.appendLine("  - ${it.name} -> Start time: ${it.startTime}, Total time: ${it.totalTime}, Count: ${it.count}," +
                "Violations: ${it.violations}, Depth: ${it.depth}, Current tick total: ${it.currentTickTotal}")
        }
        return builder.toString()
    }
}
