package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration

data class Wave(
    val base: Double,
    val amplitude: Double,
    val phase: Double,
    val frequency: Double,
    var timeElapsed: Duration = Duration.ZERO,
) {

    val cos: Double
        get() = base + (amplitude * cos((timeElapsed.inWholeSeconds * frequency) + phase))

    val sin: Double
        get() = base + (amplitude * sin((timeElapsed.inWholeSeconds * frequency) + phase))

}
