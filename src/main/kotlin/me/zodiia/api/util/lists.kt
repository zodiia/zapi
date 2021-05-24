package me.zodiia.api.util

fun <T: Any> T?.validateWhitelist(values: Collection<T?>): Boolean {
    if (values.isEmpty()) {
        return false
    }
    values.forEach {
        if (this == it) {
            return true
        }
    }
    return false
}

fun <T: Any> T?.validateBlacklist(values: Collection<T?>): Boolean {
    return !(this.validateWhitelist(values))
}

inline fun <reified T: Enum<T>> String.toEnumList(): List<T> {
    val res = mutableListOf<T>()

    this.split(',').forEach { elem ->
        java.lang.Enum.valueOf(T::class.java, elem).notNull { res.add(it) }
    }
    return res
}
