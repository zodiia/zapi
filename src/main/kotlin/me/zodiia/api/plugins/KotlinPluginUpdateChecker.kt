package me.zodiia.api.plugins

import me.zodiia.api.logger.Console

class KotlinPluginUpdateChecker(
    private val plugin: KotlinPlugin,
): Runnable {
    override fun run() {
        Console.log("Checking for updates...")
        // TODO: Update check
        if (plugin.updates == null) {
            Console.log("No new update is available.")
        } else {
            Console.log("An update is available: ${plugin.updates}")
        }
    }
}
