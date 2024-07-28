package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL11

class SceneCloudy @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
) : SceneClear(gl, models, textures) {

    override val backgroundId = R.drawable.bg1

}
