package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL11

class SceneCloudy @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    timeOfDayTint: TimeOfDayTint,
    state: MutableStateFlow<WeatherState>,
) : SceneClear(time, gl, models, textures, things, timeOfDayTint, state) {

    override val backgroundId = R.drawable.bg1

}
