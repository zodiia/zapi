package me.zodiia.api.command.definitions

import me.zodiia.api.command.Command
import me.zodiia.api.command.command
import me.zodiia.api.plugins.KotlinPlugin
import me.zodiia.api.util.minecraftChatCentered
import me.zodiia.api.util.send
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder

fun versionCommand(plugin: KotlinPlugin, perm: String? = null): Command = command {
    permission = perm
    description = "Displays the version and information about the plugin"

    executor { ctx ->
        ctx.sender.send("&6${plugin.description.name} &7v&6${plugin.description.version} for Minecraft ${plugin.description.apiVersion}.".minecraftChatCentered())
        if (plugin.description.description != null) {
            ctx.sender.send("&7${plugin.description.description}".minecraftChatCentered())
        }
        ctx.sender.send("")
        with (plugin.getKotlinDescription()) {
            if (hashCode() == 0) {
                return@with
            }
            val component = ComponentBuilder("Spigot page")
                .event(ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/${spigotId}/"))
                .color(ChatColor.GREEN)

            if (urls.homepage != null) {
                component.append(" • ").color(ChatColor.GRAY)
                    .append("Homepage")
                    .event(ClickEvent(ClickEvent.Action.OPEN_URL, urls.homepage))
                    .color(ChatColor.GREEN)
            }
            if (urls.bugs != null) {
                component.append(" • ").color(ChatColor.GRAY)
                    .append("Bugs")
                    .event(ClickEvent(ClickEvent.Action.OPEN_URL, urls.bugs))
                    .color(ChatColor.GREEN)
            }
            if (urls.docs != null) {
                component.append(" • ").color(ChatColor.GRAY)
                    .append("Documentation")
                    .event(ClickEvent(ClickEvent.Action.OPEN_URL, urls.docs))
                    .color(ChatColor.GREEN)
            }
            if (urls.repository != null) {
                component.append(" • ").color(ChatColor.GRAY)
                    .append("Source code")
                    .event(ClickEvent(ClickEvent.Action.OPEN_URL, urls.repository))
                    .color(ChatColor.GREEN)
            }
            ctx.sender.send(component.currentComponent.minecraftChatCentered())
            ctx.sender.send("")
        }
        ctx.sender.send("&aMade with &c♥ &aby ${plugin.description.authors.joinToString(",")}".minecraftChatCentered())
    }
}
