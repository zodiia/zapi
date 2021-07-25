package me.zodiia.api.menu

import fr.minuskube.inv.InventoryListener
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryType

class Menu(
    dsl: Menu.() -> Unit,
) {
    companion object {
        const val DEFAULT_ROWS = 6
        const val DEFAULT_COLUMNS = 9
    }

    private val builder = SmartInventory.builder()
    private val smartInventory: SmartInventory
    private var initFct: (Player?, InventoryContents?) -> Unit
    private var updateFct: (Player?, InventoryContents?) -> Unit
    var id: String
        get() = throw IllegalStateException("Cannot get the value.")
        set(value) { builder.id(value) }
    var title: String
        get() = throw IllegalStateException("Cannot get the value.")
        set(value) { builder.title(title) }
    var type: InventoryType
        get() = throw IllegalStateException("Cannot get the value.")
        set(value) { builder.type(type) }
    var rows: Int = DEFAULT_ROWS
        set(value) { builder.size(value, columns); field = value }
    var columns: Int = DEFAULT_COLUMNS
        set(value) { builder.size(rows, value); field = value }
    var closeable: Boolean
        get() = throw IllegalStateException("Cannot get the value.")
        set(value) { builder.closeable(closeable) }

    init {
        initFct = { _, _ -> }
        updateFct = { _, _ -> }
        dsl.invoke(this)
        builder.manager(Menus.inventoryManager)
        builder.provider(asProvider())
        smartInventory = builder.build()
    }

    private fun asProvider(): InventoryProvider = object: InventoryProvider {
        override fun init(player: Player?, contents: InventoryContents?) {
            initFct.invoke(player, contents)
        }

        override fun update(player: Player?, contents: InventoryContents?) {
            updateFct.invoke(player, contents)
        }
    }

    fun <T: Event> listener(type: Class<T>, fct: (T) -> Unit) {
        builder.listener(InventoryListener(type, fct))
    }

    fun init(fct: (Player?, InventoryContents?) -> Unit) {
        initFct = fct
    }

    fun update(fct: (Player?, InventoryContents?) -> Unit) {
        updateFct = fct
    }
}
