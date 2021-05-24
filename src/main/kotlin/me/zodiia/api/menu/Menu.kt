package me.zodiia.api.menu

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.util.UUID

class Menu(
    dsl: Menu.() -> Unit
) {
    private var openExecutor: (Context) -> Boolean
    private var closeExecutor: (Context) -> Boolean
    var name: String? = null
    var size: Int = 54
    var type: InventoryType = InventoryType.CHEST

    init {
        openExecutor = { true }
        closeExecutor = { true }
        dsl.invoke(this)
    }

    private fun create(): Inventory {
        val inventory =
            if (name != null) {
                if (type != InventoryType.CHEST) {
                    Bukkit.createInventory(null, type, name!!)
                } else {
                    Bukkit.createInventory(null, size, name!!)
                }
            } else {
                if (type != InventoryType.CHEST) {
                    Bukkit.createInventory(null, type)
                } else {
                    Bukkit.createInventory(null, size)
                }
            }


        return inventory
    }

    fun openExecutor(fct: (Context) -> Boolean) {
        openExecutor = fct
    }

    fun closeExecutor(fct: (Context) -> Boolean) {
        closeExecutor = fct
    }

    companion object {
        private val inventories = hashMapOf<UUID, Inventory>()
    }
}