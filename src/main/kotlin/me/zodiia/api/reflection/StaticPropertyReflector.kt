package me.zodiia.api.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.isAccessible

class StaticPropertyReflector<T: Any, R: Any>(
    type: KClass<T>,
    name: String,
) {
    private val field: KProperty0<R> = (type.staticProperties.find { it.name == name }
        ?: throw NoSuchFieldException("Property does not exist")) as KProperty0<R>

    init {
        field.isAccessible = true
    }

    fun get(): R = field.get()

    fun set(value: R) =
        if (field is KMutableProperty0<R>) {
            field.set(value)
        } else {
            throw NoSuchMethodException("Property does not have a setter")
        }
}
