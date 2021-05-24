package me.zodiia.api.util

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit

object Vault {
    val economy: Economy? by lazy {
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
            null
        } else {
            Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider
        }
    }

    val permission: Permission? by lazy {
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
            null
        } else {
            Bukkit.getServer().servicesManager.getRegistration(Permission::class.java)?.provider
        }
    }

    val chat: Chat? by lazy {
        if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
            null
        } else {
            Bukkit.getServer().servicesManager.getRegistration(Chat::class.java)?.provider
        }
    }
}