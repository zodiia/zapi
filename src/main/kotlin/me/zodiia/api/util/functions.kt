package me.zodiia.api.util

fun <T: Any> T?.notNull(fct: (it: T) -> Unit) {
    if (this != null) {
        fct(this)
    }
}

@JvmName("objectNotNull")
inline fun <T: Any> notNull(it: T?, f: (it: T) -> Unit) {
    if (it != null) {
        f(it)
    }
}

fun tryFct(fct: () -> Unit): Throwable? {
    try { fct() } catch (any: Throwable) { return any }
    return null
}

fun <T : Any?> ternary(condition: Boolean, trueValue: T, falseValue: T) =
    if (condition) trueValue
    else falseValue

fun <T : Any?> ternary(condition: () -> Boolean, trueValue: T, falseValue: T) =
    if (condition.invoke()) trueValue
    else falseValue

fun <K, V : Any> Map<K, V>.toPairArray() = map { it.key to it.value }.toTypedArray()
