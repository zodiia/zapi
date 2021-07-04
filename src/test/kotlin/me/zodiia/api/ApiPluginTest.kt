package me.zodiia.api

import me.zodiia.tests.MockBukkitTestClass
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ApiPluginTest: MockBukkitTestClass() {
    @Test
    fun `Should return the plugin`() {
        Assertions.assertEquals(plugin, ApiPlugin.plugin)
    }
}
