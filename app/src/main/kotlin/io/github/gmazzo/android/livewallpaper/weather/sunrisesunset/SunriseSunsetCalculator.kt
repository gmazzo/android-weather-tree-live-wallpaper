package io.github.gmazzo.android.livewallpaper.weather.sunrisesunset

import java.util.Calendar
import java.util.TimeZone

object SunriseSunsetCalculator {
    fun getSunrise(
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone,
        date: Calendar,
        degrees: Double
    ): Calendar? {
        return SolarEventCalculator(latitude, longitude, timeZone).computeSunriseCalendar(
            Zenith(
                90.0 - degrees
            ), date
        )
    }

    fun getSunset(
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone,
        date: Calendar,
        degrees: Double
    ): Calendar? {
        return SolarEventCalculator(
            latitude,
            longitude,
            timeZone
        ).computeSunsetCalendar(Zenith(90.0 - degrees), date)
    }
}
