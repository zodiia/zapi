package me.zodiia.api.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant

class Context(
    /**
     * Which command is being used
     */
    val command: Command,

    /**
     * Command label
     */
    val label: String,

    /**
     * List of arguments (nulls are missing optional arguments)
     */
    val args: List<String?>,

    /**
     * Who sent the command
     */
    val sender: CommandSender,

    /**
     * Shortcut for the player instance (if the sender is a player)
     */
    val player: Player?,

    /**
     * Instant at which the command was sent
     */
    val instant: Instant,
) {
    internal constructor(sender: CommandSender, label: String, args: List<String?>, command: Command, instant: Instant):
        this(command, label, args, sender, sender as? Player, instant)
}