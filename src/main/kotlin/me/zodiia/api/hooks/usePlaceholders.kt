package me.zodiia.api.hooks

import me.zodiia.api.placeholder.LocalPlaceholders
import kotlin.reflect.KProperty

fun usePlaceholders() = PlaceholdersHook()

class PlaceholdersHook internal constructor() {
    private val localPlaceholders = LocalPlaceholders()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): LocalPlaceholders = localPlaceholders
}
