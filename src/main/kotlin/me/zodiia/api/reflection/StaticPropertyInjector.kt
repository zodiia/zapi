package me.zodiia.api.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class StaticPropertyInjector<T: Any, R: Any>(
    private val from: KClass<T>,
    private val type: KClass<R>,
    name: String,
) {
    private val reflector = StaticPropertyReflector<T, R>(from, name)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): R =
        reflector.get()

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
        reflector.set(value)
    }
}
