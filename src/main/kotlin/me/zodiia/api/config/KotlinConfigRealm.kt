package me.zodiia.api.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigSource
import com.sksamuel.hoplite.json.JsonPropertySource
import me.zodiia.api.config.decoders.FlatStringMapDecoder
import me.zodiia.api.plugins.KotlinPlugin
import java.io.File
import kotlin.reflect.KClass

open class KotlinConfigRealm(
    private val plugin: KotlinPlugin
) {
    private data class InnerLanguage(val values: Map<String, String>)

    private val configLoader = ConfigLoader()

    fun <T : Any> load(path: String, type: KClass<T>): T {
        val sources = ConfigSource.fromFiles(listOf(File(plugin.dataFolder, path)))

        if (sources.isInvalid()) {
            throw IllegalStateException("Could not load config file $path of type ${type.qualifiedName}.")
        }
        return configLoader.loadConfigOrThrow(type, sources.getUnsafe())
    }

    fun loadLanguages(folderPath: String) {
        val folder = File(plugin.dataFolder, folderPath)

        if (!folder.isDirectory) {
            throw IllegalStateException("Specified language folder is not a folder.")
        }
        folder.listFiles()?.forEach {
            if (!it.isFile || it.extension != "json") {
                return@forEach
            }
            val content = it.readText()
            val loader = ConfigLoader.Builder().addDecoder(FlatStringMapDecoder()).addSource(JsonPropertySource("{\"values\"${content}}")).build()
            val lang = loader.loadConfigOrThrow<InnerLanguage>()


        }
    }
}
