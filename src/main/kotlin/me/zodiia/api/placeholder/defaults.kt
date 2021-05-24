package me.zodiia.api.placeholder

fun placeholders(name: String, dsl: Placeholder.() -> Unit): Placeholder = Placeholder(name, dsl)