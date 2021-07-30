package me.zodiia.api.plugins

import com.sksamuel.hoplite.ConfigLoader
import com.vdurmont.semver4j.Semver
import com.vdurmont.semver4j.SemverException
import me.zodiia.api.config.KotlinConfigRealm
import me.zodiia.api.logger.Console
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

abstract class KotlinPlugin: JavaPlugin {
    companion object {
        var envMode = EnvironmentMode.PRODUCTION
    }

    internal var updates: String? = null
    abstract val configRealm: KotlinConfigRealm
    val kotlinDescription: KotlinPluginDescription =
        ConfigLoader().loadConfigOrThrow(Paths.get(javaClass.getResource("plugin.json")?.toURI()
            ?: throw FileNotFoundException("Could not find Kotlin plugin file (plugin.json).")))

    constructor(): super()
    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File): super(
        loader, description, dataFolder, file,
    )

    override fun onLoad() {
        setEnvMode()
        checkMinecraftVersion()
        try {
            checkRequiredPluginDependencies()
            checkPluginDependenciesVersions()
        } catch (err: SemverException) {
            throw IllegalStateException("Could not parse version ranges.", err)
        }
    }

    override fun onEnable() {
        saveResources()
        Bukkit.getScheduler().runTask(this, KotlinPluginUpdateChecker(this))
    }

    override fun onDisable() {

    }

    final override fun getConfig(): Nothing = throw IllegalStateException("Config file provided from Bukkit is not supported. Please use KotlinPlugin#configRealm")

    private fun checkMinecraftVersion() {
        if (!checkVersionRange(kotlinDescription.minecraftVersion, Bukkit.getVersion())) {
            Console.error(
                "Server's Minecraft version is incompatible with this plugin.",
                "Please update to a Minecraft version that satisfies this requirement: ${kotlinDescription.minecraftVersion}"
            )
            throw IllegalStateException("Server's Minecraft version is incompatible with this plugin.")
        }
    }

    private fun checkRequiredPluginDependencies() {
        val missingPlugins = hashMapOf<String, String>()

        kotlinDescription.pluginDependencies?.forEach {
            if (Bukkit.getPluginManager().getPlugin(it.key) == null) {
                missingPlugins[it.key] = it.value
            }
        }
        if (missingPlugins.size > 0) {
            Console.error(
                "One or more plugins are missing on the server.",
                "Please install the following plugin(s) on your server:"
            )
            missingPlugins.forEach {
                Console.error("  - ${it.key} (version: ${it.value})")
            }
            throw IllegalStateException("One or more plugins are missing on the server.")
        }
    }

    private fun checkPluginDependenciesVersions() {
        val invalidVersions = hashMapOf<String, Pair<String, String>>()

        kotlinDescription.pluginDependencies?.forEach {
            if (!checkVersionRange(it.value, Bukkit.getPluginManager().getPlugin(it.key)!!.description.version)) {
                invalidVersions[it.key] = it.value to Bukkit.getPluginManager().getPlugin(it.key)!!.description.version
            }
        }
        kotlinDescription.softPluginDependencies?.forEach {
            if (Bukkit.getPluginManager().getPlugin(it.key) == null) {
                return@forEach
            }
            if (!checkVersionRange(it.value, Bukkit.getPluginManager().getPlugin(it.key)!!.description.version)) {
                invalidVersions[it.key] = it.value to Bukkit.getPluginManager().getPlugin(it.key)!!.description.version
            }
        }
        if (invalidVersions.size > 0) {
            Console.error(
                "One or more plugin dependencies have an incompatible version installed on the server.",
                "Please update the following plugin(s) on your server to a compatible version:"
            )
            invalidVersions.forEach {
                Console.error("  - ${it.key} (current: ${it.value.first}, required: ${it.value.second}")
            }
            throw IllegalStateException("One or more plugin dependencies have an incompatible version installed on the server.")
        }
    }

    private fun setEnvMode() {
        if (envMode == EnvironmentMode.TEST) {
            Console.warn(
                "You are running in test mode.",
                "If you see this message in your server's console, please report it immediately."
            )
        } else if (!Semver(description.version).isStable) {
            Console.warn(
                "You are running a development version of ${description.name}. Please be aware that unknown bugs may occur.",
                "The author of this plugin is not responsible for any damage to your server because of bugs from this plugin."
            )
            kotlinDescription.urls?.bugs?.let {
                Console.warn("Please report any bug you may find to ${it}.")
            }
            envMode = EnvironmentMode.DEVELOPMENT
        }
    }

    private fun saveResources() {
        kotlinDescription.files?.forEach {
            // Write files
        }
    }

    private fun checkVersionRange(range: String, version: String?): Boolean =
        if (version == null)
            false
        else
            Semver(version, Semver.SemverType.NPM).satisfies(range)
}
