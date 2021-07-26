package me.zodiia.api.reflection

import kotlin.reflect.KFunction

fun <R: Any> KFunction<R>.bind(thisRef: Any?) = FunctionBinding(this, thisRef)
