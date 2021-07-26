package me.zodiia.api.timings

interface TimingsWrapper {
    fun capture(): TimingsCapture
    fun start()
    fun stop()
    fun isEnabled(): Boolean
}
