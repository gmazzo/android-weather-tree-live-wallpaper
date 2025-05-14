package io.github.gmazzo.android.livewallpaper.weather.engine.time

import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class Clock(
    val time: ZonedDateTime,
    val delta: Duration = Duration.Companion.ZERO,
    val elapsed: Duration = Duration.Companion.ZERO,
) {
    val deltaSeconds = (delta / 1.seconds).toFloat()
    val elapsedSeconds = (elapsed / 1.seconds).toFloat()
}
