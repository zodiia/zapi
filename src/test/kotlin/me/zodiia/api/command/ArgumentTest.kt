package me.zodiia.api.command

import me.zodiia.tests.MockBukkitTestClass
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom

class ArgumentTest: MockBukkitTestClass() {
    companion object {
        val dummyCommand = Command {}
        val staticArgument = Argument {
            staticCompleter { addAll(0 until 10) }
        }
        val dynamicArgument = Argument {
            completer { addAll(dynamicValues) }
        }
        var dynamicValues: IntRange = 0 until 10
        val filteringArgument = Argument {
            filter { it.toIntOrNull() != null }
        }
        val optionalArgument = Argument {
            required = false
        }
        val longArgument = Argument {
            long = true
        }
        val permissibleArgument = Argument {
            permission = "api.test"
            staticCompleter { add(0) }
        }
    }

    @Test
    fun `Should correctly parse arguments into the correct value`() {
        val args = arrayOf("arg0", "arg1", "arg2")

        Assertions.assertNull(staticArgument.value(-1, args))
        Assertions.assertNull(staticArgument.value(3, args))
        Assertions.assertEquals("arg1", staticArgument.value(1, args))
        Assertions.assertEquals("arg2", longArgument.value(2, args))
        Assertions.assertEquals("arg0 arg1 arg2", longArgument.value(0, args))
    }

    @Test
    fun `Should correctly accept values`() {
        val sender = server.addPlayer("Player")
        val context = Context(
            command = dummyCommand,
            label = "random",
            args = listOf("arg0", "arg1", "arg2"),
            sender = sender,
            player = sender,
            instant = Instant.now(),
        )

        Assertions.assertTrue(staticArgument.test(context, "3"))
        Assertions.assertFalse(staticArgument.test(context, "12"))
        Assertions.assertFalse(staticArgument.test(context, "Hello"))
        Assertions.assertFalse(staticArgument.test(context, null))
        Assertions.assertTrue(dynamicArgument.test(context, "3"))
        Assertions.assertFalse(staticArgument.test(context, "12"))
        dynamicValues = 10 until 20
        Assertions.assertFalse(dynamicArgument.test(context, "3"))
        Assertions.assertTrue(dynamicArgument.test(context, "12"))
        repeat(50) { Assertions.assertTrue(filteringArgument.test(context, ThreadLocalRandom.current().nextInt().toString())) }
        Assertions.assertTrue(optionalArgument.test(context, null))
        Assertions.assertFalse(permissibleArgument.test(context, "0"))
        sender.addAttachment(plugin, "api.test", true)
        Assertions.assertTrue(permissibleArgument.test(context, "0"))
    }
}
