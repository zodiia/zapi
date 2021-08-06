package me.zodiia.api.placeholder

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.regex.Pattern

class LocalPlaceholders {
    private val localPlaceholders = hashSetOf<Placeholder>()

    private fun placeholderParse(text: String, player: Player?): String {
        var res = text
        val replacements = hashMapOf<String, String>()
        val matches = PLACEHOLDER_REGEX.matcher(text)

        while (matches.find()) {
            val identifier = text.substring(matches.start() until matches.end())
            val strippedIdentifier = identifier.replace("%", "")
            val identifierKey = strippedIdentifier.split('_')[0]
            val placeholder = localPlaceholders.find { it.name == identifierKey }

            replacements[identifier] = placeholder?.exec(player, strippedIdentifier.substring(identifierKey.length + 1)) ?: "undefined"
        }
        replacements.forEach {
            res = res.replaceFirst(it.key, it.value)
        }
        return res
    }

    fun registerLocally(placeholder: Placeholder) = localPlaceholders.add(placeholder)

    fun registerGlobally(placeholder: Placeholder, plugin: Plugin) {
        if (LocalPlaceholders.placeholderApiAvailable) {
            object: PlaceholderExpansion() {
                override fun getIdentifier(): String = placeholder.name
                override fun getAuthor(): String = plugin.description.authors.toString()
                override fun getVersion(): String = plugin.description.version
                override fun canRegister(): Boolean = true
                override fun persist(): Boolean = true
                override fun onPlaceholderRequest(player: Player?, params: String): String = placeholder.exec(player, params)
            }
        }
    }

    fun parse(text: String, player: Player?): String =
        if (placeholderApiAvailable)
            PlaceholderAPI.setPlaceholders(player, placeholderParse(text, player))
        else
            placeholderParse(text, player)


    companion object {
        private val PLACEHOLDER_REGEX = Pattern.compile("%[^\\s%]+%")
        internal val placeholderApiAvailable: Boolean by lazy { Bukkit.getServer().pluginManager.getPlugin("PlaceholderAPI") != null }
    }
}
