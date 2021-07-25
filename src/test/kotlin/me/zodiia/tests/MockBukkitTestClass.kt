package me.zodiia.tests

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import me.zodiia.api.ApiPlugin
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class MockBukkitTestClass {
    lateinit var server: ServerMock
    lateinit var plugin: ApiPlugin

    @BeforeAll
    fun setup() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(ApiPlugin::class.java)
        ApiPlugin.env = ApiPlugin.Env.TEST
    }

    @AfterAll
    fun teardown() {
        MockBukkit.unmock()
    }
}
