package me.zodiia.api.config.configurables

import me.zodiia.api.config.ConfigSection
import me.zodiia.api.config.Configurable
import org.bukkit.Bukkit
import org.bukkit.Location

data class ConfigurableLocation(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Double? = null,
    var pitch: Double? = null,
    var world: String? = null,
): Configurable {
    override fun fromConfig(cfg: ConfigSection) {
        x = cfg["x"]!!.toValue(Double::class.java)
        y = cfg["y"]!!.toValue(Double::class.java)
        z = cfg["z"]!!.toValue(Double::class.java)
        yaw = cfg["yaw"]?.toValue(Double::class.java) ?: yaw
        pitch = cfg["pitch"]?.toValue(Double::class.java) ?: pitch
        world = cfg["world"]?.toValue(String::class.java) ?: world
    }

    override fun toConfig(cfg: ConfigSection) {
        cfg["x"] = x
        cfg["y"] = y
        cfg["z"] = z
        yaw?.let { cfg["yaw"] = it }
        pitch?.let { cfg["pitch"] = it }
        world?.let { cfg["world"] = it }
    }

    fun fromLocation(location: Location) {
        x = location.x
        y = location.y
        z = location.z
        yaw = location.yaw.toDouble()
        pitch = location.pitch.toDouble()
        world = location.world?.name
    }

    fun toLocation(): Location {
        val loc = Location(world?.let { Bukkit.getWorld(it) }, x, y, z)

        pitch?.let { loc.pitch = it.toFloat() }
        yaw?.let { loc.yaw = it.toFloat() }
        return loc
    }


    companion object {
        fun fromLocation(location: Location): ConfigurableLocation {
            return ConfigurableLocation().also { it.fromLocation(location) }
        }
    }
}
