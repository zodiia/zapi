package me.zodiia.api.config.configurables

import me.zodiia.api.config.ConfigSection
import me.zodiia.api.config.Configurable
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

data class ConfigurableInventory(
    var inventoryType: InventoryType = InventoryType.CHEST,
    var size: Int? = null,
    var title: String? = null,
    val items: ArrayList<ConfigurableItemStack?> = arrayListOf(),
): Configurable {
    override fun fromConfig(cfg: ConfigSection) {
        cfg["inventoryType"]?.let { inventoryType = InventoryType.valueOf(it.toValue(String::class.java)) }
        size = cfg["size"]?.toValue(Int::class.java) ?: size
        title = cfg["title"]?.toValue(String::class.java) ?: title
        cfg["items"]?.toNullableArray(items, ConfigurableItemStack::class.java)
    }

    override fun toConfig(cfg: ConfigSection) {
        cfg["inventoryType"] = inventoryType.toString()
        size?.let { cfg["size"] = it }
        title?.let { cfg["title"] = it }
        cfg["items"] = items
    }

    fun fromInventory(inventory: Inventory) {
        inventoryType = inventory.type
        size = inventory.size
        items.addAll(inventory.contents.map {
            if (it == null) {
                null
            } else {
                ConfigurableItemStack.fromItemStack(it)
            }
        })
    }

    fun toInventory(holder: InventoryHolder? = null): Inventory {
        val inventory: Inventory =
            if (title != null) {
                if (size != null) {
                    Bukkit.createInventory(holder, size!!, title!!)
                } else {
                    Bukkit.createInventory(holder, inventoryType, title!!)
                }
            } else {
                if (size != null) {
                    Bukkit.createInventory(holder, size!!)
                } else {
                    Bukkit.createInventory(holder, inventoryType)
                }
            }

        items.forEachIndexed { idx, it ->
            if (it == null) {
                inventory.setItem(idx, null)
            } else {
                inventory.setItem(idx, it.toItemStack())
            }
        }
        return inventory
    }
}
