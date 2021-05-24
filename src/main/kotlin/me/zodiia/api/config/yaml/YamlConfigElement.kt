package me.zodiia.api.config.yaml

import me.zodiia.api.config.ConfigElement
import me.zodiia.api.config.Configurable
import org.bukkit.configuration.ConfigurationSection
import java.math.BigDecimal
import java.math.BigInteger

class YamlConfigElement(val sec: ConfigurationSection, val key: String): ConfigElement {
    override fun isNull() = sec.getString(key)?.equals("null") ?: false

    private fun <T: Any> stringTo(str: String, cl: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        try {
            return when (cl) {
                Int::class -> str.toInt()
                Long::class -> str.toLong()
                Short::class -> str.toShort()
                Byte::class -> str.toByte()
                Float::class -> str.toFloat()
                Double::class -> str.toDouble()
                Char::class -> str.toCharArray()[0]
                Boolean::class -> str.toBoolean()
                String::class -> str
                BigInteger::class -> BigInteger(str)
                BigDecimal::class -> BigDecimal(str)
                else -> throw IllegalArgumentException("Tried casting a configuration element into a unsupported type.")
            } as T
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not cast a configuration element.", ex)
        }
    }

    override fun <T: Any> toValue(cl: Class<T>): T {
        return try {
            if (cl.isAssignableFrom(Configurable::class.java)) {
                @Suppress("UNCHECKED_CAST")
                (cl.getConstructor().newInstance() as Configurable).apply { fromConfig(YamlConfigSection(sec)) } as T
            } else {
                stringTo(sec.getString(key)!!, cl)
            }
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not cast a configuration element.", ex)
        }
    }

    override fun <T: Any> toArray(arr: MutableCollection<T>, cl: Class<T>) {
        arr.clear()
        try {
            sec.getStringList(key).forEach {
                arr.add(stringTo(it, cl))
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration array.", ex)
        }
    }

    override fun <T : Any> toNullableArray(arr: MutableCollection<T?>, cl: Class<T>) {
        arr.clear()
        try {
            sec.getStringList(key).forEach {
                if (it.equals("null")) {
                    arr.add(null)
                } else {
                    arr.add(stringTo(it, cl))
                }
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration array.", ex)
        }
    }

    override fun <T: Any> toMap(map: MutableMap<String, T>, cl: Class<T>) {
        map.clear()
        try {
            val section = sec.getConfigurationSection(key)!!

            section.getKeys(true).forEach {
                if (cl.isAssignableFrom(Configurable::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    map[it] = (cl.getConstructor().newInstance() as Configurable).apply { fromConfig(YamlConfigSection(section.getConfigurationSection(it)!!)) } as T
                } else {
                    map[it] = stringTo(section.getString(it)!!, cl)
                }
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration map.", ex)
        }
    }

    override fun <T : Any> toNullableMap(map: MutableMap<String, T?>, cl: Class<T>) {
        map.clear()
        try {
            val section = sec.getConfigurationSection(key)!!

            section.getKeys(true).forEach {
                if (cl.isAssignableFrom(Configurable::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    map[it] = (cl.getConstructor().newInstance() as Configurable).apply { fromConfig(YamlConfigSection(section.getConfigurationSection(it)!!)) } as T
                } else if (section.getString(it)!! == "null") {
                    map[it] = null
                } else {
                    map[it] = stringTo(section.getString(it)!!, cl)
                }
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration map.", ex)
        }
    }

    override fun toRawMap(map: MutableMap<String, ConfigElement>) {
        map.clear()
        try {
            val section = sec.getConfigurationSection(key)!!

            section.getKeys(true).forEach {
                map[it] = YamlConfigElement(section, it)
            }
        } catch (ex: Throwable) {
            throw IllegalArgumentException("Could not parse a configuration map.", ex)
        }
    }
}
