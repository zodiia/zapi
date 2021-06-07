package me.zodiia.api

import me.zodiia.api.threads.Threads
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

class ApiPlugin: JavaPlugin {
    constructor() : super()
    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File) : super(
        loader, description, dataFolder, file
    )

    companion object {
        val plugin by lazy { getPlugin(ApiPlugin::class.java) }
    }
}
