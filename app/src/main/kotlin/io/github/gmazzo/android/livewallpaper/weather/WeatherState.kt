package io.github.gmazzo.android.livewallpaper.weather

data class WeatherState(
    val latitude: Float = Float.NaN,
    val longitude: Float = Float.NaN,
    val weatherType: WeatherType = WeatherType.SUNNY_DAY,
    val sunPosition: Float = 0f,
)