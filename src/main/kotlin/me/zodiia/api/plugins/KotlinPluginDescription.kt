package me.zodiia.api.plugins

data class KotlinPluginDescription(
    val minecraftVersion: String,
    val pluginDependencies: Map<String, String>? = null,
    val softPluginDependencies: Map<String, String>? = null,
    val files: Array<String>? = null,
    val spigotId: Int = -1,
    val urls: Urls? = null,
) {
    data class Urls(
        val bugs: String? = null,
        val docs: String? = null,
        val homepage: String? = null,
        val repository: String? = null,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KotlinPluginDescription

        if (minecraftVersion != other.minecraftVersion) return false
        if (pluginDependencies != other.pluginDependencies) return false
        if (softPluginDependencies != other.softPluginDependencies) return false
        if (files != null) {
            if (other.files == null) return false
            if (!files.contentEquals(other.files)) return false
        } else if (other.files != null) return false
        if (spigotId != other.spigotId) return false
        if (urls != other.urls) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minecraftVersion.hashCode()
        result = 31 * result + (pluginDependencies?.hashCode() ?: 0)
        result = 31 * result + (softPluginDependencies?.hashCode() ?: 0)
        result = 31 * result + (files?.contentHashCode() ?: 0)
        result = 31 * result + spigotId
        result = 31 * result + (urls?.hashCode() ?: 0)
        return result
    }
}
