package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.theme.WeatherIcons

enum class SceneMode(@StringRes val textId: Int, val icon: ImageVector) {
    CLEAR(R.string.scene_clear, WeatherIcons.sunny),
    CLOUDY(R.string.scene_cloudy, WeatherIcons.cloudy),
    RAIN(R.string.scene_rain, WeatherIcons.rainy),
    STORM(R.string.scene_storm, WeatherIcons.storm),
    SNOW(R.string.scene_snow, WeatherIcons.snowy),
    FOG(R.string.scene_fog, WeatherIcons.foggy),
}
