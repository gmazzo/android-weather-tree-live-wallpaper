package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.annotation.FloatRange
import io.github.gmazzo.android.livewallpaper.weather.Location
import io.github.gmazzo.android.livewallpaper.weather.WeatherRendererScoped
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.minutesSinceMidnight
import kotlinx.coroutines.flow.MutableStateFlow
import org.shredzone.commons.suncalc.SunPosition
import org.shredzone.commons.suncalc.SunTimes
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@WeatherRendererScoped
class TimeOfDay @Inject constructor(
    @Named("scaled") private val time: GlobalTime,
    private val location: MutableStateFlow<Location?>,
) {

    @FloatRange(from = 0.0, to = 1.0)
    var sunPosition: Float = 0f

    lateinit var tintSpec: TintSpec

    @Inject
    fun update() {
        val now = time.time.value
        val minutes = now.minutesSinceMidnight
        val location = location.value

        sunPosition = computeSunProgress(now, minutes, location)
        tintSpec = computeAmbientTintColors(now, minutes, location)
    }

    private fun computeSunProgress(
        now: ZonedDateTime,
        minutes: Duration,
        location: Location?,
    ) = if (location != null) {
        SunPosition.compute()
            .on(now)
            .at(location.latitude, location.longitude)
            .execute()
            .altitude.toFloat() / 90f

    } else {
        when {
            minutes >= defaultMidday -> 1 - (minutes - defaultMidday) / (defaultSunset - defaultMidday)
            else -> (minutes - defaultSunrise) / (defaultMidday - defaultSunrise)
        }.toFloat()
    }

    private fun computeAmbientTintColors(
        now: ZonedDateTime,
        minutes: Duration,
        location: Location?,
    ): TintSpec {
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
            sunrise?.to(TintColor.SUNRISE),
            midday?.to(TintColor.DAY),
            sunset?.to(TintColor.SUNSET),
            midnight?.to(TintColor.NIGHT),
        )

        val (mainColor, sinceDelta) = colors.asSequence()
            .map { (time, color) -> color to if (minutes > time) minutes - time else 1.days - time + minutes }
            .minBy { (_, delta) -> delta }

        val (blendColor, nextDelta) = colors.asSequence()
            .map { (time, color) -> color to if (minutes <= time) time - minutes else 1.days - minutes + time }
            .minBy { (_, delta) -> delta }

        val amount = (sinceDelta / (sinceDelta + nextDelta)).toFloat()

        check(amount in 0f..1f) { "Invalid blend amount: $amount" }

        return TintSpec(
            main = mainColor,
            blend = blendColor,
            amount = (when(blendColor) {
                TintColor.SUNSET, TintColor.SUNRISE -> max(amount - .5f, 0f)
                else -> amount
            } * 2).coerceIn(0f, 1f)
        )
    }

    enum class TintColor { SUNRISE, DAY, SUNSET, NIGHT }

    data class TintSpec(
        val main: TintColor,
        val blend: TintColor,
        @FloatRange(from = 0.0, to = 1.0) val amount: Float,
    )

    companion object {
        private val defaultSunrise = 6.hours
        private val defaultSunset = 18.hours
        private val defaultMidday = 12.hours
        private val defaultMidnight = 0.hours
        const val GOLDER_HOUR_FACTOR = .2f
    }

}
