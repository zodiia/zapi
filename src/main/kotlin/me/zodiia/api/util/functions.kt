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
    try { fct() }
    catch (th: Throwable) { return th }
    return null
}
