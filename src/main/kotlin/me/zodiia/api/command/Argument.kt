package me.zodiia.api.command

/**
 * A class representing a single argument in a command. Usually created from a [Command] instance.
 *
 * @author Zodiia
 * @since 5.0.0
 *
 * @constructor
 * Create an argument in a DSL form.
 *
 * @param dsl Argument DSL
 */
class Argument(dsl: Argument.() -> Unit) {
    private val filters = hashSetOf<(String) -> Boolean>()
    private val providers = hashSetOf<HashSet<Any>.(Context) -> Unit>()
    private val staticValues = hashSetOf<String>()

    /**
     * Controls whether if this argument is a long argument.
     *
     * A long argument is an argument that can have more than one word.
     * Be aware that a long argument must be the last argument in your command, as it will overlap next arguments.
     *
     * Default value: `false`
     */
    var long: Boolean = false

    /**
     * Controls whether if this argument is required.
     *
     * If the argument is required, the command will fail when run if the argument is missing.
     *
     * When the argument is optional and the player doesn't specify it, the value in the context will be `null`.
     *
     * Default value: `true`
     */
    var required: Boolean = true

    /**
     * Controls which permission is required to use this argument.
     *
     * Default value: none
     */
    var permission: String? = null

    /**
     * Controls the name's i18n key of this argument.
     *
     * It will be used for help commands, and should accurately describe what kind of argument it is, in a very short manner.
     *
     * Default value: none
     */
    var name: String? = null

    init {
        dsl.invoke(this)
    }

    internal fun get(context: Context): HashSet<String> {
        val values = hashSetOf<Any>()

        providers.forEach { it(values, context) }
        return values.map { it.toString() }.toHashSet().also { it.addAll(staticValues) }
    }

    internal fun test(context: Context, value: String?): Boolean {
        if (permission != null && !context.sender.hasPermission(permission!!)) {
            return false
        }
        if (value == null) {
            return !required
        }
        if (get(context).contains(value) || filters.find { it(value) } != null) {
            return true
        }
        return false
    }

    internal fun value(idx: Int, args: Array<out String>): String? {
        if (args.size <= idx || idx < 0) {
            return null
        }
        return if (long) {
            args.copyOfRange(idx, args.size).joinToString(" ")
        } else {
            args[idx]
        }
    }

    /**
     * Provide a list of static values displayed in the tab completer (Brigadier).
     *
     * These values will be calculated only one time, when the command is created. If you need to use dynamic arguments (such as a player list),
     * please use [Argument#completer].
     *
     * Do not use a static completer if you want to add a lot of values (such as any integer), use [Argument#filter] instead.
     *
     * You can add multiple static completers in a single argument.
     *
     * @param dsl Completer DSL
     */
    fun staticCompleter(dsl: HashSet<Any>.() -> Unit) {
        staticValues.addAll(hashSetOf<Any>().also { dsl(it) }.map { it.toString() })
    }

    /**
     * Provide a dynamic list of values displayed in the tab completer (Brigadier).
     *
     * These values will be computed each time the player requests a tab complete, which usually is on every key press. As such, be careful to not
     * add heavy calculations in a dynamic provider, or it will slow down the server.
     *
     * Do not use a dynamic completer if you want to add a lot of values (such as any integer), use [Argument#filter] instead.
     *
     * You can add multiple dynamic completers in a single argument.
     *
     * @param dsl Completer DSL
     */
    fun completer(dsl: HashSet<Any>.(Context) -> Unit) {
        providers.add(dsl)
    }

    /**
     * Set an additional filter to define allowed values when the command is run.
     *
     * These filters will not add possible values in tab completions, but will filter values and allow them when the command is run.
     *
     * You can add multiple filters in a single argument.
     *
     * @param dsl Filtering function (DSL)
     */
    fun filter(dsl: (String) -> Boolean) {
        filters.add(dsl)
    }
}
