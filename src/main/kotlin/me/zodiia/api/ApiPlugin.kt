package me.zodiia.api

import me.zodiia.api.threads.Threads
import org.bukkit.plugin.java.JavaPlugin

class ApiPlugin: JavaPlugin() {
    override fun onLoad() {
        Threads.startSyncTask(this)
    }
}
