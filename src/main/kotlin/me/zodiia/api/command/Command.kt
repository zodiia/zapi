package me.zodiia.api.command

import me.zodiia.api.util.translateColors
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.command.Command as BCommand
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.util.StringUtil
import java.time.Instant
import java.util.Arrays

class Command(dsl: Command.() -> Unit) {
    private val subcommands = hashMapOf<String, Command>()
    private val arguments = sortedMapOf<Int, Argument>()
    private var executor: ((Context) -> Unit)
    private var syntaxExecutor: ((Context, Int) -> Unit)
    private var permissionExecutor: ((Context) -> Unit)
    private var internalErrorExecutor: ((Context, Throwable) -> Unit)

    /**
     * Defines the permission required to use this command
     */
    var permission: String? = null

    /**
     * A list of aliases for this command
     */
    var aliases: Collection<String> = hashSetOf()

    /**
     * Description of the command
     */
    var description: String? = null

    init {
        executor = { ctx ->
            ctx.sender.sendMessage("&cWhoops! It seems that the developer forgot to implement this command...".translateColors())
        }
        syntaxExecutor = { ctx, _ ->
            ctx.sender.sendMessage("&cError: Invalid syntax.".translateColors())
        }
        permissionExecutor = { ctx ->
            ctx.sender.sendMessage("&cError: Missing permissions.".translateColors())
        }
        internalErrorExecutor = { ctx, _ ->
            ctx.sender.sendMessage("&cError: Internal error.".translateColors())
        }
        dsl.invoke(this)
    }

    internal fun getRegistration(label: String): BukkitCommand {
        return object : BukkitCommand(label) {
            override fun execute(sender: CommandSender, alias: String, args: Array<out String>): Boolean {
                return commandExecute(sender, alias, args, Instant.now()).run { true }
            }

            override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
                return commandTabComplete(sender, alias, args, Instant.now())
            }
        }
    }

    private fun commandExecute(sender: CommandSender, alias: String, args: Array<out String>, instant: Instant) {
        val values = parseArguments(args)
        val context = Context(sender, alias, values, this, instant)

        try {
            if (args.isNotEmpty() && subcommands.containsKey(args[0])) {
                val subcommand = findSubcommand(args[0])

                if (subcommand != null) {
                    subcommands[args[0]]!!.commandExecute(sender, "$alias ${args[0]}", args.copyOfRange(1, args.size), instant)
                    return
                }
            }
            if (sender is Player && permission != null && !sender.hasPermission(permission!!)) {
                permissionExecutor(context)
            }
            for (pair in arguments) {
                val arg = pair.value.value(pair.key, args)

                if (!pair.value.test(context, arg)) {
                    syntaxExecutor(context, pair.key)
                    return
                }
            }
            executor(context)
        } catch (th: Throwable) {
            internalErrorExecutor(context, th)
        }
    }

    private fun commandTabComplete(sender: CommandSender, alias: String, args: Array<out String>, instant: Instant): MutableList<String> {
        val values = parseArguments(args)
        val context = Context(sender, alias, values, this, instant)

        try {
            if (args.isNotEmpty()) {
                val subcommand = findSubcommand(args[0])

                if (subcommand != null) {
                    return subcommands[args[0]]!!.commandTabComplete(sender, "$alias ${args[0]}", args.copyOfRange(1, args.size), instant)
                }
            }
            if (permission != null && !sender.hasPermission(permission!!)) {
                return mutableListOf()
            }
            val argument = getArgument(args.size - 1) ?: return mutableListOf()
            val possibleValues = argument.get(context)
            val finalList = mutableListOf<String>()

            for (subcommand in subcommands) {
                if (subcommand.value.permission != null && sender.hasPermission(subcommand.value.permission!!)) {
                    possibleValues.add(subcommand.key)
                }
            }
            StringUtil.copyPartialMatches(args[args.size - 1], possibleValues, finalList)
            return finalList
        } catch (th: Throwable) {
            internalErrorExecutor(context, th)
            return mutableListOf()
        }
    }

    private fun parseArguments(args: Array<out String>): List<String?> {
        val res = mutableListOf<String?>()

        arguments.forEach { res.add(it.value.value(it.key, args)) }
        return res
    }

    private fun getArgument(idx: Int): Argument? {
        if (arguments.containsKey(idx)) {
            return arguments[idx]
        }
        if (arguments.size <= idx && arguments[arguments.size - 1]?.long == true) {
            return arguments[arguments.size - 1]
        }
        return null
    }

    private fun findSubcommand(key: String): Command? {
        for (subcommand in subcommands) {
            if (subcommand.key == key) {
                return subcommand.value
            }
            if (subcommand.value.aliases.contains(key)) {
                return subcommand.value
            }
        }
        return null
    }

    /**
     * Creates a new subcommand
     *
     * @param name Primary name (syntax) of the subcommand
     * @param dsl Subcommand definition
     */
    fun subcommand(name: String, dsl: Command.() -> Unit) {
        val cmd = Command(dsl)

        cmd.internalErrorExecutor = internalErrorExecutor
        cmd.permissionExecutor = permissionExecutor
        cmd.syntaxExecutor = syntaxExecutor
        subcommands[name] = cmd
    }

    /**
     * Creates a new command argument
     *
     * @param idx Argument index (always starts at 0)
     * @param dsl Argument definition
     */
    fun argument(idx: Int, dsl: Argument.() -> Unit) {
        arguments[idx] = Argument(dsl)
    }

    /**
     * Command executor
     */
    fun executor(fct: (Context) -> Unit) {
        executor = fct
    }

    /**
     * Bad permissions executor
     */
    fun permissionExecutor(fct: (Context) -> Unit) {
        permissionExecutor = fct
    }

    /**
     * Invalid syntax executor
     */
    fun syntaxExecutor(fct: (Context, Int) -> Unit) {
        syntaxExecutor = fct
    }

    /**
     * Internal error executor
     */
    fun internalErrorExecutor(fct: (Context, Throwable) -> Unit) {
        internalErrorExecutor = fct
    }
}