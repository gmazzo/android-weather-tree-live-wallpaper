package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

open class ThingLightCloud @AssistedInject constructor(
    random: Random,
    gl: GL11,
    resources: Resources,
    @Named("real") clock: MutableStateFlow<Clock>,
    @Named("clouds") cloudsColor: EngineColor,
    @Assisted which: Int,
) : ThingCloud(
    random, gl,
    model = resources.models[which % resources.models.size],
    texture = resources.textures[which % resources.textures.size],
    clock,
    cloudsColor,
) {

    @AssistedFactory
    fun interface Factory : ThingCloud.Factory<ThingLightCloud>

    class Resources @Inject constructor(
        models: Models,
        textures: Textures,
    ) : ThingCloud.Resources(models) {
        val cloud1 = textures[R.drawable.cloud1]
        val cloud2 = textures[R.drawable.cloud2]
        val cloud3 = textures[R.drawable.cloud3]
        val cloud4 = textures[R.drawable.cloud4]
        val cloud5 = textures[R.drawable.cloud5]

        val textures = arrayOf(cloud1, cloud2, cloud3, cloud4, cloud5)
    }

}
