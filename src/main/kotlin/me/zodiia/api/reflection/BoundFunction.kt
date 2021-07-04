package me.zodiia.api.reflection

import kotlin.reflect.KFunction

class BoundFunction<R: Any>(
    private val function: KFunction<R>,
    private val thisRef: Any?,
) {
    operator fun invoke(vararg args: Any?) = function.call(thisRef, *args)
}
