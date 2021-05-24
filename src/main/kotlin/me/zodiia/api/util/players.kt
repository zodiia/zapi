package me.zodiia.api.util

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
        sendMessage(ChatColor.translateAlternateColorCodes('&', it))
    }
}
