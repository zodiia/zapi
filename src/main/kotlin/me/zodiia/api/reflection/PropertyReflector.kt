package me.zodiia.api.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class PropertyReflector<T: Any, R: Any>(
    type: KClass<out Any>,
    name: String,
) {
    private val field: KProperty1<T, R> = (type.declaredMemberProperties.find { it.name == name }
        ?: throw NoSuchFieldException("Property does not exist")) as KProperty1<T, R>

    init {
        field.isAccessible = true
    }

    operator fun get(from: T): R = field.get(from)
    operator fun set(to: T, value: R) =
        if (field is KMutableProperty1<T, R>) {
            field.set(to, value)
        } else {
            throw NoSuchMethodException("Property does not have a setter")
        }
}
