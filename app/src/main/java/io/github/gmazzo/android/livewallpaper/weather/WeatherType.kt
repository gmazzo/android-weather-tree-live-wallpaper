package io.github.gmazzo.android.livewallpaper.weather

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    WeatherType.SUNNY_DAY,
    WeatherType.MOSTLY_SUNNY_DAY,
    WeatherType.PARTLY_SUNNY_DAY,
    WeatherType.INTERMITTENT_CLOUDS_DAY,
    WeatherType.HAZY_SUNSHINE_DAY,
    WeatherType.MOSTLY_CLOUDY_DAY,
    WeatherType.CLOUDY,
    WeatherType.DREARY_OVERCAST,
    WeatherType.FOG,
    WeatherType.SHOWERS,
    WeatherType.MOSTLY_CLOUDY_WITH_SHOWERS_DAY,
    WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY,
    WeatherType.THUNDER_STORMS,
    WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_DAY,
    WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY,
    WeatherType.RAIN,
    WeatherType.FLURRIES,
    WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_DAY,
    WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY,
    WeatherType.SNOW,
    WeatherType.MOSTLY_CLOUDY_WITH_SNOW_DAY,
    WeatherType.ICE,
    WeatherType.SLEET,
    WeatherType.FREEZING_RAIN,
    WeatherType.RAIN_AND_SNOW,
    WeatherType.HOT,
    WeatherType.COLD,
    WeatherType.WINDY,
    WeatherType.CLEAR_NIGHT,
    WeatherType.MOSTLY_CLEAR_NIGHT,
    WeatherType.PARTLY_CLOUDY_NIGHT,
    WeatherType.INTERMITTENT_CLOUDS_NIGHT,
    WeatherType.HAZY_MOONLIGHT_NIGHT,
    WeatherType.MOSTLY_CLOUDY_NIGHT,
    WeatherType.PARTLY_CLOUDY_WITH_SHOWERS_NIGHT,
    WeatherType.MOSTLY_CLOUDY_WITH_SHOWERS_NIGHT,
    WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT,
    WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT,
    WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT,
    WeatherType.MOSTLY_CLOUDY_WITH_SNOW_NIGHT
)
annotation class WeatherType {

    // from https://developer.accuweather.com/weather-icons
    companion object {
        const val SUNNY_DAY = 1
        const val MOSTLY_SUNNY_DAY = 2
        const val PARTLY_SUNNY_DAY = 3
        const val INTERMITTENT_CLOUDS_DAY = 4
        const val HAZY_SUNSHINE_DAY = 5
        const val MOSTLY_CLOUDY_DAY = 6
        const val CLOUDY = 7
        const val DREARY_OVERCAST = 8
        const val FOG = 11
        const val SHOWERS = 12
        const val MOSTLY_CLOUDY_WITH_SHOWERS_DAY = 13
        const val PARTLY_SUNNY_WITH_SHOWERS_DAY = 14
        const val THUNDER_STORMS = 15
        const val MOSTLY_CLOUDY_WITH_THUNDER_STORMS_DAY = 16
        const val PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY = 17
        const val RAIN = 18
        const val FLURRIES = 19
        const val MOSTLY_CLOUDY_WITH_FLURRIES_DAY = 20
        const val PARTLY_SUNNY_WITH_FLURRIES_DAY = 21
        const val SNOW = 22
        const val MOSTLY_CLOUDY_WITH_SNOW_DAY = 23
        const val ICE = 24
        const val SLEET = 25
        const val FREEZING_RAIN = 26
        const val RAIN_AND_SNOW = 29
        const val HOT = 30
        const val COLD = 31
        const val WINDY = 32
        const val CLEAR_NIGHT = 33
        const val MOSTLY_CLEAR_NIGHT = 34
        const val PARTLY_CLOUDY_NIGHT = 35
        const val INTERMITTENT_CLOUDS_NIGHT = 36
        const val HAZY_MOONLIGHT_NIGHT = 37
        const val MOSTLY_CLOUDY_NIGHT = 38
        const val PARTLY_CLOUDY_WITH_SHOWERS_NIGHT = 39
        const val MOSTLY_CLOUDY_WITH_SHOWERS_NIGHT = 40
        const val PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT = 41
        const val MOSTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT = 42
        const val MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT = 43
        const val MOSTLY_CLOUDY_WITH_SNOW_NIGHT = 44
    }

}
