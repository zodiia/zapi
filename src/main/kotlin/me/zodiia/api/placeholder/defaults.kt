package me.zodiia.api.placeholder

fun placeholder(name: String, dsl: Placeholder.() -> Unit): Placeholder = Placeholder(name, dsl)
