package me.zodiia.api.command

import me.zodiia.api.ApiPlugin
import me.zodiia.api.reflection.PropertyInjector
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin

object Commands {
    private val commandMap by PropertyInjector<CommandMap>(
        if (ApiPlugin.env == ApiPlugin.Env.TEST)
            Bukkit.getServer()
        else
            Bukkit.getPluginManager(),
        "commandMap",
    )

    /**
     * Register a new command
     *
     * @param label Name of this command
     * @param plugin Which plugin is registering this command
     * @param command Command to register
     */
    fun register(label: String, plugin: Plugin, command: Command) {
        register(label, plugin.name, command)
    }

    /**
     * Register a new command
     *
     * @param label Name of this command
     * @param prefix Fallback prefix (/prefix:command)
     * @param command Command to register
     */
    fun register(label: String, prefix: String, command: Command) {
        commandMap.register(label, prefix.lowercase(), command.getRegistration(label))
    }
}
