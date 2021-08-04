package me.zodiia.api.hooks

import me.zodiia.api.plugins.KotlinPlugin
import org.bukkit.plugin.java.JavaPlugin

inline fun <reified T: KotlinPlugin> usePlugin() = JavaPlugin.getPlugin(T::class.java)
