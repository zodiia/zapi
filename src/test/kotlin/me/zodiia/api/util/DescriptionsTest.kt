package me.zodiia.api.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DescriptionsTest {
    @Test
    fun `Should correctly transform numbers into roman numbers`() {
        Assertions.assertEquals("I", 1.toRomanString())
        Assertions.assertEquals("", 0.toRomanString())
        Assertions.assertEquals("III", 3.toRomanString())
        Assertions.assertEquals("MCMLXXIX", 1979.toRomanString())
    }
}