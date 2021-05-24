package me.zodiia.api.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.EntityType
import kotlin.math.abs

private fun dist(a: Double, b: Double): Double {
    return if (a > b) {
        a - b
    } else {
        b - a
    }
}

fun World.removeEntities(type: EntityType): Int {
    return removeEntities(setOf(type))
}

fun World.removeEntities(types: Collection<EntityType>): Int {
    var total = 0

    entities.forEach {
        if (types.contains(it.type)) {
            it.remove()
            total++
        }
    }
    return total
}

fun World.removeEntities(type: EntityType, from: Location, to: Location) {
    return removeEntities(setOf(type), from, to)
}

fun World.removeEntities(types: Collection<EntityType>, from: Location, to: Location) {
    var total = 0
    val center = Location(
        this,
        (from.x + to.x) / 2.0,
        (from.y + to.y) / 2.0,
        (from.z + to.z) / 2.0,
    )
    val xRange = dist(from.x, to.x) / 2.0
    val yRange = dist(from.y, to.y) / 2.0
    val zRange = dist(from.z, to.z) / 2.0

    getNearbyEntities(center, xRange, yRange, zRange).forEach {
        if (types.contains(it.type)) {
            it.remove()
            total++
        }
    }
}
