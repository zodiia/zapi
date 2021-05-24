package me.zodiia.api.events

import me.zodiia.api.ApiPlugin
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object Events: Listener {
    fun <T: Event> emit(event: T) {
        Bukkit.getServer().pluginManager.callEvent(event)
    }

    fun <T: Event> on(
        clazz: Class<out Event>,
        plugin: Plugin = JavaPlugin.getPlugin(ApiPlugin::class.java),
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        fct: (T) -> Unit,
    ) {
        Bukkit.getServer().pluginManager.registerEvent(
            clazz,
            this,
            priority,
            { _, genericEvent ->
                @Suppress("UNCHECKED_CAST")
                fct(genericEvent as? T ?: return@registerEvent)
            },
            plugin,
            ignoreCancelled,
        )
    }
}
