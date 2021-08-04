package me.zodiia.api.i18n

import me.zodiia.api.exceptions.MissingLanguageException
import java.io.File
import kotlin.properties.Delegates

open class I18n(defaultId: String) {
    private val languages = mutableSetOf<I18nLanguage>()
    private var current: I18nLanguage? = null
    var currentId = defaultId

    @Throws(MissingLanguageException::class)
    fun get(language: String): I18nLanguage {
        languages.forEach {
            if (it.language == language) {
                return it
            }
        }
        throw MissingLanguageException(language)
    }

    fun add(language: String, map: Map<String, Array<String>>): I18nLanguage {
        val lang = I18nLanguage(language, map)

        languages.add(lang)
        return lang
    }

    @Throws(MissingLanguageException::class)
    fun getCurrent(): I18nLanguage = current ?: throw MissingLanguageException(currentId)

    @Throws(MissingLanguageException::class)
    fun setCurrent(key: String) {
        currentId = key
        current = get(currentId)
    }

    internal fun clear() {
        languages.clear()
    }
}
