package me.zodiia.api.config

interface ConfigElement {
    fun isNull(): Boolean
    fun <T: Any> toValue(cl: Class<T>): T
    fun <T: Any> toArray(arr: MutableCollection<T>, cl: Class<T>)
    fun <T: Any> toNullableArray(arr: MutableCollection<T?>, cl: Class<T>)
    fun <T: Any> toMap(map: MutableMap<String, T>, cl: Class<T>)
    fun <T: Any> toNullableMap(map: MutableMap<String, T?>, cl: Class<T>)
    fun toRawMap(map: MutableMap<String, ConfigElement>)
}
