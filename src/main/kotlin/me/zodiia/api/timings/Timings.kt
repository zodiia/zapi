package me.zodiia.api.timings

object Timings {
    // Temporary
    private val wrapper: TimingsWrapper = SpigotTimingsWrapper()

    fun capture() = wrapper.capture()
    fun start() = wrapper.start()
    fun stop() = wrapper.stop()
    fun isEnabled() = wrapper.isEnabled()
}
