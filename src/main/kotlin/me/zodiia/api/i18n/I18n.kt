package me.zodiia.api.i18n

import java.io.File

object I18n {
    private val languages = mutableSetOf<I18nLanguage>()
    private var current = "en"
    private var currentI18n: I18nLanguage? = null

    fun get(language: String): I18nLanguage {
        languages.forEach {
            if (it.language == language) {
                return it
            }
        }
        return get(current)
    }

    fun add(language: String, file: File): I18nLanguage {
        val lang = I18nLanguage(language, file)

        languages.add(lang)
        return lang
    }

    fun getCurrent(): I18nLanguage {
        return currentI18n!!
    }

    fun setCurrent(key: String) {
        current = key
        currentI18n = get(current)
    }
}
