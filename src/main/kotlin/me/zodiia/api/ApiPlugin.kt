package me.zodiia.api

import me.zodiia.api.config.KotlinConfigRealm
import me.zodiia.api.hooks.useI18n
import me.zodiia.api.plugins.KotlinPlugin
import me.zodiia.api.plugins.KotlinPluginDescription
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

class ApiPlugin: KotlinPlugin {
    companion object {
        private var constructedPlugin: KotlinPlugin? = null

        val plugin by lazy { constructedPlugin ?: getProvidingPlugin(ApiPlugin::class.java) }
    }

    init {
        constructedPlugin = this

        kotlinDescription {
            minecraftVersion(">= 1.16.5")

            softPluginDependency("PlaceholderAPI", ">= 2.9")
            softPluginDependency("Vault", ">= 1.7.2")

            spigotId(-1)

            bugsUrl("https://gitlab.com/zodiia/zapi/-/issues")
            docsUrl("https://zodiia.moe/zapi/docs")
            homepageUrl("https://zodiia.moe/zapi")
            repository("https://gitlab.com/zodiia/zapi")
        }
    }

    override val configRealm: KotlinConfigRealm = KotlinConfigRealm(this)

    constructor() : super()
    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File) : super(
        loader, description, dataFolder, file,
    )
}
