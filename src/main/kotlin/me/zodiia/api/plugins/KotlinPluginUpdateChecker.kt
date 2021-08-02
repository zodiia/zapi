package me.zodiia.api.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.vdurmont.semver4j.Semver
import me.zodiia.api.logger.Console
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.netty.http.client.HttpClient
import java.lang.reflect.Type
import java.util.*

class KotlinPluginUpdateChecker(
    private val plugin: KotlinPlugin,
): Runnable {
    companion object {
        const val API_URL = "https://api.spigotmc.org/simple/0.2/index.php"
        const val GET_RESOURCE_ACTION = "getResource"
    }

    override fun run() {
        if (plugin.getKotlinDescription().spigotId <= 0) {
            Console.debug("Spigot ID is not set. Skipping update checking.")
            return
        }

        Console.log("Checking for updates...")

        HttpClient
            .create()
            .baseUrl("${API_URL}?action=${GET_RESOURCE_ACTION}&id=${plugin.getKotlinDescription().spigotId}#")
            .get()
            .responseContent()
            .aggregate()
            .flatMap {
                val str = StringBuilder()

                it.forEachByte { b ->
                    str.append(Char(b.toInt()))
                    true
                }
                Mono.just(str.toString())
            }
            .map {
                val json = JsonParser().parse(it).asJsonObject
                val lastVersion = json["current_version"].asString
                val semver = Semver(lastVersion, Semver.SemverType.NPM)

                if (semver.isGreaterThan(plugin.description.version)) {
                    plugin.updates = lastVersion
                }
                if (plugin.updates == null) {
                    Console.log("No new update is available.")
                } else {
                    Console.log("An update is available: ${plugin.updates}")
                }
            }
            .subscribe()
    }
}
