package me.zodiia.api.command

import me.zodiia.api.util.send
import me.zodiia.tests.AssertionsExtensions
import me.zodiia.tests.MockBukkitTestClass
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommandTest: MockBukkitTestClass() {
    companion object {
        const val TEST_PERMISSION = "api.test"
        const val TEST_PERMISSION_2 = "api.test2"
        val helloWorldCommand = command {
            executor { it.sender.send("Hello world!") }
        }
        val choiceCommand = command {
            subcommand("a") {
                aliases = setOf("notb")
                executor { it.sender.send("Choose A") }
            }
            subcommand("b") {
                executor { it.sender.send("Choose B") }
            }
            executor { it.sender.send("No choice") }
            internalErrorExecutor { _, th -> th.printStackTrace() }
        }
        val argumentCommand = command {
            argument(0) {
                staticCompleter { addAll(0 until 2) }
                completer { addAll(2 until 4) }
                filter { it.toIntOrNull() != null }
            }
            executor { it.sender.send("Argument is ${it.args[0]}") }
            syntaxExecutor { it, _ -> it.sender.send("Syntax error") }
        }
        val permissionCommand = command {
            permission = TEST_PERMISSION
            subcommand("thing") {
                permission = TEST_PERMISSION_2
            }
            executor { it.sender.send("Permission OK") }
            permissionExecutor { it.sender.send("Permission KO") }
        }
        val errorCommand = command {
            executor { throw IllegalStateException("Some error message") }
            argument(0) {
                required = false
                completer { throw IllegalStateException() }
            }
            internalErrorExecutor { it, th -> it.sender.send("Error: ${th.message}") }
        }
        val subCommandArgsCommand = command {
            subcommand("sub") {
                argument(0) {
                    staticCompleter { addAll(listOf("a", "b", "c")) }
                }
            }
        }
        val emptyCommand = command {}
        val defaultExecutorsCommand = command {
            permission = TEST_PERMISSION
            argument(0) { filter { it.toIntOrNull() != null } }
            executor { throw IllegalStateException() }
        }
    }

    @Test
    fun `Should correctly register the command`() {
        Commands.register("hello", plugin, helloWorldCommand)
        Assertions.assertNotNull(server.commandMap.getCommand("hello"))
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly execute the command`() {
        val player = server.addPlayer()

        Commands.register("hello", plugin, helloWorldCommand)
        player.performCommand("hello")
        Assertions.assertEquals("Hello world!", player.nextMessage())
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly handle subcommands`() {
        val player = server.addPlayer()

        Commands.register("choice", plugin, choiceCommand)
        player.performCommand("choice a")
        player.performCommand("choice b")
        player.performCommand("choice notb")
        player.performCommand("choice")
        Assertions.assertEquals("Choose A", player.nextMessage())
        Assertions.assertEquals("Choose B", player.nextMessage())
        Assertions.assertEquals("Choose A", player.nextMessage())
        Assertions.assertEquals("No choice", player.nextMessage())
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly handle arguments`() {
        val player = server.addPlayer()

        Commands.register("argument", plugin, argumentCommand)
        player.performCommand("argument 42")
        player.performCommand("argument 381")
        Assertions.assertEquals("Argument is 42", player.nextMessage())
        Assertions.assertEquals("Argument is 381", player.nextMessage())
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should handle invalid syntaxes`() {
        val player = server.addPlayer()

        Commands.register("argument", plugin, argumentCommand)
        player.performCommand("argument")
        player.performCommand("argument 1 2")
        player.performCommand("argument fourtytwo")
        Assertions.assertEquals("Syntax error", player.nextMessage())
        Assertions.assertEquals("Syntax error", player.nextMessage())
        Assertions.assertEquals("Syntax error", player.nextMessage())
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should handle permission errors`() {
        val player = server.addPlayer()

        Commands.register("permission", plugin, permissionCommand)
        player.performCommand("permission")
        player.addAttachment(plugin, TEST_PERMISSION, true)
        player.performCommand("permission")
        Assertions.assertEquals("Permission KO", player.nextMessage())
        Assertions.assertEquals("Permission OK", player.nextMessage())
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should handle exceptions`() {
        val player = server.addPlayer()

        Commands.register("error", plugin, errorCommand)
        player.performCommand("error")
        Assertions.assertEquals("Error: Some error message", player.nextMessage())
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly tab complete static and dynamic completers`() {
        val player = server.addPlayer()
        val cmd by lazy { server.commandMap.getCommand("argument") ?: throw IllegalStateException() }

        Commands.register("argument", plugin, argumentCommand)
        AssertionsExtensions.assertIterableEqualsAnyOrder(listOf("0", "1", "2", "3"), cmd.tabComplete(player, "argument", arrayOf("")))
        Assertions.assertIterableEquals(emptyList<String>(), cmd.tabComplete(player, "argument", arrayOf("5")))
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly tab complete sub commands`() {
        val player = server.addPlayer()
        val cmd by lazy { server.commandMap.getCommand("choice") ?: throw IllegalStateException() }

        Commands.register("choice", plugin, choiceCommand)
        AssertionsExtensions.assertIterableEqualsAnyOrder(listOf("a", "notb", "b"), cmd.tabComplete(player, "choice", arrayOf("")))
        AssertionsExtensions.assertIterableEqualsAnyOrder(listOf("notb"), cmd.tabComplete(player, "choice", arrayOf("n")))
        AssertionsExtensions.assertIterableEqualsAnyOrder(listOf("b"), cmd.tabComplete(player, "choice", arrayOf("b")))
        Assertions.assertIterableEquals(emptyList<String>(), cmd.tabComplete(player, "choice", arrayOf("bb")))
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly tab complete inside subcommands`() {
        val player = server.addPlayer()
        val cmd by lazy { server.commandMap.getCommand("argsubcmd") ?: throw IllegalStateException() }

        Commands.register("argsubcmd", plugin, subCommandArgsCommand)
        AssertionsExtensions.assertIterableEqualsAnyOrder(listOf("a", "b", "c"), cmd.tabComplete(player, "argsubcmd", arrayOf("sub", "")))
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should correctly tab complete according to permissions`() {
        val player = server.addPlayer()
        val cmd by lazy { server.commandMap.getCommand("permission") ?: throw IllegalStateException() }

        Commands.register("permission", plugin, permissionCommand)
        Assertions.assertIterableEquals(emptyList<String>(), cmd.tabComplete(player, "permission", arrayOf("")))
        player.addAttachment(plugin, TEST_PERMISSION, true)
        Assertions.assertIterableEquals(emptyList<String>(), cmd.tabComplete(player, "permission", arrayOf("")))
        player.addAttachment(plugin, TEST_PERMISSION_2, true)
        AssertionsExtensions.assertIterableEqualsAnyOrder(listOf("thing"), cmd.tabComplete(player, "permission", arrayOf("")))
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should return no match in case of an error`() {
        val player = server.addPlayer()
        val cmd by lazy { server.commandMap.getCommand("error") ?: throw IllegalStateException() }

        Commands.register("error", plugin, errorCommand)
        Assertions.assertIterableEquals(emptyList<String>(), cmd.tabComplete(player, "error", arrayOf("")))
        Assertions.assertNotNull(player.nextMessage())
        server.commandMap.clearCommands()
    }

    @Test
    fun `Should have default executors implementations`() {
        val player = server.addPlayer()

        Commands.register("empty", plugin, emptyCommand)
        Commands.register("default", plugin, defaultExecutorsCommand)
        player.performCommand("empty")
        player.performCommand("default")
        player.addAttachment(plugin, TEST_PERMISSION, true)
        player.performCommand("default")
        player.performCommand("default 1")
        repeat(4) {
            Assertions.assertNotNull(player.nextMessage())
        }
        Assertions.assertNull(player.nextMessage())
        server.commandMap.clearCommands()
    }
}
