package me.zodiia.api.i18n

import me.zodiia.api.util.translateColors
import org.bukkit.ChatColor

class I18nLanguage(val language: String, private val translations: Map<String, Array<String>>) {
    companion object {
        const val missingTranslations = "#MISSING_TRANSLATIONS#"
    }

    fun get(key: String, vararg args: Pair<String, String>): String {
        var line = translations[key]?.getOrNull(0) ?: return missingTranslations

        args.forEach { line = line.replace("\$${it.first}", it.second) }
        return line.translateColors()
    }

    fun get(key: String, args: Map<String, String>): String {
        var line = translations[key]?.getOrNull(0) ?: return missingTranslations

        args.forEach { line = line.replace("\$${it.key}", it.value) }
        return line.translateColors()
    }

    fun getMultiple(keys: Array<String>, vararg args: Pair<String, String>) = Array(keys.size) { idx ->
        var line = translations[keys[idx]]?.getOrNull(0) ?: missingTranslations

        args.forEach { line = line.replace("\$${it.first}", it.second) }
        line
    }

    fun getMultiple(keys: Array<String>, args: Map<String, String>) = Array(keys.size) { idx ->
        var line = translations[keys[idx]]?.getOrNull(0) ?: missingTranslations

        args.forEach { line = line.replace("\$${it.key}", it.value) }
        line
    }

    fun getArray(key: String, vararg args: Pair<String, String>): Array<String> {
        val lines = translations[key] ?: return arrayOf(missingTranslations)

        return Array(lines.size) { idx ->
            var line = lines[idx]

            args.forEach { line = line.replace("\$${it.first}", it.second) }
            line
        }
    }

    fun getArray(key: String, args: Map<String, String>): Array<String> {
        val lines = translations[key] ?: return arrayOf(missingTranslations)

        return Array(lines.size) { idx ->
            var line = lines[idx]

            args.forEach { line = line.replace("\$${it.key}", it.value) }
            line
        }
    }
}
