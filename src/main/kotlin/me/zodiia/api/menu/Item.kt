package me.zodiia.api.menu

import java.time.Instant

class Item(
    dsl: Item.() -> Unit
) {
    internal var clickExecutor: ((Context) -> Boolean)
    internal var moveExecutor: ((Context) -> Boolean)
    internal var dragExecutor: ((Context) -> Boolean)
    var slot: Int? = null
    var slots: Collection<Int>? = null
    var name: String? = null
    var lore: List<String>? = null
    var enchanted: Boolean = false
    var update: Long? = null

    init {
        clickExecutor = { true }
        moveExecutor = { true }
        dragExecutor = { true }
        dsl.invoke(this)
    }

    fun clickExecutor(fct: (Context) -> Boolean) {
        clickExecutor = fct
    }

    fun moveExecutor(fct: (Context) -> Boolean) {
        moveExecutor = fct
    }

    fun dragExecutor(fct: (Context) -> Boolean) {
        dragExecutor = fct
    }

    fun updateExecutor(dsl: Item.(Context) -> Unit) {
        dsl.invoke(this, Context(Instant.now()))
    }
}