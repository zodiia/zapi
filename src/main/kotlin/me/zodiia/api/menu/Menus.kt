package me.zodiia.api.menu

import fr.minuskube.inv.InventoryManager
import me.zodiia.api.ApiPlugin
import me.zodiia.api.hooks.usePlugin

object Menus {
    internal val plugin by usePlugin(ApiPlugin::class.java)
    internal val inventoryManager = InventoryManager(plugin)
}
