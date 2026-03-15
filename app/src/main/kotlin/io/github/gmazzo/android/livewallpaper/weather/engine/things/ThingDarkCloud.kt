package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingDarkCloud @AssistedInject constructor(
    random: Random,
    gl: GL11,
    resources: Resources,
    @Named("real") clock: MutableStateFlow<Clock>,
    sceneMode: SceneMode,
    @Named("clouds") cloudsColor: EngineColor,
    @Assisted which: Int,
) : ThingCloud(
    random, gl,
    model = resources.models[which % resources.models.size],
    texture = resources.textures[which % resources.textures.size],
    clock,
    cloudsColor,
) {

    private val flare = when (sceneMode) {
        SceneMode.STORM -> resources.flares[which % resources.flares.size]
        else -> null
    }

    private var flashIntensity = 0f

    override fun render() {
        super.render()

        if (flare != null && flashIntensity > 0f) {
            color.a = flashIntensity
            super.render(GL_SRC_ALPHA, GL_ONE)
            color.a = 1f
        }
    }

    override fun update() {
        super.update()

        if (flare != null) {
            if (flashIntensity > 0f) {
                flashIntensity -= 1.25f * clock.value.deltaSeconds
            }
            if (flashIntensity <= 0f && random.nextFloat(0f, 4.5f) < clock.value.deltaSeconds) {
                flashIntensity = .5f
            }
        }
    }

    @AssistedFactory
    fun interface Factory : ThingCloud.Factory<ThingDarkCloud>

    class Resources @Inject constructor(
        models: Models,
        textures: Textures,
    ) : ThingCloud.Resources(models) {
        val cloudDark1 = textures[R.drawable.clouddark1]
        val cloudDark2 = textures[R.drawable.clouddark2]
        val cloudDark3 = textures[R.drawable.clouddark3]
        val cloudDark4 = textures[R.drawable.clouddark4]
        val cloudDark5 = textures[R.drawable.clouddark5]
        val cloudflare1 = textures[R.drawable.cloudflare1]
        val cloudflare2 = textures[R.drawable.cloudflare2]
        val cloudflare3 = textures[R.drawable.cloudflare3]
        val cloudflare4 = textures[R.drawable.cloudflare4]
        val cloudflare5 = textures[R.drawable.cloudflare5]

        val textures = arrayOf(cloudDark1, cloudDark2, cloudDark3, cloudDark4, cloudDark5)
        val flares = arrayOf(cloudflare1, cloudflare2, cloudflare3, cloudflare4, cloudflare5)
    }

}
