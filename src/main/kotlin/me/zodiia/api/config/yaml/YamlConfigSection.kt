package me.zodiia.api.config.yaml

import me.zodiia.api.config.ConfigElement
import me.zodiia.api.config.ConfigSection
import me.zodiia.api.config.Configurable
import org.bukkit.configuration.ConfigurationSection
import java.math.BigDecimal
import java.math.BigInteger

open class YamlConfigSection(private val section: ConfigurationSection): ConfigSection {
    override fun has(key: String) = section.contains(key)

    override fun getSection(key: String) = YamlConfigSection(section.getConfigurationSection(key)!!)

    override fun get(key: String): ConfigElement? {
        if (has(key)) {
            return YamlConfigElement(section, key)
        }
        return null
    }

    override fun set(key: String, value: Int) = section.set(key, value)

    override fun set(key: String, value: Long) = section.set(key, value)

    override fun set(key: String, value: Short) = section.set(key, value)

    override fun set(key: String, value: Byte) = section.set(key, value)

    override fun set(key: String, value: Float) = section.set(key, value)

    override fun set(key: String, value: Double) = section.set(key, value)

    override fun set(key: String, value: Char) = section.set(key, value)

    override fun set(key: String, value: Boolean) = section.set(key, value)

    override fun set(key: String, value: String) = section.set(key, value)

    override fun set(key: String, value: BigInteger) = section.set(key, value)

    override fun set(key: String, value: BigDecimal) = section.set(key, value)

    override fun <T: Any> set(key: String, value: Collection<T?>) {
        val finalArray = mutableListOf<String>()

        value.forEach {
            if (it == null) {
                finalArray.add("null")
            } else {
                finalArray.add(it.toString())
            }
        }
        section.set(key, finalArray)
    }

    override fun set(key: String, value: Configurable) {
        value.toConfig(YamlConfigSection(section.createSection(key)))
    }

    override fun merge(with: ConfigSection) {
        if (with !is YamlConfigSection) {
            throw IllegalArgumentException()
        }
        with.yaml().getKeys(false).forEach {
            if (!section.contains(it)) {
                section.set(it, with.yaml().get(it))
            } else if (with.yaml().isConfigurationSection(it)) {
                getSection(it).merge(with.getSection(it))
            }
        }
    }

    private fun yaml() = section
}
