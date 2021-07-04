package me.zodiia.api

import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

/**
 *
 */
class ApiPlugin: JavaPlugin {
    constructor() : super()
    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File) : super(
        loader, description, dataFolder, file,
    )

    init {
        constructedPlugin = this
    }

    companion object {
        private var constructedPlugin: JavaPlugin? = null
        internal var env = Env.PRODUCTION

        val plugin by lazy { constructedPlugin ?: getProvidingPlugin(ApiPlugin::class.java) }

    }

    internal enum class Env { TEST, DEV, PRODUCTION }
}
