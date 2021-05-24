package me.zodiia.api.command

import org.bukkit.command.Command as BukkitCommand
import me.zodiia.api.logger.Console
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin

object Commands {
    private var commandMap: CommandMap? = null

    init {
        val commandMapField = Bukkit.getPluginManager().javaClass.getDeclaredField("commandMap")

        if (!commandMapField.trySetAccessible()) {
            Console.error("Cannot retrieve the Bukkit command map.")
        } else {
            commandMap = commandMapField.get(Bukkit.getPluginManager()) as CommandMap
        }
    }

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
        if (commandMap == null) {
            Console.error("Cannot register new commands.")
            return
        }
        commandMap?.register(label, prefix.lowercase(), command.getRegistration(label))
    }
}