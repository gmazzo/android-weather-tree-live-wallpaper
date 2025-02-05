package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Named
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

open class ThingLightCloud @AssistedInject constructor(
    random: Random,
    gl: GL11,
    models: Models,
    textures: Textures,
    time: GlobalTime,
    @Named("clouds") cloudsColor: EngineColor,
    @Assisted which: Int,
) : ThingCloud(
    random, gl,
    model = models[MODELS[which % MODELS.size]],
    texture = textures[TEXTURES[which % TEXTURES.size]],
    time,
    cloudsColor,
) {

    @AssistedFactory
    fun interface Factory : ThingCloud.Factory<ThingLightCloud>

    companion object {
        private val TEXTURES = intArrayOf(
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5
        )
    }

}
