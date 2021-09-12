package me.zodiia.api.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.base.toFlatMap
import com.uchuhimo.konf.source.hocon
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import me.zodiia.api.i18n.I18n
import me.zodiia.api.plugins.KotlinPlugin
import java.io.File
import kotlin.reflect.KProperty

abstract class KotlinConfigRealm(
    open val plugin: KotlinPlugin,
    private val defaultI18nId: String = "en",
    private val i18nDirectory: String = "lang",
) {
    private var loadedFiles = hashMapOf<String, Any>()
    val i18n = I18n(defaultI18nId)

    fun <T : Any> configFile(path: String) = ConfigFileDelegate<T>(File(plugin.dataFolder, path), this)

    fun <T : Any> configFileArray(path: String) = ConfigFileArrayDelegate<T>(File(plugin.dataFolder, path), this)

    internal fun <T : Any> loadFile(file: File): T {
        if (loadedFiles.containsKey(file.path)) {
            return loadedFiles[file.path] as T
        }

        val cfg: T = when (file.extension) {
            "json" -> Config().from.json.file(file).toDataClass()
            "yml", "yaml" -> Config().from.yaml.file(file).toDataClass()
            "conf" -> Config().from.hocon.file(file).toDataClass()
            else -> throw IllegalStateException("Config format not accepted: ${file.extension}")
        }
        loadedFiles[file.path] = cfg
        return cfg
    }

    private fun loadLanguages() {
        val folder = File(plugin.dataFolder, i18nDirectory)

        i18n.clear()
        if (!folder.isDirectory) {
            throw IllegalStateException("Specified language folder is not a folder.")
        }
        folder.listFiles()?.forEach { file ->
            if (!file.isFile || (file.extension != "yml" && file.extension != "yaml")) {
                return@forEach
            }
            val config = Config().from.yaml.file(file).toFlatMap()


            i18n.add(file.nameWithoutExtension, config.mapValues { it.value.lines() })
        }
    }

    fun reloadConfig() {
        loadLanguages()
        loadedFiles = hashMapOf()
    }
}
