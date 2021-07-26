package me.zodiia.api.reflection

import kotlin.reflect.KFunction

class FunctionBinding<R: Any>(
    private val function: KFunction<R>,
    private val thisRef: Any?,
    private val defArgs: Array<out Any?> = emptyArray()
) {
    operator fun invoke(vararg args: Any?) = function.call(thisRef, *defArgs, *args)
}
