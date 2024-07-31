package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.annotation.FloatRange
import io.github.gmazzo.android.livewallpaper.weather.OpenGLScoped
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import kotlinx.coroutines.flow.MutableStateFlow
import org.shredzone.commons.suncalc.SunPosition
import org.shredzone.commons.suncalc.SunTimes
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OpenGLScoped
class TimeOfDay @Inject constructor(
    @Named("scaled") private val time: GlobalTime,
    private val state: MutableStateFlow<WeatherState>,
) {

    @FloatRange(from = 0.0, to = 1.0)
    var sunPosition: Float = 0f

    lateinit var tintMainColor: (TimeOfDayColors) -> Int

    lateinit var tintBlendColor: (TimeOfDayColors) -> Int

    var tintBlendAmount = 0f

    @FloatRange(from = 0.0, to = 1.0)
    var moonPosition: Float = 0f

    fun update() {
        val minutes = time.now.minutesSinceMidnight
        val location = state.value.location

        computeSunProgress(minutes, location)
        moonPosition = -sunPosition
        computeAmbientColors(minutes, location)
    }

    private fun computeSunProgress(minutes: Duration, location: WeatherState.Location?) {
        sunPosition = (if (location != null) {
            SunPosition.compute()
                .on(time.now)
                .at(location.latitude, location.longitude)
                .execute()
                .altitude / 90f

        } else {
            when {
                minutes >= defaultMidday -> 1 - (minutes - defaultMidday) / (defaultSunset - defaultMidday)
                else -> (minutes - defaultSunrise) / (defaultMidday - defaultSunrise)
            }
        }).toFloat()
    }

    private fun computeAmbientColors(minutes: Duration, location: WeatherState.Location?) {
        var sunrise: Duration? = defaultSunrise
        var midday: Duration? = defaultMidday
        var sunset: Duration? = defaultSunset
        var midnight: Duration? = defaultMidnight

        if (location != null && false) { // FIXME not working properly
            val times = SunTimes.compute()
                .on(time.now)
                .at(location.latitude, location.longitude)
                .execute()

            sunrise = times.rise?.minutesSinceMidnight
            midday = times.noon?.minutesSinceMidnight
            sunset = times.set?.minutesSinceMidnight
            midnight = times.nadir?.minutesSinceMidnight
        }

        val colors = listOfNotNull(
            sunrise?.to(Event.Sunrise),
            midday?.to(Event.Midday),
            sunset?.to(Event.Sunset),
            midnight?.to(Event.Midnight),
        )

        val (mainIndex, mainTime, mainColor) = colors.asSequence()
            .mapIndexed { i, (time, color) -> Triple(i, time, color) }
            .minBy { (_, time) -> if (time <= minutes) minutes - time else Duration.INFINITE }
        val (blendTime, blendColor) = colors[(mainIndex + 1) % colors.size]
        val range = if (mainTime < blendTime) blendTime - mainTime else 1.days - mainTime + blendTime
        val amount = (minutes - mainTime) / range

        tintMainColor = mainColor
        tintBlendColor = blendColor
        tintBlendAmount = amount.toFloat()
    }

    private val ZonedDateTime.minutesSinceMidnight
        get() = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, this).minutes

    private enum class Event(
        accessor: (TimeOfDayColors) -> Int
    ) : (TimeOfDayColors) -> Int by accessor {
        Sunrise(TimeOfDayColors::dawn),
        Sunset(TimeOfDayColors::dusk),
        Midday(TimeOfDayColors::day),
        Midnight(TimeOfDayColors::night),
    }

    companion object {
        private val defaultSunrise = 6.hours
        private val defaultSunset = 18.hours
        private val defaultMidday = 12.hours
        private val defaultMidnight = 0.hours
    }

}
