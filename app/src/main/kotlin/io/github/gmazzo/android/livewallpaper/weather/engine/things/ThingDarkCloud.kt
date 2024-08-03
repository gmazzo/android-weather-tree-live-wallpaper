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
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingCloud.Companion.CLOUD_X_RANGE
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import kotlin.math.abs
import kotlin.random.Random

class ThingDarkCloud @AssistedInject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    @Assisted which: Int,
    @Assisted private val withFlare: Boolean,
) : ThingSimple(time, gl, models, textures, MODELS[which % MODELS.size], TEXTURES[which % TEXTURES.size]) {

    override val engineColor = EngineColor(1f, 1f, 1f, 1f)

    private val texNameFlare = textures[FLARES[which % MODELS.size]]

    private var flashIntensity = 0f

    private fun calculateCloudRangeX(): Float {
        return ((origin.y * CLOUD_X_RANGE) / 90f + abs(
            scale.x.toDouble()
        )).toFloat()
    }

    override fun render() = gl.pushMatrix {
        gl.glBindTexture(GL_TEXTURE_2D, texture.glId)

        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
            model.render()

        if (withFlare && flashIntensity > 0f) {
            gl.glDisable(GL_LIGHTING)
            gl.glBindTexture(GL_TEXTURE_2D, texNameFlare.glId)
            gl.glColor4f(1f,1f,1f, flashIntensity)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            model.render()
            gl.glEnable(GL_LIGHTING)
        }
        gl.glColor4f(1f, 1f, 1f, 1f)
    }

    override fun update() {
        super.update()

        val rangX = calculateCloudRangeX()
        if (origin.x > rangX) {
            origin = origin.copy(x = -rangX)

        } else if (origin.x < (-rangX)) {
            origin = origin.copy(x = rangX)
        }
        engineColor.r = .2f
        engineColor.g = .2f
        engineColor.b = .2f
        if (timeElapsed < 2f) {
            val alpha = timeElapsed * .5f
            engineColor *= alpha
            engineColor.a = alpha
        }
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
