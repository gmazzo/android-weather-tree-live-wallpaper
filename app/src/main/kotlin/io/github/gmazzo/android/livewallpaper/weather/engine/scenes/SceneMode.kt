package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.gmazzo.android.livewallpaper.weather.WeatherIcons

enum class SceneMode(val icon: ImageVector) {
    CLEAR(WeatherIcons.sunny),
    CLOUDY(WeatherIcons.cloudy),
    FOG(WeatherIcons.foggy),
    RAIN(WeatherIcons.rainy),
    SNOW(WeatherIcons.snowy),
    STORM(WeatherIcons.storm),
}
