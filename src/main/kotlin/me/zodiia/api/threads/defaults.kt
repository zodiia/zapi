package me.zodiia.api.threads

typealias TaskExecutor<T> = () -> T
typealias SuspendFunction = suspend () -> Unit
