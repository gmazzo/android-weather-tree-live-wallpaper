package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.annotation.FloatRange
import io.github.gmazzo.android.livewallpaper.weather.OpenGLScoped
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.minutesSinceMidnight
import kotlinx.coroutines.flow.MutableStateFlow
import org.shredzone.commons.suncalc.SunPosition
import org.shredzone.commons.suncalc.SunTimes
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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
        val now = time.time.value
        val minutes = now.minutesSinceMidnight
        val location = state.value.location

        computeSunProgress(now, minutes, location)
        moonPosition = -sunPosition

        computeAmbientColors(now, minutes, location)
    }

    private fun computeSunProgress(
        now: ZonedDateTime,
        minutes: Duration,
        location: WeatherState.Location?,
    ) {
        sunPosition = (if (location != null) {
            SunPosition.compute()
                .on(now)
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

    private fun computeAmbientColors(
        now: ZonedDateTime,
        minutes: Duration,
        location: WeatherState.Location?,
    ) {
        var sunrise: Duration? = defaultSunrise
        var midday: Duration? = defaultMidday
        var sunset: Duration? = defaultSunset
        var midnight: Duration? = defaultMidnight

        if (location != null) {
            val times = SunTimes.compute()
                .on(now)
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
            .flatMapIndexed { i, (time, color) ->
                sequenceOf(
                    Triple(i, time, color),
                    Triple(i, time - 1.days, color)
                )
            }
            .minBy { (_, time) -> if (time <= minutes) minutes - time else Duration.INFINITE }
        val (blendTime, blendColor) = colors[(mainIndex + 1) % colors.size]
        val range =
            if (mainTime < blendTime) blendTime - mainTime else 1.days - mainTime + blendTime
        val amount = (minutes - mainTime) / range

        check(amount in 0f..1f) { "Invalid blend amount: $amount" }

        tintMainColor = mainColor
        tintBlendColor = blendColor
        tintBlendAmount = amount.toFloat()
    }

    private enum class Event(
        accessor: (TimeOfDayColors) -> Int,
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
