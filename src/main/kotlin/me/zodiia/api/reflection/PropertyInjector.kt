package me.zodiia.api.reflection

import kotlin.reflect.KProperty

class PropertyInjector<T: Any>(
    private val from: Any,
    name: String,
) {
    private val reflector = PropertyReflector<Any, T>(from::class, name)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        reflector[from]

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        reflector[from] = value
    }
}
