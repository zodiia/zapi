package me.zodiia.api.config

import java.io.File
import kotlin.reflect.KProperty

class ConfigFileDelegate<out T>(
    private val file: File,
    private val realm: KotlinConfigRealm,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = realm.loadFile(file)
}