package me.zodiia.api.command

/**
 * Creates a new command
 */
fun command(dsl: Command.() -> Unit): Command = Command(null, dsl)
