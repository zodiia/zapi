package me.zodiia.api.i18n

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.ChatColor
import java.io.File

class I18nLanguage(val language: String, values: Map<String, String>) {
    private val keys = mutableMapOf<String, String>()
    private val multilineKeys = mutableMapOf<String, List<String>>()

    init {
        values.map { keys[it.key] = it.value }
    }

    operator fun get(key: String): String = get(key, emptyMap())

    fun get(key: String, args: Map<String, String>): String {
        var line = keys[key] ?: return ""

        args.forEach { arg ->
            line = line.replace("\$${arg.key}", arg.value)
        }
        return ChatColor.translateAlternateColorCodes('&', line)
    }

    fun getMultiple(keys: List<String>, args: Map<String, String>): List<String> {
        return keys.map { key ->
            var line = this.keys[key] ?: return@map ""

            args.forEach { arg ->
                line = line.replace("\$${arg.key}", arg.value)
            }
            return@map line
        }
    }

    fun getArray(key: String, args: Map<String, String>): List<String> {
        val lines = multilineKeys[key] ?: return listOf()

        return lines.map { line ->
            var newLine = line

            args.forEach { arg ->
                newLine = line.replace("\$${arg.key}", arg.value)
            }
            return@map ChatColor.translateAlternateColorCodes('&', newLine)
        }
    }
}
