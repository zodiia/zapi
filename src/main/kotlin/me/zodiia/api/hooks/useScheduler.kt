package me.zodiia.api.hooks

import me.zodiia.api.scheduler.Scheduler
import kotlin.reflect.KProperty

fun useScheduler() = SchedulerHook()

class SchedulerHook internal constructor() {
    private val scheduler = Scheduler()

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = scheduler
}
