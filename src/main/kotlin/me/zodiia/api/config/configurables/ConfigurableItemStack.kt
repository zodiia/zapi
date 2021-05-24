package me.zodiia.api.config.configurables

import me.zodiia.api.config.ConfigSection
import me.zodiia.api.config.Configurable
import me.zodiia.api.util.getItemMetaSafe
import me.zodiia.api.util.translateColors
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.UUID

data class ConfigurableItemStack(
    var material: Material = Material.AIR,
    var amount: Int = 1,
    var damage: Int = 0,
    var name: String? = null,
    val lore: MutableList<String> = mutableListOf(),
    val enchantments: MutableList<ConfigurableEnchantment> = mutableListOf(),
    val attributes: MutableList<ConfigurableAttribute> = mutableListOf(),
    val flags: MutableSet<ItemFlag> = mutableSetOf(),
): Configurable {
    override fun fromConfig(cfg: ConfigSection) {
        material = Material.matchMaterial(cfg["material"]!!.toValue(String::class.java))!!
        amount = cfg["amount"]?.toValue(Int::class.java) ?: amount
        damage = cfg["damage"]?.toValue(Int::class.java) ?: damage
        name = cfg["name"]?.toValue(String::class.java) ?: name
        cfg["lore"]?.toArray(lore, String::class.java)
        cfg["enchantments"]?.toArray(enchantments, ConfigurableEnchantment::class.java)
        cfg["attributes"]?.toArray(attributes, ConfigurableAttribute::class.java)
        mutableSetOf<String>().apply { cfg["flags"]?.toArray(this, String::class.java) }.map { ItemFlag.valueOf(it) }.forEach { flags.add(it) }
    }

    override fun toConfig(cfg: ConfigSection) {
        cfg["material"] = material.toString()
        cfg["amount"] = amount
        cfg["damage"] = damage
        name?.let { cfg["name"] = it }
        cfg["lore"] = lore
        cfg["enchantments"] = enchantments
        cfg["attributes"] = attributes
        cfg["flags"] = flags.map { it.toString() }
    }

    fun fromItemStack(item: ItemStack) {
        material = item.type
        amount = item.amount
        damage = (item as Damageable).damage

        if (item.hasItemMeta()) {
            val meta = item.getItemMetaSafe()

            if (meta.hasDisplayName()) {
                name = meta.displayName
            }
            if (meta.hasLore()) {
                meta.lore?.forEach { lore.add(it) }
            }
            if (meta.hasEnchants()) {
                meta.enchants.forEach { (ench, lv) ->
                    enchantments.add(ConfigurableEnchantment(ench.key.key, lv))
                }
            }
            if (meta.hasAttributeModifiers()) {
                meta.attributeModifiers?.forEach { attr, modif ->
                    attributes.add(ConfigurableAttribute(attr, modif.operation, modif.amount, modif.name, modif.slot))
                }
            }
            meta.itemFlags.forEach { flags.add(it) }
        }
    }

    fun toItemStack(): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.getItemMetaSafe()

        if (damage != 0) {
            (meta as? Damageable)?.damage = damage
        }
        if (name != null) {
            meta.setDisplayName(name)
        }
        if (lore.size != 0) {
            val finalLore = mutableListOf<String>()

            lore.forEach {
                finalLore.add(it.translateColors())
            }
            meta.lore = finalLore
        }
        if (enchantments.size != 0) {
            enchantments.forEach {
                Enchantment.getByKey(NamespacedKey.minecraft(it.id))?.let { it1 -> meta.addEnchant(it1, it.level, true) }
            }
        }
        if (attributes.size != 0) {
            attributes.forEach {
                meta.addAttributeModifier(it.id, AttributeModifier(UUID.randomUUID(), it.name, it.amount, it.operation, it.slot))
            }
        }
        if (flags.size != 0) {
            flags.forEach {
                meta.addItemFlags(it)
            }
        }
        item.itemMeta = meta
        return item
    }

    companion object {
        fun fromItemStack(item: ItemStack): ConfigurableItemStack {
            return ConfigurableItemStack().also { it.fromItemStack(item) }
        }
    }

    data class ConfigurableEnchantment(
        var id: String,
        var level: Int = 1,
    ): Configurable {
        override fun fromConfig(cfg: ConfigSection) {
            id = cfg["id"]!!.toValue(String::class.java)
            level = cfg["level"]?.toValue(Int::class.java) ?: level
        }

        override fun toConfig(cfg: ConfigSection) {
            cfg["id"] = id
            cfg["level"] = level
        }
    }

    data class ConfigurableAttribute(
        var id: Attribute,
        var operation: AttributeModifier.Operation,
        var amount: Double,
        var name: String,
        var slot: EquipmentSlot?,
    ): Configurable {
        override fun fromConfig(cfg: ConfigSection) {
            id = Attribute.valueOf(cfg["id"]!!.toValue(String::class.java))
            operation = AttributeModifier.Operation.values()[cfg["operation"]!!.toValue(Int::class.java)]
            amount = cfg["amount"]!!.toValue(Double::class.java)
            name = cfg["name"]!!.toValue(String::class.java)
            if (cfg["slot"] != null) {
                slot = EquipmentSlot.valueOf(cfg["slot"]!!.toValue(String::class.java))
            }
        }

        override fun toConfig(cfg: ConfigSection) {
            cfg["id"] = id.toString()
            cfg["operation"] = AttributeModifier.Operation.values().indexOf(operation)
            cfg["amount"] = amount
            cfg["name"] = name
            slot?.let { cfg["slot"] = it.name }
        }
    }
}
