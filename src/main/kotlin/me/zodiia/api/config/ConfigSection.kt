package me.zodiia.api.config

import java.math.BigDecimal
import java.math.BigInteger

interface ConfigSection {
    fun has(key: String): Boolean
    fun getSection(key: String): ConfigSection
    operator fun get(key: String): ConfigElement?
    operator fun set(key: String, value: Int)
    operator fun set(key: String, value: Long)
    operator fun set(key: String, value: Short)
    operator fun set(key: String, value: Byte)
    operator fun set(key: String, value: Float)
    operator fun set(key: String, value: Double)
    operator fun set(key: String, value: Char)
    operator fun set(key: String, value: Boolean)
    operator fun set(key: String, value: String)
    operator fun set(key: String, value: BigInteger)
    operator fun set(key: String, value: BigDecimal)
    operator fun <T: Any> set(key: String, value: Collection<T?>)
    operator fun set(key: String, value: Configurable)
    fun merge(with: ConfigSection)
}
