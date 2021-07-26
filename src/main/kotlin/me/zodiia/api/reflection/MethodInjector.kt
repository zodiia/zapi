package me.zodiia.api.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

class MethodInjector<R: Any>(
    private val from: Any,
    type: KClass<out Any>,
    name: String,
    vararg parameters: KType,
) {
    private val reflector = MethodReflector<Any, R>(type, name, *parameters)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): FunctionBinding<R> =
        reflector[from]
}
