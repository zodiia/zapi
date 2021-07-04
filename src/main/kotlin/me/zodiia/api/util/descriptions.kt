package me.zodiia.api.util

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta

private val romanNumberMap = sortedMapOf(
    1000 to "M",
    900 to "CM",
    500 to "D",
    400 to "CD",
    100 to "C",
    90 to "XC",
    50 to "L",
    40 to "XL",
    10 to "X",
    9 to "IX",
    5 to "V",
    4 to "IV",
    1 to "I",
)

fun Int.toRomanString(): String {
    if (this < 1) {
        return ""
    }

    var key: Int = 0
    for (k in romanNumberMap.keys.reversed()) {
        if (k <= this) {
            key = k
            break
        }
    }

    return if (this == key) {
        romanNumberMap[key]!!
    } else {
        romanNumberMap[key]!! + (this - key).toRomanString()
    }
}

@Deprecated("Work in progress, do not use!")
fun ItemMeta.putItemEnchantsInLore() {
    if (hasLore()) {
        return
    }
    if (hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
        lore = listOf<String>()
        return
    }

    val newLore = mutableListOf<String>()

    enchants.forEach { entry -> run {
        if (entry.key.maxLevel == 1) {
            newLore.add("§7${entry.key.key.key}")
        } else {
            newLore.add("§7${entry.key.key.key} §c${entry.value.toRomanString()}")
        }
    } }
}
