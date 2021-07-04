package me.zodiia.api.command

class Argument(dsl: Argument.() -> Unit) {
    private val filters = hashSetOf<(String) -> Boolean>()
    private val providers = hashSetOf<HashSet<Any>.(Context) -> Unit>()
    private val staticValues = hashSetOf<String>()
    var long: Boolean = false
    var required: Boolean = true
    var permission: String? = null

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
        if (get(context).contains(value)) {
            return true
        }
        if (filters.find { it(value) } != null) {
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
     * Provides a list of static values displayed in the tab completer (Brigadier)
     */
    fun staticCompleter(dsl: HashSet<Any>.() -> Unit) {
        staticValues.addAll(hashSetOf<Any>().also { dsl(it) }.map { it.toString() })
    }

    /**
     * Provides a dynamic list of values displayed in the tab completer (Brigadier)
     */
    fun completer(dsl: HashSet<Any>.(Context) -> Unit) {
        providers.add(dsl)
    }

    /**
     * Additional filters to define allowed values
     */
    fun filter(dsl: (String) -> Boolean) {
        filters.add(dsl)
    }
}
