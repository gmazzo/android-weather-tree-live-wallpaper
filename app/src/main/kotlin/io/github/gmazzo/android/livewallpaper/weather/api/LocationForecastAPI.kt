// Source:
// https://api.met.no/weatherapi/locationforecast/2.0/documentation#JSON_format_and_variables
// Example API:
//  https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=59.93&lon=10.72&altitude=90
package io.github.gmazzo.android.livewallpaper.weather.api

import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

interface LocationForecastAPI {

    @GET("locationforecast/2.0/compact")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("altitude") altitude: Int? = null,
    ): Forecast

    @Serializable
    data class Forecast(
        val properties: Properties,
    )

    @Serializable
    data class Properties(
        @SerialName("timeseries") val timeSeries: List<Time>,
    )

    @Serializable
    data class Time(
        @Serializable(DateSerializer::class) val time: Date,
        val data: Data,
    )

    @Serializable
    data class Data(
        @SerialName("next_1_hours") val nextHour: Hour? = null,
        @SerialName("next_6_hours") val next6Hours: Hour? = null,
        @SerialName("next_12_hours") val next12Hours: Hour? = null,
    )

    @Serializable
    data class Hour(
        val summary: Summary,
    ) {

        val weatherType = when (summary.symbolCode) {
            "clearsky_day" -> WeatherType.SUNNY_DAY
            "clearsky_night" -> WeatherType.CLEAR_NIGHT
            "clearsky_polartwilight" -> WeatherType.SUNNY_DAY

            "fair_day" -> WeatherType.MOSTLY_SUNNY_DAY
            "fair_night" -> WeatherType.MOSTLY_CLEAR_NIGHT
            "fair_polartwilight" -> WeatherType.MOSTLY_SUNNY_DAY

            "lightssnowshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "lightssnowshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "lightssnowshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "lightsnowshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY
            "lightsnowshowers_night" -> WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT
            "lightsnowshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY

            "heavyrainandthunder" -> WeatherType.THUNDER_STORMS
            "heavysnowandthunder" -> WeatherType.SNOW
            "rainandthunder" -> WeatherType.THUNDER_STORMS
            "heavysleetshowersandthunder_day" -> WeatherType.THUNDER_STORMS
            "heavysleetshowersandthunder_night" -> WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "heavysleetshowersandthunder_polartwilight" -> WeatherType.THUNDER_STORMS

            "heavysnow" -> WeatherType.SNOW

            "heavyrainshowers_day" -> WeatherType.SHOWERS
            "heavyrainshowers_night" -> WeatherType.MOSTLY_CLOUDY_WITH_SHOWERS_NIGHT
            "heavyrainshowers_polartwilight" -> WeatherType.SHOWERS
            "lightsleet" -> WeatherType.SLEET
            "heavyrain" -> WeatherType.RAIN
            "lightrainshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "lightrainshowers_night" -> WeatherType.PARTLY_CLOUDY_WITH_SHOWERS_NIGHT
            "lightrainshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "heavysleetshowers_day" -> WeatherType.SHOWERS
            "heavysleetshowers_night" -> WeatherType.MOSTLY_CLOUDY_WITH_SHOWERS_NIGHT
            "heavysleetshowers_polartwilight" -> WeatherType.SHOWERS
            "lightsleetshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "lightsleetshowers_night" -> WeatherType.PARTLY_CLOUDY_WITH_SHOWERS_NIGHT
            "lightsleetshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY

            "snow" -> WeatherType.SNOW

            "heavyrainshowersandthunder_day" -> WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_DAY
            "heavyrainshowersandthunder_night" -> WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "heavyrainshowersandthunder_polartwilight" -> WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_DAY
            "snowshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY
            "snowshowers_night" -> WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT
            "snowshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY
            "fog" -> WeatherType.FOG
            "snowshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "snowshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "snowshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "lightsnowandthunder" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "heavysleetandthunder" -> WeatherType.THUNDER_STORMS
            "lightrain" -> WeatherType.RAIN
            "rainshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "rainshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "rainshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "rain" -> WeatherType.RAIN
            "lightsnow" -> WeatherType.FLURRIES
            "lightrainshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "lightrainshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "lightrainshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "heavysleet" -> WeatherType.SLEET
            "sleetandthunder" -> WeatherType.THUNDER_STORMS
            "lightrainandthunder" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "sleet" -> WeatherType.SLEET
            "lightssleetshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "lightssleetshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "lightssleetshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "lightsleetandthunder" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "partlycloudy_day" -> WeatherType.INTERMITTENT_CLOUDS_DAY
            "partlycloudy_night" -> WeatherType.INTERMITTENT_CLOUDS_NIGHT
            "partlycloudy_polartwilight" -> WeatherType.INTERMITTENT_CLOUDS_DAY
            "sleetshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "sleetshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "sleetshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "rainshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "rainshowers_night" -> WeatherType.PARTLY_CLOUDY_WITH_SHOWERS_NIGHT
            "rainshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "snowandthunder" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "sleetshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "sleetshowers_night" -> WeatherType.PARTLY_CLOUDY_WITH_SHOWERS_NIGHT
            "sleetshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY
            "cloudy" -> WeatherType.CLOUDY
            "heavysnowshowersandthunder_day" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "heavysnowshowersandthunder_night" -> WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT
            "heavysnowshowersandthunder_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY
            "heavysnowshowers_day" -> WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY
            "heavysnowshowers_night" -> WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT
            "heavysnowshowers_polartwilight" -> WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY
            else -> WeatherType.CLOUDY // Default case
        }

    }

    @Serializable
    data class Summary(
        @SerialName("symbol_code") val symbolCode: String,
    )

}
