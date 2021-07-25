package me.zodiia.api.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FunctionsTest {
    @Test
    fun `Should run the callback if the object is not null`() {
        var obj: Int? = null

        obj.notNull { Assertions.fail() }
        notNull(obj) { Assertions.fail() }
        obj = 1
        obj.notNull { Assertions.assertEquals(1, it) }
        notNull(obj) { Assertions.assertEquals(1, it) }
    }

    @Test
    fun `Should return the thrown exception if it exists`() {
        Assertions.assertDoesNotThrow { tryFct { throw Exception() } }
        Assertions.assertNotNull(tryFct { throw Exception() })
        Assertions.assertNull(tryFct { 1 + 1 })
    }

    @Test
    fun `Should correctly return the correct value in the ternary operation`() {
        Assertions.assertEquals(1, ternary(true, 1, 2))
        Assertions.assertEquals(2, ternary(false, 1, 2))
        Assertions.assertEquals(1, ternary({ true }, 1, 2))
        Assertions.assertEquals(2, ternary({ false }, 1, 2))
    }
}
