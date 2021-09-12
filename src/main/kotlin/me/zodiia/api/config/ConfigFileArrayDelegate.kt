package me.zodiia.api.config

import java.io.File
import kotlin.reflect.KProperty

class ConfigFileArrayDelegate<out T>(
    private val directory: File,
    private val realm: KotlinConfigRealm,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Map<String, T> {
        val values = HashMap<String, T>()

        if (!directory.isDirectory) {
            throw IllegalStateException("Specified path is not a directory.")
        }
        directory.listFiles()?.forEach {
            if (it.isFile) {
                values[it.name] = realm.loadFile(it)
            }
        }
        return values
    }
}