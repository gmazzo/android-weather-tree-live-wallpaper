package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.theme.Weather

enum class SceneMode(@param:StringRes val textId: Int, val icon: ImageVector) {
    CLEAR(R.string.scene_clear, Icons.Weather.Sunny),
    CLOUDY(R.string.scene_cloudy, Icons.Weather.Cloudy),
    RAIN(R.string.scene_rain, Icons.Weather.Rain),
    STORM(R.string.scene_storm, Icons.Weather.Storm),
    SNOW(R.string.scene_snow, Icons.Weather.Snow),
    FOG(R.string.scene_fog, Icons.Weather.Fog),
}
