package me.zodiia.api.config.json

import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import me.zodiia.api.config.ConfigElement
import me.zodiia.api.config.ConfigSection
import me.zodiia.api.config.Configurable
import java.math.BigDecimal
import java.math.BigInteger

open class JsonConfigSection(private val elem: JsonObject): ConfigSection {
    override fun has(key: String) = elem.has(key)

    override fun getSection(key: String) = JsonConfigSection(elem[key].asJsonObject)

    override fun get(key: String): ConfigElement? {
        if (has(key)) {
            return JsonConfigElement(elem[key])
        }
        return null
    }

    override fun set(key: String, value: Int) = elem.addProperty(key, value)

    override fun set(key: String, value: Long) = elem.addProperty(key, value)

    override fun set(key: String, value: Short) = elem.addProperty(key, value)

    override fun set(key: String, value: Byte) = elem.addProperty(key, value)

    override fun set(key: String, value: Float) = elem.addProperty(key, value)

    override fun set(key: String, value: Double) = elem.addProperty(key, value)

    override fun set(key: String, value: Char) = elem.addProperty(key, value)

    override fun set(key: String, value: Boolean) = elem.addProperty(key, value)

    override fun set(key: String, value: String) = elem.addProperty(key, value)

    override fun set(key: String, value: BigInteger) = elem.addProperty(key, value)

    override fun set(key: String, value: BigDecimal) = elem.addProperty(key, value)

    override fun <T: Any> set(key: String, value: Collection<T?>) {
        val array = JsonArray()

        value.forEach {
            if (it == null) {
                array.add(JsonNull.INSTANCE)
                return@forEach
            }
            when (it) {
                is Number -> array.add(it)
                is String -> array.add(it)
                is Char -> array.add(it)
                is Boolean -> array.add(it)
                is Configurable -> {
                    val section = JsonConfigSection(JsonObject())

                    it.toConfig(section)
                    array.add(section.json())
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun set(key: String, value: Configurable) {
        if (elem.has(key)) {
            elem.remove(key)
        }
        elem.add(key, JsonObject())
        value.toConfig(JsonConfigSection(elem))
    }

    override fun merge(with: ConfigSection) {
        if (with !is JsonConfigSection) {
            throw IllegalArgumentException()
        }
        with.json().entrySet().forEach {
            if (!elem.has(it.key)) {
                elem.add(it.key, it.value)
            } else if (it.value.isJsonObject) {
                getSection(it.key).merge(with.getSection(it.key))
            }
        }
    }

    fun json() = elem
}
