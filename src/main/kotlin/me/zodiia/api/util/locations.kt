package me.zodiia.api.util

import org.bukkit.Location

fun Location.compare(other: Location, orientation: Boolean = false): Boolean {
    if (blockX != other.blockX || blockY != other.blockY || blockZ != other.blockZ) {
        return false
    }
    if (!world?.name.equals(other.world?.name)) {
        return false
    }
    if (orientation && pitch != other.pitch && yaw != other.yaw) {
        return false
    }
    return true
}
