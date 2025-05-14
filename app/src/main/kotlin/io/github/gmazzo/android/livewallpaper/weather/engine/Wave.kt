package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class Wave(
    val base: Double,
    val amplitude: Double,
    val phase: Double,
    val frequency: Double,
    var timeElapsed: Duration = Duration.ZERO,
) {

    val angle
        get() = (timeElapsed / 1.seconds * frequency) + phase

    val cos: Double
        get() = base + (amplitude * cos(angle))

    val sin: Double
        get() = base + (amplitude * sin(angle))

}
