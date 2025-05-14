package io.github.gmazzo.android.livewallpaper.weather.engine.time

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Singleton
class TimeManager @Inject constructor(
    source: TimeSource,
    @Named("real") private val clock: MutableStateFlow<Clock>,
    @Named("fast") private val fastClock: MutableStateFlow<Clock>,
    @Named("fastTimeSpeed") private val speed: MutableStateFlow<Double>,
) : TimeSource by source {

    fun update() {
        val delta = ChronoUnit.MILLIS.between(clock.value.time, now()).milliseconds
        val scaledDelta = delta * speed.value

        clock.applyDelta(delta)
        fastClock.applyDelta(scaledDelta)
    }

    private fun MutableStateFlow<Clock>.applyDelta(delta: Duration) = update {
        Clock(
            time = it.time.plus(delta.toJavaDuration()),
            delta = delta,
            elapsed = it.elapsed + delta
        )
    }

}
