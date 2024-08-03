package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingDarkCloud @AssistedInject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    time: GlobalTime,
    @Assisted which: Int,
    @Assisted private val withFlare: Boolean,
) : ThingCloud(
    gl,
    models[MODELS[which % MODELS.size]],
    textures[TEXTURES[which % TEXTURES.size]],
    time,
    color = EngineColor(.2f, .2f, .2f, 1f)
) {

    private val flare = textures[FLARES[which % MODELS.size]]

    private var flashIntensity = 0f

    // FIXME clouds are darker after 6399f93043745b9fb8797af48f4f6bcda294576f
    override fun render() = gl.pushMatrix {
        super.render()

        if (withFlare && flashIntensity > 0f) {
            gl.glDisable(GL_LIGHTING)
            gl.glBindTexture(GL_TEXTURE_2D, flare.glId)
            gl.glColor4f(1f, 1f, 1f, flashIntensity)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            model.render()
            gl.glEnable(GL_LIGHTING)
        }
        gl.glColor4f(1f, 1f, 1f, 1f)
    }

    override fun update() {
        super.update()

        if (withFlare) {
            if (flashIntensity > 0f) {
                flashIntensity -= 1.25f * time.deltaSeconds
            }
            if (flashIntensity <= 0f && Random.nextFloat(0f, 4.5f) < time.deltaSeconds) {
                flashIntensity = .5f
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(which: Int, withFlare: Boolean = true): ThingDarkCloud
    }

    companion object {
        private val MODELS = intArrayOf(
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m
        )
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
