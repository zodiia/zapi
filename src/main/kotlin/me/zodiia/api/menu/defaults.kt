package me.zodiia.api.menu

import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.SlotPos
import kotlin.reflect.KProperty

fun InventoryContents.set(row: Int, column: Int, item: Item): InventoryContents = this.set(row, column, item.create())
fun InventoryContents.set(slotPos: SlotPos, item: Item): InventoryContents = this.set(slotPos, item.create())
fun InventoryContents.add(item: Item): InventoryContents = this.add(item.create())
fun InventoryContents.fill(item: Item): InventoryContents = this.fill(item.create())
fun InventoryContents.fillRow(row: Int, item: Item): InventoryContents = this.fillRow(row, item.create())
fun InventoryContents.fillColumn(column: Int, item: Item): InventoryContents = this.fillColumn(column, item.create())
fun InventoryContents.fillBorders(item: Item): InventoryContents = this.fillBorders(item.create())
fun InventoryContents.fillRect(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int, item: Item): InventoryContents =
    this.fillRect(fromRow, fromColumn, toRow, toColumn, item.create())
fun InventoryContents.fillRect(fromPos: SlotPos, toPos: SlotPos, item: Item): InventoryContents = this.fillRect(fromPos, toPos, item.create())

fun <T : Any> InventoryContents.state(defaultValue: T) = object {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T = this@state.property("state:${prop.name}", defaultValue)

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
        this@state.setProperty("state:${prop.name}", value)
    }
}

fun InventoryContents.effect(vararg dependencies: Any?, effect: () -> Unit) {
    val id = StackWalker.getInstance().walk {
        it.skip(1).findFirst()
    }.get().lineNumber
    val lastHash = this.property("effect:$id", 0)
    var hash = 0

    dependencies.forEach {
        hash = hash xor it.hashCode()
    }
    if (lastHash != hash) {
        this.setProperty("effect:$id", hash)
        effect()
    }
}

fun InventoryContents.eachTicks(ticks: Int, action: () -> Unit) {
    val id = StackWalker.getInstance().walk {
        it.skip(1).findFirst()
    }.get().lineNumber
    val count = this.property("timer:$id", 1)

    if (count == ticks) {
        action()
        this.setProperty("timer:$id", 1)
    }
    this.setProperty("timer:$id", count + 1)
}
