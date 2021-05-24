package me.zodiia.api.config

import me.zodiia.api.config.json.JsonConfigFile
import me.zodiia.api.config.yaml.YamlConfigFile
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.Writer

interface ConfigFile: ConfigSection {
    fun root(): ConfigSection
    fun version(): ConfigVersion
    fun save(file: File)
    fun save(writer: Writer)

    companion object {
        fun getConfig(file: File): ConfigFile? {
            if (file.endsWith(".json")) {
                return JsonConfigFile(file)
            } else if (file.endsWith(".yml") || file.endsWith(".yaml")) {
                return YamlConfigFile(file)
            }
            return null
        }

        fun getConfig(plugin: Plugin, name: String = "config.yml"): ConfigFile? {
            val file = File(plugin.dataFolder, name)

            if (!file.exists() || !file.isFile) {
                return null
            }
            if (name.endsWith(".json")) {
                return JsonConfigFile(file)
            } else if (name.endsWith(".yml") || name.endsWith(".yaml")) {
                return YamlConfigFile(file)
            }
            return null
        }

        fun getInternalConfig(plugin: Plugin, name: String = "config.yml"): ConfigFile? {
            val url = plugin.javaClass.classLoader.getResource(name) ?: return null

            if (name.endsWith(".json")) {
                return JsonConfigFile(url)
            } else if (name.endsWith(".yml") || name.endsWith(".yaml")) {
                return YamlConfigFile(url)
            }
            return null
        }
    }
}
