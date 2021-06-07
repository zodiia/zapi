package me.zodiia.api.menu

import me.zodiia.api.util.getItemMetaSafe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType

class Item(
    dsl: Item.() -> Unit,
) {
    private var type = Material.AIR
    private var amount = 1
    private var damage = 0
    private var enchants = mutableListOf<CustomEnchantment>()
    private var attributes = hashMapOf<Attribute, AttributeModifier>()
    private var flags = hashSetOf<ItemFlag>()
    private var name: String? = null
    private var lore: List<String>? = null
    private var unbreakable = false

    init {
        dsl.invoke(this)
    }

    constructor(type: Material, dsl: Item.() -> Unit): this(dsl) {
        this.type = type
    }

    constructor(type: Material, amount: Int, dsl: Item.() -> Unit): this(dsl) {
        this.type = type
        this.amount = amount
    }

    fun damage(damage: Int): Item {
        this.damage = damage
        return this
    }

    fun enchant(type: Enchantment, level: Int, force: Boolean = true): Item {
        enchants.add(CustomEnchantment(type, level, force))
        return this
    }

    fun attribute(type: Attribute, modifier: AttributeModifier): Item {
        attributes[type] = modifier
        return this
    }

    fun flags(vararg flags: ItemFlag): Item {
        flags.forEach { this.flags.add(it) }
        return this
    }

    fun name(name: String?): Item {
        this.name = name
        return this
    }

    fun lore(lore: List<String>?): Item {
        this.lore = lore
        return this
    }

    fun unbreakable(unbreakable: Boolean): Item {
        this.unbreakable = unbreakable
        return this
    }

    fun create(): ItemStack {
        val item = ItemStack(type, amount)
        val meta = item.getItemMetaSafe()

        meta.setDisplayName(name)
        meta.setLore(lore)
        meta.addItemFlags(*flags.toTypedArray())
        attributes.forEach {
            meta.addAttributeModifier(it.key, it.value)
        }
        enchants.forEach {
            meta.addEnchant(it.type, it.level, it.force)
        }
        meta.isUnbreakable = unbreakable
        if (damage != 0) {
            (meta as Damageable).setDamage(damage)
        }
        item.setItemMeta(meta)
        return item
    }

    class CustomEnchantment(
        val type: Enchantment,
        val level: Int,
        val force: Boolean,
    )
}
