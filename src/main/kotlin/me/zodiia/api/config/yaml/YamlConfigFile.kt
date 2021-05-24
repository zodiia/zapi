package me.zodiia.api.config.yaml

import me.zodiia.api.config.ConfigFile
import me.zodiia.api.config.ConfigVersion
import me.zodiia.api.config.readYaml
import me.zodiia.api.config.writeYaml
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.Reader
import java.io.Writer
import java.net.URL

class YamlConfigFile(private val config: YamlConfiguration): YamlConfigSection(config), ConfigFile {
    constructor(file: File): this(file.readYaml())
    constructor(reader: Reader): this(reader.readYaml())
    constructor(url: URL): this(url.readYaml())

    override fun root() = this

    override fun version(): ConfigVersion {
        if (has("_version")) {
            return ConfigVersion(get("_version")!!.toValue(String::class.java))
        }
        return ConfigVersion("0")
    }

    override fun save(file: File) = file.writeYaml(this)

    override fun save(writer: Writer) = writer.writeYaml(this)

    fun yamlFile(): YamlConfiguration = config
}
