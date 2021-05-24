package me.zodiia.api.util

const val COLOR_CHARS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx"
const val COLOR_PREFIX = 167.toChar()

fun String.isRegex() = startsWith('/') && endsWith('/')

fun CharArray.match(pattern: CharArray, thisIdx: Int = 0, patternIdx: Int = 0): Boolean {
    if (pattern[patternIdx] == '*') {
        if (size == thisIdx) {
            return match(pattern, thisIdx, patternIdx + 1)
        }
        return match(pattern, thisIdx + 1, patternIdx) || match(pattern, thisIdx, patternIdx + 1)
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
