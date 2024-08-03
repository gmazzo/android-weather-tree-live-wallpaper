package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.annotation.FloatRange
import io.github.gmazzo.android.livewallpaper.weather.OpenGLScoped
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTintMode.Blend
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTintMode.Day
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTintMode.Night
import io.github.gmazzo.android.livewallpaper.weather.minutesSinceMidnight
import kotlinx.coroutines.flow.MutableStateFlow
import org.shredzone.commons.suncalc.MoonPosition
import org.shredzone.commons.suncalc.SunPosition
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.hours

@OpenGLScoped
class TimeOfDay @Inject constructor(
    @Named("scaled") private val time: GlobalTime,
    private val state: MutableStateFlow<WeatherState>,
) {

    @FloatRange(from = 0.0, to = 1.0)
    var sunPosition: Float = 0f

    @FloatRange(from = 0.0, to = 1.0)
    var moonPosition: Float = 0f

    lateinit var tintMode: TimeOfDayTintMode

    fun update() {
        val now = time.time.value
        val location = state.value.location

        sunPosition = computeSunProgress(now, location)
        moonPosition = computeMoonPosition(now, location)
        tintMode = computeAmbientColors()
    }

    private fun computeSunProgress(
        now: ZonedDateTime,
        location: WeatherState.Location?,
    ) = if (location != null) {
        SunPosition.compute()
            .on(now)
            .at(location.latitude, location.longitude)
            .execute()
            .altitude.toFloat() / 90f

    } else {
        val minutes = now.minutesSinceMidnight

        // TODO can it be simplified?
        when {
            minutes >= 12.hours -> 1 - (minutes - 12.hours) / 6.hours
            else -> (minutes - 6.hours) / 6.hours
        }.toFloat()
    }

    private fun computeMoonPosition(
        now: ZonedDateTime,
        location: WeatherState.Location?,
    ) = if (location != null && false) { // FIXME doesn't look well
        MoonPosition.compute()
            .on(now)
            .at(location.latitude, location.longitude)
            .execute()
            .altitude.toFloat() / 90f

    } else {
        -sunPosition
    }

    private fun computeAmbientColors() = when {
        sunPosition > GOLDER_HOUR_FACTOR -> Day
        sunPosition < -GOLDER_HOUR_FACTOR -> Night
        sunPosition >= 0 -> Blend(TimeOfDayColors::dawn, Day, sunPosition / GOLDER_HOUR_FACTOR)
        else -> Blend(TimeOfDayColors::dawn, Night, sunPosition / -GOLDER_HOUR_FACTOR)
    }

    companion object {
        const val GOLDER_HOUR_FACTOR = .2f
    }

}
