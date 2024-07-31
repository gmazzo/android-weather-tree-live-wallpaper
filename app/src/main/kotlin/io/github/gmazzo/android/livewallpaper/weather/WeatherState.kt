package io.github.gmazzo.android.livewallpaper.weather

import android.location.Location
import androidx.annotation.FloatRange

data class WeatherState(
    val location: Location? = null,
    val weatherType: WeatherType = WeatherType.SUNNY_DAY,
    @FloatRange(from = -1.0, to = 1.0) val sunPosition: Float = 0f,
)
