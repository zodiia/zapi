package me.zodiia.api.logger

import me.zodiia.api.timings.Timings
import me.zodiia.api.timings.TimingsCapture
import me.zodiia.api.timings.TimingsRange
import me.zodiia.api.util.nextString
import org.bukkit.Bukkit
import java.util.concurrent.ThreadLocalRandom
import java.util.logging.Logger

open class LoggerWrapper(
    private val logger: Logger,
) {
    private val timers = mutableMapOf<String, Long>()
    private val counters = mutableMapOf<String, Long>()
    private val timings = mutableMapOf<String, TimingsCapture>()

    fun print(vararg obj: Any) {
        obj.forEach {
            Bukkit.getConsoleSender().sendMessage(it.toString())
        }
    }

    fun log(vararg obj: Any) {
        obj.forEach {
            logger.info(it.toString())
        }
    }

    fun warn(vararg obj: Any) {
        obj.forEach {
            logger.warning(it.toString())
        }
    }

    fun error(vararg obj: Any) {
        obj.forEach {
            logger.severe(it.toString())
        }
    }

    fun error(th: Throwable): String {
        val id = ThreadLocalRandom.current().nextString(DEFAULT_ERROR_ID_SIZE, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")

        logger.severe("Error with id #$id:")
        th.printStackTrace()
        return id
    }

    fun time(id: String) {
        synchronized(timers) {
            timers.remove(id)
            timers.put(id, System.nanoTime())
        }
    }

    fun timeLog(id: String) {
        val endTime = System.nanoTime()
        val startTime: Long?

        synchronized(timers) {
            startTime = timers[id]
        }
        if (startTime == null) {
            return
        }
        val millis = (endTime - startTime) / MILLIS_PER_SECOND
        log("$id: ${millis}ms.")
    }

    fun timeEnd(id: String) {
        timeLog(id)
        synchronized(timers) {
            timers.remove(id)
        }
    }

    fun count(id: String) {
        val count: Long
        synchronized(counters) {
            count = counters[id] ?: 0
        }

        logger.info("$id: Called ${count + 1} times.")
        counters[id] = count + 1
    }

    fun countReset(id: String) {
        synchronized(counters) {
            counters.remove(id)
        }
    }

    fun trace() {
        val trace = Thread.currentThread().stackTrace

        log("Current stack trace:")
        trace.forEach {
            log("\t$it")
        }
    }

    fun timingsCapture() {
        val capture = Timings.capture()

        log(*capture.toString().lines().toTypedArray())
    }

    fun timingsStart(id: String) {
        val capture = Timings.capture()

        synchronized(timings) {
            timings[id] = capture
        }
    }

    fun timingsEnd(id: String) {
        val capture = Timings.capture()
        val start: TimingsCapture

        synchronized(timings) {
            start = timings[id] ?: throw IllegalStateException()
        }
        val range = TimingsRange(start, capture)

        log(*range.toString().lines().toTypedArray())
    }

    companion object {
        const val MILLIS_PER_SECOND = 1000.0
        const val DEFAULT_ERROR_ID_SIZE = 7
    }
}
