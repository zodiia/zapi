package me.zodiia.api.util

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.regex.Pattern

private val itemFactory = Bukkit.getServer().itemFactory
const val MAX_LORE_LINE_LENGTH = 300 // OPTIMIZE: Dynamic configurations

@Suppress("NestedBlockDepth")
fun ItemMeta.parseLore() { // TODO: Refactoring
    val newLore = mutableListOf<String>()

    if (lore == null) {
        return
    }
    lore?.forEach {
        val line = ChatColor.translateAlternateColorCodes('&', it)
        val res = mutableListOf<String>()
        val words = line.split(' ')
        var currentLine = ""
        var currentLineLength = 0

        for (word in words) {
            var wordLength = 0

            for (ch in word.toCharArray()) {
                wordLength += ch.minecraftLength()
            }
            if (wordLength + currentLineLength > MAX_LORE_LINE_LENGTH) {
                res.add(currentLine)
                currentLine = ChatColor.getLastColors(currentLine) + word
                currentLineLength = wordLength
            } else {
                if (currentLineLength != 0) {
                    currentLine += " "
                    currentLineLength += ' '.minecraftLength()
                }
                currentLine += word
                currentLineLength += wordLength
            }
        }
        res.add(currentLine)
        newLore.addAll(res)
    }
    lore = newLore
}

fun ItemStack.getItemMetaSafe(): ItemMeta {
    if (hasItemMeta()) {
        return itemMeta as ItemMeta
    }
    return itemFactory.getItemMeta(type) as ItemMeta
}

fun Material.match(pattern: String): Boolean {
    if (key.key.equals(pattern, true)) {
        return true
    }
    if (pattern.isRegex()) {
        if (Pattern.matches(pattern.substring(1 until (pattern.length - 1)), key.key)) {
            return true
        }
    } else if (pattern.contains('*')) {
        if (key.key.match(pattern)) {
            return true
        }
    }
    return false
}

fun getMaterials(pattern: String): Set<Material> {
    val res = mutableSetOf<Material>()
    val isRegex = pattern.isRegex()
    val finalPattern = if (isRegex) {
        pattern.substring(1 until (pattern.length - 1))
    } else {
        pattern
    }

    if (isRegex || finalPattern.contains('*')) {
        for (mat in Material.values()) {
            if (isRegex && Pattern.matches(finalPattern, mat.key.key)) {
                res.add(mat)
            } else if (mat.key.key.match(pattern)) {
                res.add(mat)
            }
        }
    } else {
        var mat = Material.matchMaterial(finalPattern)

        if (mat == null) {
            mat = Material.valueOf(finalPattern)
        }
        res.add(mat)
    }
    return res
}


