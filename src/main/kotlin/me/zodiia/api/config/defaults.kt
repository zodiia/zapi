package me.zodiia.api.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.zodiia.api.config.json.JsonConfigFile
import me.zodiia.api.config.yaml.YamlConfigFile
import org.bukkit.configuration.file.YamlConfiguration

import java.io.File
import java.io.Reader
import java.io.Writer
import java.lang.IllegalArgumentException
import java.net.URL

val jsonParser: JsonParser = JsonParser()
val gson: Gson = GsonBuilder().setPrettyPrinting().create()

@Deprecated("Old API, removed in a very near future",
    ReplaceWith("File#readConfig(KSerializer<T>)")
)
fun File.readJson() = jsonParser.parse(readText()).asJsonObject!!
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("File#writeConfig(KSerializer<T>, T)")
)
fun File.writeJson(config: JsonConfigFile) = writeText(gson.toJson(config.json()))
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("Reader#readConfig(KSerializer<T>, String)")
)
fun Reader.readJson() = jsonParser.parse(readText()).asJsonObject!!
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("Writer#writeConfig(KSerializer<T>, T, String)")
)
fun Writer.writeJson(config: JsonConfigFile) = write(gson.toJson(config.json()))
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("URL#readConfig(KSerializer<T>, String)")
)
fun URL.readJson() = jsonParser.parse(readText()).asJsonObject!!

@Deprecated("Old API, removed in a very near future",
    ReplaceWith("File#readConfig(KSerializer<T>)")
)
fun File.readYaml() = YamlConfiguration.loadConfiguration(this)
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("File#writeConfig(KSerializer<T>, T)")
)
fun File.writeYaml(config: YamlConfigFile) = config.yamlFile().save(this)
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("Reader#readConfig(KSerializer<T>, String)")
)
fun Reader.readYaml() = YamlConfiguration.loadConfiguration(this)
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("Writer#writeConfig(KSerializer<T>, T, String)")
)
fun Writer.writeYaml(config: YamlConfigFile) = this.write(config.yamlFile().saveToString())
@Deprecated("Old API, removed in a very near future",
    ReplaceWith("URL#readConfig(KSerializer<T>, String)")
)
fun URL.readYaml() = YamlConfiguration().also { it.loadFromString(this.readText()) }

fun <T: Any> File.readConfig(serializer: KSerializer<T>): T = when (extension) {
//    "yml", "yaml" -> Yaml.default.decodeFromString(serializer, readText())
    "json" -> Json.decodeFromString(serializer, readText())
    else -> {
        throw IllegalArgumentException("Invalid file format: ${extension}.")
    }
}

fun <T: Any> File.writeConfig(serializer: KSerializer<T>, obj: T) = when (extension) {
//    "yml", "yaml" -> writeText(Yaml.default.encodeToString(serializer)
    "json" -> writeText(Json.encodeToString(serializer, obj))
    else -> {
        throw IllegalArgumentException("Invalid file format: ${extension}.")
    }
}

fun <T: Any> Reader.readConfig(serializer: KSerializer<T>, format: String): T = when (format) {
    //    "yml", "yaml" -> Yaml.default.decodeFromString(serializer, readText())
    "json" -> Json.decodeFromString(serializer, readText())
    else -> {
        throw IllegalArgumentException("Invalid file format: ${format}.")
    }
}

fun <T: Any> Writer.writeConfig(serializer: KSerializer<T>, obj: T, format: String) = when (format) {
    //    "yml", "yaml" -> writeText(Yaml.default.encodeToString(serializer)
    "json" -> write(Json.encodeToString(serializer, obj))
    else -> {
        throw IllegalArgumentException("Invalid file format: ${format}.")
    }
}

fun <T: Any> URL.readConfig(serializer: KSerializer<T>, format: String): T = when (format) {
    //    "yml", "yaml" -> Yaml.default.decodeFromString(serializer, readText())
    "json" -> Json.decodeFromString(serializer, readText())
    else -> {
        throw IllegalArgumentException("Invalid file format: ${format}.")
    }
}

