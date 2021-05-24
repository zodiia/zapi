package me.zodiia.api.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

fun Plugin.updateYamlConfig(resourcePath: String, toUpdate: File, ignoredSections: MutableList<String> = mutableListOf()) {
    val inputStream = getResource(resourcePath) ?: throw IllegalArgumentException("Resource ${resourcePath} does not exists.")
    val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
    val lines = reader.readLines()
    val oldConfig = YamlConfiguration.loadConfiguration(toUpdate)
    val newConfig = YamlConfiguration.loadConfiguration(InputStreamReader(getResource(resourcePath)!!))
    val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(toUpdate), Charsets.UTF_8))
    val yaml = Yaml()

    reader.close()
    ignoredSections.removeIf { newConfig.isConfigurationSection(it) }

    val comments = lines.parseComments(ignoredSections, oldConfig, yaml)

    newConfig.write(oldConfig, comments, ignoredSections, writer, yaml)
}

private fun FileConfiguration.write(
    oldConfig: FileConfiguration,
    comments: HashMap<String?, String>,
    ignoredSections: List<String>,
    writer: BufferedWriter,
    yaml: Yaml,
) {
    getKeys(true).forEach {
        val keys = it.split("\\.")
        val key = keys[keys.size - 1]
        val comment = comments.remove(key)
        val indents = keys.size - 1
        val prefixSpaces = indents.getPrefixSpaces()
        val ignoredSection = ignoredSections.find { sec -> key.startsWith(sec) }

        if (comment != null) {
            writer.write(comment)
        }
        if (ignoredSection != null) {
            val newObj = this[key]
            val oldObj = oldConfig[key]

            newObj.writeNewObject(oldObj, key, prefixSpaces, writer, yaml)
        }
    }
}

private fun Any?.writeNewObject(
    oldObj: Any?,
    key: String,
    prefixSpaces: String,
    writer: BufferedWriter,
    yaml: Yaml,
) {
    if (this is ConfigurationSection && oldObj is ConfigurationSection) {
        oldObj.write(key, prefixSpaces, writer)
        return
    }
    if (this is ConfigurationSection) {
        this.write(key, prefixSpaces, writer)
        return
    }
    if (oldObj != null) {
        oldObj.write(key, prefixSpaces, writer, yaml)
        return
    }
    this?.write(key, prefixSpaces, writer, yaml)
}

private fun Any.write(key: String, prefixSpaces: String, writer: BufferedWriter, yaml: Yaml) {
    if (this is ConfigurationSerializable) {
        writer.write("${prefixSpaces}${key}: ${yaml.dump(this.serialize())}")
        return
    }
    if (this is String || this is Char) {
        var toWrite = this
        if (this is String) {
            toWrite = this.replace("\n", "\\n")
        }
        writer.write("${prefixSpaces}${key}: ${yaml.dump(toWrite)}")
        return
    }
    if (this is List<*>) {
        this.writeList(key, prefixSpaces, writer, yaml)
        return
    }
    writer.write("${prefixSpaces}${key}: ${yaml.dump(this)}")
}

private fun ConfigurationSection.write(key: String, prefixSpaces: String, writer: BufferedWriter) {
    writer.write("${prefixSpaces}${key}:")
    if (getKeys(false).isEmpty()) {
        writer.write(" {}")
    }
    writer.write("\n")
}

private fun List<*>.writeList(key: String, prefixSpaces: String, writer: BufferedWriter, yaml: Yaml) =
    writer.write(getAsString(key, prefixSpaces, yaml))

private fun List<String?>.parseComments(ignoredSections: List<String>, oldConfig: FileConfiguration, yaml: Yaml): HashMap<String?, String> {
    val comments = hashMapOf<String?, String>()
    val builder = StringBuilder()
    val keyBuilder = StringBuilder()
    var lastLineIndent = 0

    for (line in this) {
        if (line != null && line.trim().startsWith('-'))
            continue
        if (line == null || line.trim() == "" || line.trim().startsWith("#")) {
            builder.append("${line}\n")
            continue
        }
        lastLineIndent = keyBuilder.setFullKey(line, lastLineIndent)

        val section = ignoredSections.find { keyBuilder.toString() == it }

        if (section != null) {
            val value = oldConfig.get(keyBuilder.toString())

            if (value is ConfigurationSection) {
                builder.appendSection(value, StringBuilder(lastLineIndent.getPrefixSpaces()), yaml)
            }
            continue
        }
        if (keyBuilder.isNotEmpty()) {
            comments[keyBuilder.toString()] = builder.toString()
            builder.setLength(0)
        }
    }
    if (builder.isNotEmpty()) {
        comments[null] = builder.toString()
    }
    return comments
}

private fun List<*>.getAsString(key: String, prefixSpaces: String, yaml: Yaml): String {
    val builder = StringBuilder(prefixSpaces).append(key).append(":")

    if (isEmpty()) {
        builder.append(" []\n")
        return builder.toString()
    }
    builder.append("\n")
    for (i in 0 until size) {
        val value = this[i]

        if (value is String || value is Char) {
            builder.append("${prefixSpaces}- '${value}'")
        } else if (value is List<*>) {
            builder.append("${prefixSpaces}- ${yaml.dump(value)}")
        } else {
            builder.append("${prefixSpaces}- ${value.toString()}")
        }
        if (i != size) {
            builder.append("\n")
        }
    }
    return builder.toString()
}

private fun StringBuilder.appendSection(section: ConfigurationSection, prefixSpaces: StringBuilder, yaml: Yaml) {
    val keys = section.getKeys(false)

    append(prefixSpaces).append(section.currentPath?.getKeyFromFullKey()).append(":")
    if (keys.isEmpty()) {
        append(" {}\n")
        return
    }
    append("\n")
    prefixSpaces.append("  ")
    keys.forEach {
        val value = section.get(it)
        val key = it.getKeyFromFullKey()

        if (value is ConfigurationSection) {
            appendSection(value, prefixSpaces, yaml)
            prefixSpaces.setLength(prefixSpaces.length - 2)
        } else if (value is List<*>) {
            append(value.getAsString(key, prefixSpaces.toString(), yaml))
        } else {
            append("${prefixSpaces}${key}: ${yaml.dump(value)}")
        }
    }
}

private fun String.countIndents(): Int {
    var spaces = 0

    forEach { if (it == ' ') spaces++ else return spaces }
    return spaces
}

private fun String.getKeyFromFullKey(): String {
    val keys = split("\\.")

    return keys[keys.size - 1]
}

private fun StringBuilder.removeLastKey() {
    var current = toString()
    val keys = current.split("\\.")

    if (keys.size == 1) {
        setLength(0)
        return
    }

    current = current.substring(0, current.length - keys[keys.size - 1].length - 1)
    setLength(current.length)
}

private fun StringBuilder.setFullKey(line: String, lastLineIndent: Int): Int {
    val currentIndents = line.countIndents()
    val key = line.trim().split(':')[0]

    if (length == 0) {
        append(key)
    } else if (currentIndents > lastLineIndent) {
        append(".${key}")
    } else {
        val diff = lastLineIndent - currentIndents
        for (i in 0 until diff + 1) {
            removeLastKey()
        }
        if (length > 0) {
            append('.')
        }
        append(key)
    }
    return currentIndents
}

private fun StringBuilder.appendPrefixSpaces(amount: Int) {
    for (i in 0 until amount) {
        append("  ")
    }
}

private fun Int.getPrefixSpaces(): String {
    val builder = StringBuilder()

    for (i in 0 until this) {
        builder.append("  ")
    }
    return builder.toString()
}
