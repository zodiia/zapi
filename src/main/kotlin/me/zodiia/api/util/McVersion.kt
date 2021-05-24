package me.zodiia.api.util

import org.bukkit.Bukkit

enum class McVersion(val identifier: String, val isSupported: Boolean) {
    MC1_7("1.7", false),
    MC1_8("1.8", false),
    MC1_9("1.9", false),
    MC1_10("1.10", false),
    MC1_11("1.11", false),
    MC1_12("1.12", false),
    MC1_13("1.13", false),
    MC1_14("1.14", false),
    MC1_15("1.15", false),
    MC1_16("1.16", true),
    MC1_17("1.17", true),
    OTHER("unknown", false),
    ;

    companion object {
        fun current(): McVersion {
            McVersion.values().forEach {
                if (Bukkit.getVersion().contains(it.identifier)) {
                    return it
                }
            }
            return OTHER
        }
    }
}
