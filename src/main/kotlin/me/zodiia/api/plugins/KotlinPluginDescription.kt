package me.zodiia.api.plugins

data class KotlinPluginDescription(
    val minecraftVersion: String,
    val pluginDependencies: Map<String, String> = emptyMap(),
    val softPluginDependencies: Map<String, String> = emptyMap(),
    val files: Array<String> = emptyArray(),
    val spigotId: Int = -1,
    val urls: Urls = Urls(),
) {
    data class Urls(
        val bugs: String? = null,
        val docs: String? = null,
        val homepage: String? = null,
        val repository: String? = null,
    )

    class Builder {
        private var minecraftVersion: String? = null
        private var pluginDependencies: MutableMap<String, String> = mutableMapOf()
        private var softPluginDependencies: MutableMap<String, String> = mutableMapOf()
        private var files: MutableList<String> = mutableListOf()
        private var spigotId: Int = -1
        private var urls: MutableMap<String, String> = mutableMapOf()

        fun minecraftVersion(version: String) {
            minecraftVersion = version
        }

        fun pluginDependency(plugin: String, version: String) {
            pluginDependencies[plugin] = version
        }

        fun softPluginDependency(plugin: String, version: String) {
            softPluginDependencies[plugin] = version
        }

        fun file(path: String) {
            files.add(path)
        }

        fun spigotId(id: Int) {
            spigotId = id
        }

        fun homepageUrl(url: String) {
            urls["homepage"] = url
        }

        fun docsUrl(url: String) {
            urls["docs"] = url
        }

        fun bugsUrl(url: String) {
            urls["bugs"] = url
        }

        fun repository(url: String) {
            urls["repository"] = url
        }

        internal fun build(): KotlinPluginDescription {
            if (minecraftVersion == null) {
                throw IllegalStateException("Minecraft version is missing from the Kotlin plugin description block.")
            }

            val urls = Urls(
                homepage = urls["homepage"],
                docs = urls["docs"],
                bugs = urls["bugs"],
                repository = urls["repository"],
            )

            return KotlinPluginDescription(
                minecraftVersion!!,
                pluginDependencies,
                softPluginDependencies,
                files.toTypedArray(),
                spigotId,
                urls,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KotlinPluginDescription

        if (minecraftVersion != other.minecraftVersion) return false
        if (pluginDependencies != other.pluginDependencies) return false
        if (softPluginDependencies != other.softPluginDependencies) return false
        if (!files.contentEquals(other.files)) return false
        if (spigotId != other.spigotId) return false
        if (urls != other.urls) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minecraftVersion.hashCode()
        result = 31 * result + pluginDependencies.hashCode()
        result = 31 * result + softPluginDependencies.hashCode()
        result = 31 * result + files.contentHashCode()
        result = 31 * result + spigotId
        result = 31 * result + urls.hashCode()
        return result
    }
}
