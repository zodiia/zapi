package me.zodiia.api.command

import me.zodiia.api.util.addPartialAndFullMatches
import me.zodiia.api.util.translateColors
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.time.Instant

class Command(dsl: Command.() -> Unit) {
    private val subcommands = hashMapOf<String, Command>()
    private val arguments = sortedMapOf<Int, Argument>()
    private var executor: ((Context) -> Unit)
    private var syntaxReassigned = false
    private var syntaxExecutor: ((Context, Int) -> Unit)
    private var permissionReassigned = false
    private var permissionExecutor: ((Context) -> Unit)
    private var internalErrorReassigned = false
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
        internalErrorExecutor = { ctx, th ->
            ctx.sender.sendMessage("&cError: ${th.message ?: th.javaClass.name}.".translateColors())
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
            if (args.isNotEmpty()) {
                val subcommand = findSubcommand(args[0])

                if (subcommand != null) {
                    subcommand.commandExecute(sender, "$alias ${args[0]}", args.copyOfRange(1, args.size), instant)
                    return
                }
            }
            if (sender is Player && permission != null && !sender.hasPermission(permission!!)) {
                permissionExecutor(context)
                return
            }
            if (arguments.size < args.size && arguments[arguments.size - 1]?.long == false) {
                syntaxExecutor(context, -1)
                return
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
            if (args.size >= 2) {
                val subcommand = findSubcommand(args[0])

                if (subcommand != null) {
                    return subcommands[args[0]]!!.commandTabComplete(sender, "$alias ${args[0]}", args.copyOfRange(1, args.size), instant)
                }
            }
            if (permission != null && !sender.hasPermission(permission!!)) {
                return mutableListOf()
            }
            val argument = getArgument(args.size - 1)
            val possibleValues = argument?.get(context) ?: mutableListOf()
            val finalList = mutableListOf<String>()

            for (subcommand in subcommands) {
                if ((subcommand.value.permission != null && sender.hasPermission(subcommand.value.permission!!)) ||
                        subcommand.value.permission == null) {
                    possibleValues.add(subcommand.key)
                    possibleValues.addAll(subcommand.value.aliases)
                }
            }
            finalList.addPartialAndFullMatches(args[args.size - 1], possibleValues)
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

        subcommand(name, cmd)
    }

    /**
     * Creates a new subcommand from an already defined object
     *
     * @param name Primary name (syntax) of the command
     * @param cmd Subcommand object
     */
    fun subcommand(name: String, cmd: Command) {
        if (!cmd.internalErrorReassigned) {
            cmd.internalErrorExecutor = internalErrorExecutor
        }
        if (!cmd.permissionReassigned) {
            cmd.permissionExecutor = permissionExecutor
        }
        if (!cmd.syntaxReassigned) {
            cmd.syntaxExecutor = syntaxExecutor
        }
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
     * Adds a new command argument from an already defined object
     *
     * @param idx Argument index (always starts at 0)
     * @param arg Argument object
     */
    fun argument(idx: Int, arg: Argument) {
        arguments[idx] = arg
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
        permissionReassigned = true
        permissionExecutor = fct
    }

    /**
     * Invalid syntax executor
     */
    fun syntaxExecutor(fct: (Context, Int) -> Unit) {
        syntaxReassigned = true
        syntaxExecutor = fct
    }

    /**
     * Internal error executor
     */
    fun internalErrorExecutor(fct: (Context, Throwable) -> Unit) {
        internalErrorReassigned = true
        internalErrorExecutor = fct
    }
}
