package me.zodiia.api.util

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.getPlayerEquipment(): List<ItemStack> {
    val eq = equipment

    return listOfNotNull(
        eq?.itemInMainHand,
        eq?.itemInOffHand,
        eq?.helmet,
        eq?.chestplate,
        eq?.leggings,
        eq?.boots,
    )
}

fun CommandSender.send(vararg messages: String) {
    messages.forEach {
        sendMessage(it.translateColors())
    }
}

fun CommandSender.send(messages: Collection<String>) {
    messages.forEach {
        sendMessage(it.translateColors())
    }
}

@JvmName("sendComponents")
fun CommandSender.send(vararg messages: BaseComponent) {
    messages.forEach {
        spigot().sendMessage(it)
    }
}

@JvmName("sendComponents")
fun CommandSender.send(messages: Collection<BaseComponent>) {
    messages.forEach {
        spigot().sendMessage(it)
    }
}
