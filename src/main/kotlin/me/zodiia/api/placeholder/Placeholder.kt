package me.zodiia.api.placeholder

import org.bukkit.entity.Player
import java.time.Instant
import java.util.regex.Pattern

class Placeholder(
    val name: String,
    dsl: Placeholder.() -> Unit,
) {
    private val placeholders = hashMapOf<String, Placeholder>()
    private var executor: (Context) -> String

    init {
        executor = { "undefined" }
        dsl.invoke(this)
    }

    internal fun exec(player: Player?, identifier: String): String {
        val context = Context(player, Instant.now())
        val key = identifier.split(' ')[0]
        val subPlaceholder = placeholders[key]

        if (subPlaceholder != null) {
            return subPlaceholder.exec(player, identifier.substring(key.length + 1))
        }
        return executor.invoke(context)
    }

    fun placeholder(name: String, dsl: Placeholder.() -> Unit) {
        val placeholder = Placeholder(name, dsl)

        placeholders[name] = placeholder
    }

    fun executor(fct: (Context) -> String) {
        executor = fct
    }
}
