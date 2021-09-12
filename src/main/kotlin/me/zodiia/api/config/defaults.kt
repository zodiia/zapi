package me.zodiia.api.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.RequiredConfigProperty
import com.uchuhimo.konf.cast

fun <T> Config.toDataClass(): T {
    val cast by object : RequiredConfigProperty<T>(this.withPrefix("root").withLayer(), name = "root") {}
    return cast
}