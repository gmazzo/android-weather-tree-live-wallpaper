package io.github.gmazzo.android.livewallpaper.weather.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class screenPreviews {

    private val now = ZonedDateTime.of(
        LocalDateTime.of(2024, 8, 10, 17, 5),
        ZoneId.of("GMT+2"),
    )

    @Composable
    private fun screen(
        weather: WeatherType = WeatherType.SUNNY_DAY,
        missingLocation: Boolean = false,
    ) = SettingsScreen(
        now = now,
        weather = weather,
        updateLocationEnabled = missingLocation,
        missingLocationPermission = missingLocation,
    )

    @Preview
    @Composable
    fun Sunny() = screen(weather = WeatherType.SUNNY_DAY)

    @Preview
    @Composable
    fun Cloudy() = screen(weather = WeatherType.CLOUDY)

    @Preview
    @Composable
    fun Rain() = screen(weather = WeatherType.RAIN)

    @Preview
    @Composable
    fun Storm() = screen(weather = WeatherType.THUNDER_STORMS)

    @Preview
    @Composable
    fun Snow() = screen(weather = WeatherType.SNOW)

    @Preview
    @Composable
    fun Fog() = screen(weather = WeatherType.FOG)

    @Preview
    @Composable
    fun MissingLocation() = screen(missingLocation = true)

}
