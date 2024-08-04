package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.microedition.khronos.opengles.GL11

open class ThingLightCloud @AssistedInject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    time: GlobalTime,
    private val timeOfDayTint: TimeOfDayTint,
    @Assisted which: Int,
) : ThingCloud(
    gl,
    models[MODELS[which % MODELS.size]],
    textures[TEXTURES[which % TEXTURES.size]],
    time,
    cloudColor = timeOfDayTint.color
) {

    @AssistedFactory
    interface Factory {
        fun create(which: Int): ThingLightCloud
    }

    companion object {
        private val TEXTURES = intArrayOf(
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5
        )
    }

}
