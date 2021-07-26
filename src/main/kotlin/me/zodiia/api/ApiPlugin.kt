package me.zodiia.api

import me.zodiia.api.config.KotlinConfigRealm
import me.zodiia.api.plugins.KotlinPlugin
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

class ApiPlugin: KotlinPlugin {
    override val configRealm: KotlinConfigRealm = KotlinConfigRealm(this)

    constructor() : super()
    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File) : super(
        loader, description, dataFolder, file,
    )

    init {
        constructedPlugin = this
    }

    companion object {
        private var constructedPlugin: KotlinPlugin? = null

        val plugin by lazy { constructedPlugin ?: getProvidingPlugin(ApiPlugin::class.java) }
    }
}
