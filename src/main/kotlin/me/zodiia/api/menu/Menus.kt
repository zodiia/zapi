package me.zodiia.api.menu

import fr.minuskube.inv.InventoryManager
import me.zodiia.api.ApiPlugin

object Menus {
    internal val inventoryManager = InventoryManager(ApiPlugin.plugin)
}