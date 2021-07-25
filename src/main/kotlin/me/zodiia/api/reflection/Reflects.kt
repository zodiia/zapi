package me.zodiia.api.reflection

import me.zodiia.api.logger.Console
import org.bukkit.Bukkit
import java.lang.reflect.Field
import java.lang.reflect.Method

object Reflects {
    fun getVersion(): String {
        val name = Bukkit.getServer().javaClass.getPackage().name

        return name.substring(name.lastIndexOf('.') + 1)
    }

    fun <T: Any> getClass(className: String): Class<T>? {
        var clazz: Class<T>? = null

        try {
            @Suppress("UNCHECKED_CAST")
            clazz = (Class.forName(className) as Class<T>)
        } catch (err: ClassNotFoundException) {
            Console.error(err)
        }
        return clazz
    }

    fun <T: Any> getNMSClass(className: String): Class<T>? = getClass("net.minecraft.server.${getVersion()}.${className}")

    fun <T: Any> getOBCClass(className: String): Class<T>? = getClass("org.bukkit.craftbukkit.${getVersion()}.${className}")

    fun getHandle(obj: Any): Any {
        return getMethod(obj.javaClass, "getHandle").invoke(obj)
    }

    fun getMethod(clazz: Class<*>, name: String, vararg arguments: Class<*>): Method {
        val method = clazz.getMethod(name, *arguments)

        method.isAccessible = true
        return method
    }

    fun getField(clazz: Class<*>, name: String): Field {
        val field = clazz.getDeclaredField(name)

        field.isAccessible = true
        return field
    }
}
