package me.zodiia.api.timings

import me.zodiia.api.reflection.PropertyReflector
import me.zodiia.api.reflection.StaticPropertyInjector
import org.bukkit.Bukkit
import org.spigotmc.CustomTimingsHandler
import java.util.Queue

class SpigotTimingsWrapper: TimingsWrapper {
    private val handlers by StaticPropertyInjector(CustomTimingsHandler::class, Queue::class, "HANDLERS")
    private val nameProp = PropertyReflector<CustomTimingsHandler, String>(CustomTimingsHandler::class, "name")
    private val countProp = PropertyReflector<CustomTimingsHandler, Long>(CustomTimingsHandler::class, "count")
    private val startProp = PropertyReflector<CustomTimingsHandler, Long>(CustomTimingsHandler::class, "start")
    private val timingDepthProp = PropertyReflector<CustomTimingsHandler, Long>(CustomTimingsHandler::class, "timingDepth")
    private val totalTimeProp = PropertyReflector<CustomTimingsHandler, Long>(CustomTimingsHandler::class, "totalTime")
    private val curTickTotalProp = PropertyReflector<CustomTimingsHandler, Long>(CustomTimingsHandler::class, "curTickTotal")
    private val violationsProp = PropertyReflector<CustomTimingsHandler, Long>(CustomTimingsHandler::class, "violations")

    override fun capture(): TimingsCapture {
        val capture = TimingsCapture()

        handlers.forEach {
            it as CustomTimingsHandler
            capture.captureItem(TimingsItem(
                nameProp[it],
                countProp[it],
                startProp[it],
                totalTimeProp[it],
                curTickTotalProp[it],
                violationsProp[it],
                timingDepthProp[it]
            ))
        }
        return capture
    }

    override fun start() {
        handlers.forEach {
            it as CustomTimingsHandler
            it.startTiming()
        }
    }

    override fun stop() {
        handlers.forEach {
            it as CustomTimingsHandler
            it.stopTiming()
        }
    }

    override fun isEnabled() = Bukkit.getPluginManager().useTimings()
}
