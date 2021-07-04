package me.zodiia.api.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible

class MethodReflector<T: Any, R: Any>(
    type: KClass<out Any>,
    name: String,
    vararg parameters: KType,
) {
    private val method: KFunction<R> = findMethod(type, name, parameters)

    init {
        method.isAccessible = true
    }

    private fun findMethod(type: KClass<out Any>, name: String, parameters: Array<out KType>): KFunction<R> {
        val declaredFunctions = type.functions.filter { it.name == name }
        val count = declaredFunctions.count()

        if (count == 1) {
            return declaredFunctions[0] as KFunction<R>
        } else if (count == 0) {
            throw NoSuchMethodException("No such method with this name")
        }
        if (parameters.isEmpty()) {
            throw NoSuchMethodException("Overload resolution ambiguity, multiple methods are matching")
        }
        return declaredFunctions.find { it.parameters == parameters.toList() } as? KFunction<R> ?: throw NoSuchMethodException("Could not find method")
    }

    operator fun get(from: T): BoundFunction<R> = method.bind(from)
}
