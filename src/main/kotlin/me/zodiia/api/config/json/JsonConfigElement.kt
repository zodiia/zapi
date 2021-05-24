package me.zodiia.api.config.json

import com.google.gson.JsonElement
import me.zodiia.api.config.ConfigElement
import me.zodiia.api.config.Configurable
import java.math.BigDecimal
import java.math.BigInteger

class JsonConfigElement(private val elem: JsonElement): ConfigElement {
    override fun isNull() = elem.isJsonNull

    override fun <T: Any> toValue(cl: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        try {
            return when (cl) {
                Int::class -> elem.asInt
                Long::class -> elem.asLong
                Short::class -> elem.asShort
                Byte::class -> elem.asByte
                Float::class -> elem.asFloat
                Double::class -> elem.asDouble
                Char::class -> elem.asCharacter
                Boolean::class -> elem.asBoolean
                String::class -> elem.asString.toString()
                BigInteger::class -> elem.asBigInteger
                BigDecimal::class -> elem.asBigDecimal
                else -> {
                    if (cl.isAssignableFrom(Configurable::class.java)) {
                        return (cl.getConstructor().newInstance() as Configurable).apply { fromConfig(JsonConfigSection(elem.asJsonObject)) } as T
                    }
                    throw IllegalArgumentException("Tried casting a configuration element into a unsupported type.")
                }
            } as T
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not cast a configuration element.", ex)
        }
    }

    override fun <T: Any> toArray(arr: MutableCollection<T>, cl: Class<T>) {
        arr.clear()
        try {
            elem.asJsonArray.forEach {
                arr.add(JsonConfigElement(it).toValue(cl))
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration array.", ex)
        }
    }

    override fun <T : Any> toNullableArray(arr: MutableCollection<T?>, cl: Class<T>) {
        arr.clear()
        try {
            elem.asJsonArray.forEach {
                val element = JsonConfigElement(it)
                if (element.isNull()) {
                    arr.add(null)
                } else {
                    arr.add(JsonConfigElement(it).toValue(cl))
                }
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration array.", ex)
        }
    }

    override fun <T: Any> toMap(map: MutableMap<String, T>, cl: Class<T>) {
        map.clear()
        try {
            elem.asJsonObject.entrySet().forEach {
                map[it.key] = JsonConfigElement(it.value).toValue(cl)
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration map.", ex)
        }
    }

    override fun <T : Any> toNullableMap(map: MutableMap<String, T?>, cl: Class<T>) {
        map.clear()
        try {
            elem.asJsonObject.entrySet().forEach {
                val element = JsonConfigElement(it.value)
                if (element.isNull()) {
                    map[it.key] = null
                } else {
                    map[it.key] = JsonConfigElement(it.value).toValue(cl)
                }
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration map.", ex)
        }
    }

    override fun toRawMap(map: MutableMap<String, ConfigElement>) {
        map.clear()
        try {
            elem.asJsonObject.entrySet().forEach {
                map[it.key] = JsonConfigElement(it.value)
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration map.", ex)
        }
    }
}
