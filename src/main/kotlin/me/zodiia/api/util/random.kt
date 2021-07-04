package me.zodiia.api.util

import java.util.Random

private const val DEFAULT_RANGE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"

fun Random.nextChance(chance: Double): Boolean = this.nextDouble() < chance
fun Random.nextBetween(min: Double, max: Double): Double = min + (nextDouble() * (max - min))
fun Random.nextBetween(min: Float, max: Float): Float = min + (nextFloat() * (max - min))
fun Random.nextBetween(min: Int, max: Int): Int = min + (nextInt(max - min))

fun Random.nextString(length: Int = 7, range: String = DEFAULT_RANGE): String {
    val symbols = range.toCharArray()

    return "${CharArray(length, init = { symbols[this.nextInt(symbols.size)] })}"
}
