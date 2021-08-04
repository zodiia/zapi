package me.zodiia.api.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigSource
import com.sksamuel.hoplite.json.JsonPropertySource
import me.zodiia.api.config.decoders.FlatStringMapDecoder
import me.zodiia.api.i18n.I18n
import me.zodiia.api.plugins.KotlinPlugin
import java.io.File
import kotlin.reflect.KClass

abstract class KotlinConfigRealm(
    private val plugin: KotlinPlugin,
    defaultI18nId: String = "en",
) {
    private data class InnerLanguage(val values: Map<String, Array<String>>)

    private val configLoader = ConfigLoader.Builder().addDecoder(FlatStringMapDecoder()).build()
    val i18n = I18n(defaultI18nId)

    fun <T : Any> load(path: String, type: KClass<T>): T = load(File(plugin.dataFolder, path), type)

    fun <T : Any> load(file: File, type: KClass<T>): T {
        val sources = ConfigSource.fromFiles(listOf(file))

        if (sources.isInvalid()) {
            throw IllegalStateException("Could not load config file ${file.path} of type ${type.qualifiedName}.")
        }
        return configLoader.loadConfigOrThrow(type, sources.getUnsafe())
    }

    fun loadLanguages(folderPath: String) {
        val folder = File(plugin.dataFolder, folderPath)

        i18n.clear()
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

            i18n.add(it.nameWithoutExtension, lang.values)
        }
    }

    abstract fun reloadConfig()
}
