package me.zodiia.api.command.definitions

import me.zodiia.api.command.Command
import me.zodiia.api.command.command
import me.zodiia.api.hooks.useI18n
import me.zodiia.api.plugins.KotlinPlugin
import me.zodiia.api.util.Vault.permission
import me.zodiia.api.util.send
import kotlin.math.ceil

fun helpCommand(
    perm: String? = null,
    descriptionKey: String? = null,
    headerKey: String? = null,
    lineKey: String,
    footerKey: String? = null,
    perPage: Int = 6,
): Command = command {
    permission = perm
    description = descriptionKey

    val lines = hashMapOf<String, String>()
    val i18n by useI18n()

    fun addCommands(label: String, cmd: Command) {
        if (!cmd.group) {
            val labelKeys = mutableListOf(label)

            cmd.arguments.forEach { (_, it) ->
                if (it.required) {
                    labelKeys.add("<${i18n.get(it.name)}>")
                } else {
                    labelKeys.add("[${i18n.get(it.name)}?]")
                }
            }
            lines[labelKeys.joinToString(" ")] = i18n.get(cmd.description)
        }
        cmd.subcommands.forEach { (subLabel, subCmd) ->
            addCommands("$label $subLabel", subCmd)
        }
    }

    addCommands("/${getCompleteLabel()}", parent!!)

    executor { ctx ->
        val maxPage = ceil(lines.size / perPage.toDouble())
        val i18nKeys = mapOf(
            "label" to ctx.label,
            "page" to (ctx.args[0] ?: "1"),
            "maxPage" to maxPage.toString(),
            "sender" to ctx.sender.name,
        )
        var count = 0

        headerKey?.let {
            ctx.sender.send(i18n.get(it, i18nKeys))
        }
        ctx.sender.send(lines
            .filter {
                var page = (ctx.args[0] ?: "1").toIntOrNull()

                if (page == null || page < 1 || page > maxPage) {
                    page = 1
                }
                count++
                return@filter (ceil(count / perPage.toDouble()).toInt() == page)
            }
            .map {
                val i18nLine = mapOf(
                    "label" to it.key,
                    "description" to it.value,
                )

                i18n.get(lineKey, i18nLine)
            }
        )
        footerKey?.let {
            ctx.sender.send(i18n.get(it, i18nKeys))
        }
    }
}
