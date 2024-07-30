package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.math.cos
import kotlin.math.sin

data class Wave(
    val base: Double,
    val amplitude: Double,
    val phase: Double,
    val frequency: Double,
    var timeElapsed: Long = 0,
) {

    val cos get() = cos(timeElapsed)

    val sin get() = sin(timeElapsed)

    fun cos(time: Long) =
        base + (amplitude * cos((time * frequency) + phase))

    fun sin(time: Long = timeElapsed) =
        base + (amplitude * sin((time * frequency) + phase))

}
