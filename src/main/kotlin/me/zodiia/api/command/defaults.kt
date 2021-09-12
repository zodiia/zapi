package me.zodiia.api.command

/**
 * Creates a new command
 */
fun command(label: String = "", dsl: Command.() -> Unit): Command = Command(null, label, dsl)
