package me.zodiia.tests

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.fail

object AssertionsExtensions {
    fun assertIterableEqualsAnyOrder(expected: Iterable<*>, actual: Iterable<*>) {
        Assertions.assertEquals(expected.count(), actual.count(),
                "Expected iterable of size <${expected.count()}>, got <${actual.count()}>.")
        assertIterableContains(expected, actual)
    }

    fun assertIterableContains(expected: Iterable<*>, actual: Iterable<*>) {
        expected.forEach {
            if (!actual.contains(it)) {
                fail("Expected iterable to contain <$it>, but it wasn't present.")
            }
        }
    }
}
