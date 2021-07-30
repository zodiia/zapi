package me.zodiia.api.events

import me.zodiia.api.ApiPlugin
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

object Events : Listener {
    fun <T : Event> emit(event: T) {
        Bukkit.getServer().pluginManager.callEvent(event)
    }

    inline fun <reified T : Event> on(
        plugin: Plugin = ApiPlugin.plugin,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline fct: (T) -> Unit,
    ) {
        Bukkit.getServer().pluginManager.registerEvent(
            T::class.java,
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

    inline fun <T : Event> on(
        clazz: Class<T>,
        plugin: Plugin = ApiPlugin.plugin,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline fct: (T) -> Unit,
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
