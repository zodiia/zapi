package me.zodiia.api.config.json

import me.zodiia.api.config.ConfigFile
import me.zodiia.api.config.ConfigVersion
import me.zodiia.api.config.readJson
import me.zodiia.api.config.writeJson
import java.io.File
import java.io.Reader
import java.io.Writer
import java.net.URL

class JsonConfigFile: JsonConfigSection, ConfigFile {
    constructor(file: File) : super(file.readJson())
    constructor(reader: Reader): super(reader.readJson())
    constructor(url: URL): super(url.readJson())

    override fun root() = this

    override fun version(): ConfigVersion {
        if (has("_version")) {
            return ConfigVersion(get("_version")!!.toValue(String::class.java))
        }
        return ConfigVersion("0")
    }

    override fun save(file: File) = file.writeJson(this)

    override fun save(writer: Writer) = writer.writeJson(this)
}
