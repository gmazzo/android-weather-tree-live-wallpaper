package io.github.gmazzo.android.livewallpaper.weather.engine

import android.location.Location
import android.util.Log
import io.github.gmazzo.android.livewallpaper.weather.sunrisesunset.SunriseSunsetCalculator
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.floor

object SkyManager {
    private const val MILLISECONDS_PER_DAY = 86400000
    private const val MILLISECONDS_PER_HOUR = 3600000
    private const val TAG = "GL Engine"
    const val ZENITH_ASTRONOMICAL: Double = 108.0
    const val ZENITH_CIVIL: Double = 96.0
    const val ZENITH_NAUTICAL: Double = 102.0
    const val ZENITH_OFFICIAL: Double = 90.833333

    private fun dayOfYear(): Int {
        return Calendar.getInstance()[Calendar.DAY_OF_YEAR]
    }

    fun getMoonPhase(): Double {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DATE]
        val transformedYear = (year.toDouble()) - floor(((12 - month) / 10).toDouble())
        var transformedMonth = month + 9
        if (transformedMonth >= 12) {
            transformedMonth -= 12
        }
        val term3 = floor(floor((transformedYear / 100.0) + 49.0) * 0.75) - 38.0
        var intermediate = ((floor(365.25 * (4712.0 + transformedYear)) + floor(
            (30.6 * (transformedMonth.toDouble())) + 0.5
        )) + (day.toDouble())) + 59.0
        if (intermediate > 2299160.0) {
            intermediate -= term3
        }
        var normalizedPhase = (intermediate - 2451550.1) / 29.530588853
        normalizedPhase -= floor(normalizedPhase)
        if (normalizedPhase < 0.0) {
            return normalizedPhase + 1.0
        }
        return normalizedPhase
    }

    private fun GetSunEvent(
        event: SunEvent,
        lat: Double,
        lon: Double,
        day: Int,
        degree: Double
    ): Calendar? {
        val tz = TimeZone.getDefault()
        val calendar = Calendar.getInstance(tz)
        when (AnonymousClass1.`$SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent`[event.ordinal]) {
            1 -> {
                Log.i(TAG, "GetSunriseEvent: tz=" + tz.displayName + " lat=" + lat + " lon=" + lon)
                return SunriseSunsetCalculator.getSunrise(lat, lon, tz, calendar, degree)
            }

            2 -> {
                Log.i(TAG, "GetSunsetEvent: tz=" + tz.displayName + " lat=" + lat + " lon=" + lon)
                return SunriseSunsetCalculator.getSunset(lat, lon, tz, calendar, degree)
            }

            else -> return calendar
        }
    }

    fun GetSunPosition(latitude: Double, longitude: Double): Float {
        return GetSunPosition(0, latitude, longitude, 90.833333)
    }

    fun GetSunPosition(time: Long, latitude: Double, longitude: Double, zenith: Double): Float {
        var time = time
        val dayofyear = dayOfYear()
        val todaySunrise_time =
            GetSunEvent(SunEvent.SUNRISE, latitude, longitude, dayofyear, zenith)!!
                .timeInMillis
        val todaySunset_time =
            GetSunEvent(SunEvent.SUNSET, latitude, longitude, dayofyear, zenith)!!
                .timeInMillis
        if (time == 0L) {
            time = Calendar.getInstance().timeInMillis
        }
        if (time < todaySunrise_time) {
            val yesterdaySunset_time =
                GetSunEvent(SunEvent.SUNSET, latitude, longitude, dayofyear - 1, zenith)!!
                    .timeInMillis
            return (((time - yesterdaySunset_time).toFloat()) / ((todaySunrise_time - yesterdaySunset_time).toFloat())) * -1.0f
        } else if (time < todaySunset_time) {
            return ((time - todaySunrise_time).toFloat()) / ((todaySunset_time - todaySunrise_time).toFloat())
        } else {
            return (((time - todaySunset_time).toFloat()) / ((GetSunEvent(
                SunEvent.SUNRISE,
                latitude,
                longitude,
                dayofyear + 1,
                zenith
            )!!
                .timeInMillis - todaySunset_time).toFloat())) * -1.0f
        }
    }

    fun GetSunPosition(location: Location): Float {
        return GetSunPosition(0, location.latitude, location.longitude, 90.833333)
    }

    fun GetSunPosition(location: Location, d: Double): Float {
        return GetSunPosition(0, location.latitude, location.longitude, d)
    }

    fun GetSunrise(lat: Double, lon: Double): Calendar? {
        return GetSunEvent(SunEvent.SUNRISE, lat, lon, dayOfYear(), 0.0)
    }

    fun GetSunrise(lat: Double, lon: Double, degree: Double): Calendar? {
        return GetSunEvent(SunEvent.SUNRISE, lat, lon, dayOfYear(), degree)
    }

    fun GetSunrise(location: Location): Calendar? {
        return GetSunEvent(
            SunEvent.SUNRISE,
            location.latitude,
            location.longitude,
            dayOfYear(),
            0.0
        )
    }

    fun GetSunrise(location: Location, d: Double): Calendar? {
        return GetSunEvent(SunEvent.SUNRISE, location.latitude, location.longitude, dayOfYear(), d)
    }

    fun GetSunset(d: Double, d1: Double): Calendar? {
        return GetSunEvent(SunEvent.SUNSET, d, d1, dayOfYear(), 0.0)
    }

    fun GetSunset(d: Double, d1: Double, d2: Double): Calendar? {
        return GetSunEvent(SunEvent.SUNSET, d, d1, dayOfYear(), d2)
    }

    fun GetSunset(location: Location): Calendar? {
        return GetSunEvent(SunEvent.SUNSET, location.latitude, location.longitude, dayOfYear(), 0.0)
    }

    fun GetSunset(location: Location, d: Double): Calendar? {
        return GetSunEvent(SunEvent.SUNSET, location.latitude, location.longitude, dayOfYear(), d)
    }

    private fun JulianDay(calendar: Calendar): Int {
        val secs = calendar.timeInMillis / 1000
        val ss = secs % 60
        val minutes = (secs - ss) / 60
        val mm = minutes % 60
        return (((((secs - ss) - (60 * mm)) - (3600 * (((minutes - mm) / 60) % 24))) / 86400) + 2440588).toInt()
    }

    /* renamed from: io.github.gmazzo.android.livewallpaper.weather.engine.SkyManager$1 */
    internal object AnonymousClass1 {
        /* synthetic */val `$SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent`: IntArray =
            IntArray(SunEvent.entries.size)

        init {
            try {
                `$SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent`[SunEvent.SUNRISE.ordinal] =
                    1
            } catch (e: NoSuchFieldError) {
            }
            try {
                `$SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent`[SunEvent.SUNSET.ordinal] = 2
            } catch (e2: NoSuchFieldError) {
            }
        }
    }

    private enum class SunEvent {
        SUNRISE,
        SUNSET
    }
}
