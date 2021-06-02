package me.zodiia.api

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import org.junit.jupiter.api.*

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class ApiPluginTest {
//    lateinit var server: ServerMock
//    lateinit var plugin: ApiPlugin
//
//    @BeforeAll
//    fun setup() {
//        server = MockBukkit.mock()
//        plugin = MockBukkit.load(ApiPlugin::class.java)
//    }
//
//    @AfterAll
//    fun teardown() {
//        MockBukkit.unmock()
//    }
//
//    @Test
//    fun `Should return the plugin`() {
//        Assertions.assertEquals(plugin, ApiPlugin.plugin)
//    }
//}
