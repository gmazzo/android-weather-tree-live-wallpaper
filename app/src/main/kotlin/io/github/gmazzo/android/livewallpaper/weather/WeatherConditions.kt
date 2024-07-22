package io.github.gmazzo.android.livewallpaper.weather

data class WeatherConditions(
    val latitude: Float = Float.NaN,
    val longitude: Float = Float.NaN,
    val weatherType: WeatherType = WeatherType.SUNNY_DAY,
)
