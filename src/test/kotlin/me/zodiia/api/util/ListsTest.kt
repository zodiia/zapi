package me.zodiia.api.util

import org.bukkit.entity.EntityType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ListsTest {
    companion object {
        val oddNumbers = listOf(1, 3, 5, 7, 9)
        val evenNumbers = listOf(2, 4, 6, 8, 10)
    }

    @Test
    fun `Should correctly validate against a whitelist`() {
        Assertions.assertTrue(1.validateWhitelist(oddNumbers))
        Assertions.assertFalse(5.validateWhitelist(evenNumbers))
        Assertions.assertTrue(6.validateBlacklist(oddNumbers))
        Assertions.assertFalse(8.validateBlacklist(evenNumbers))
    }

    @Test
    fun `Should convert the string into an enum list`() {
        val expectedList = listOf(EntityType.CREEPER, EntityType.BAT, EntityType.COW)
        val stringList = "CREEPER,BAT,COW"
        val actualList = stringList.toEnumList<EntityType>()

        Assertions.assertEquals(expectedList.size, actualList.size)
        for (i in actualList.indices) {
            Assertions.assertEquals(expectedList[i], actualList[i])
        }
    }
}
