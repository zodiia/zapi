package me.zodiia.api.hooks

import me.zodiia.api.plugins.KotlinPlugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KProperty

fun <T : KotlinPlugin> usePlugin(javaClass: Class<T>) = PluginHook(javaClass)

class PluginHook<T : KotlinPlugin> internal constructor(javaClass: Class<T>) {
    private val plugin: T = JavaPlugin.getPlugin(javaClass)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = plugin
}
