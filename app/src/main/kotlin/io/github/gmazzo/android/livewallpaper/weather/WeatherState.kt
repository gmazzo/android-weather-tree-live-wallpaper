package io.github.gmazzo.android.livewallpaper.weather

data class WeatherState(
    val latitude: Float,
    val longitude: Float,
    val weatherCondition: WeatherType,
)
