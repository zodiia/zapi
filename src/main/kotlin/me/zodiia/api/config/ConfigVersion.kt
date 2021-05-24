package me.zodiia.api.config

data class ConfigVersion(
    private val versionString: String,
) {
    operator fun compareTo(other: ConfigVersion): Int {
        val thisParts = versionString.split("\\.")
        val otherParts = other.versionString.split("\\.")
        val length = thisParts.size.coerceAtLeast(otherParts.size)

        for (i in 0 until length) {
            if (thisParts.size <= i) {
                return -1
            } else if (otherParts.size <= i) {
                return 1
            }

            val compared = thisParts[i].compareTo(otherParts[i], true)

            if (compared != 0) {
                return compared
            }
        }
        return 0
    }
}
