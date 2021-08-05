package me.zodiia.api.util

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent

const val COLOR_CHARS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx"
const val COLOR_PREFIX = 167.toChar()

fun String.isRegex() = startsWith('/') && endsWith('/')

fun CharArray.match(pattern: CharArray, thisIdx: Int = 0, patternIdx: Int = 0): Boolean {
    if (pattern[patternIdx] == '*') {
        return if (size == thisIdx) {
            match(pattern, thisIdx, patternIdx + 1)
        } else {
            match(pattern, thisIdx + 1, patternIdx) || match(pattern, thisIdx, patternIdx + 1)
        }
    }
    if (size != thisIdx && this[thisIdx] == pattern[patternIdx]) {
        return match(pattern, thisIdx + 1, patternIdx + 1)
    }
    if (size == thisIdx && pattern.size == patternIdx) {
        return true
    }
    return false
}

fun String.match(pattern: String): Boolean {
    return this.toCharArray().match(pattern.toCharArray())
}

fun String.tokenise(): List<String> = split(Regex("\\s+")).map { it.replace(Regex("[^\\s]+"), "") }

fun String.translateColors(altColorChar: Char = '&'): String {
    val chars = toCharArray()

    for (i in chars.indices) {
        if (chars[i] == altColorChar && COLOR_CHARS.indexOf(chars[i + 1]) > -1) {
            chars[i] = COLOR_PREFIX
            chars[i + 1] = Character.toLowerCase(chars[i + 1])
        }
    }
    return String(chars)
}

fun MutableCollection<String>.addPartialAndFullMatches(token: String, values: Iterable<String>) {
    values.forEach {
        if (it.startsWith(token, true) || it.equals(token, true)) {
            add(it)
        }
    }
}

fun Char.minecraftLength() = minecraftCharactersLength[this] ?: 6

fun String.minecraftLength(): Int {
    var res = 0

    this.toCharArray().forEach {
        res += it.minecraftLength()
    }
    return res
}

fun String.minecraftChatCentered(): String {
    val res = StringBuilder()
    val length = this.minecraftLength()

    if (length > 320) {
        return this
    }
    res.append(" ".repeat((320 - length) / 8))
    res.append(this)
    return res.toString()
}

fun BaseComponent.minecraftLength(): Int = this.toPlainText().minecraftLength()

fun BaseComponent.minecraftChatCentered(): BaseComponent {
    val length = this.toPlainText().minecraftLength()

    if (length > 320) {
        return this
    }
    return TextComponent(TextComponent(" ".repeat((320 - length) / 8)), this)
}

private val minecraftCharactersLength = mutableMapOf(
    "i.,!:;|".toCharArray().toHashSet() to 2,
    "l'`".toCharArray().toHashSet() to 3,
    "It[] ".toCharArray().toHashSet() to 4,
    "fk()\"*<>²".toCharArray().toHashSet() to 5,
    "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghjmnopqrsuvwxyzÆÇÉÈÊËÔÖŒÜàæçéèêëñôöü0123456789/\\?&$%+-=#_".toCharArray().toHashSet() to 6,
    "œ~@".toCharArray().toHashSet() to 7,
).flatten()
