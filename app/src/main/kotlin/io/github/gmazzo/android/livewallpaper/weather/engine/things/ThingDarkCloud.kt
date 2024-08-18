package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingDarkCloud @AssistedInject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    time: GlobalTime,
    sceneMode: SceneMode,
    @Named("clouds") cloudsColor: EngineColor,
    @Assisted which: Int,
) : ThingCloud(
    gl,
    model = models[MODELS[which % MODELS.size]],
   texture = textures[TEXTURES[which % TEXTURES.size]],
    time,
    cloudsColor,
) {

    private val flare = when (sceneMode) {
        SceneMode.STORM -> textures[FLARES[which % MODELS.size]]
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
                flashIntensity -= 1.25f * time.deltaSeconds
            }
            if (flashIntensity <= 0f && Random.nextFloat(0f, 4.5f) < time.deltaSeconds) {
                flashIntensity = .5f
            }
        }
    }

    @AssistedFactory
    interface Factory : ThingCloud.Factory<ThingDarkCloud>

    companion object {
        private val TEXTURES = intArrayOf(
            R.drawable.clouddark1, R.drawable.clouddark2, R.drawable.clouddark3,
            R.drawable.clouddark4, R.drawable.clouddark5
        )
        private val FLARES = intArrayOf(
            R.drawable.cloudflare1, R.drawable.cloudflare2, R.drawable.cloudflare3,
            R.drawable.cloudflare4, R.drawable.cloudflare5
        )
    }
}
