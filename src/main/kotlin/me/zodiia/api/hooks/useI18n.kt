package me.zodiia.api.hooks

import me.zodiia.api.i18n.I18n
import me.zodiia.api.i18n.I18nLanguage
import me.zodiia.api.plugins.KotlinPlugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KProperty

fun useI18n(language: String? = null) = I18nHook(language)

class I18nHook internal constructor(private val language: String? = null) {
    private val i18n: I18n

    init {
        val pl = JavaPlugin.getProvidingPlugin(javaClass)

        if (pl !is KotlinPlugin) {
            throw ClassCastException("Providing plugin is not an instance of KotlinPlugin.")
        }
        i18n = pl.configRealm.i18n
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): I18nLanguage =
        if (language != null)
            i18n.get(language)
        else
            i18n.getCurrent()
}
