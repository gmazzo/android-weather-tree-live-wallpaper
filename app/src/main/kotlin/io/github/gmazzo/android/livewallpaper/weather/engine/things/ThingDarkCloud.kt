package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneClear.Companion.CLOUD_X_RANGE
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.abs

class ThingDarkCloud(
    models: Models,
    textures: Textures,
    which: Int,
    private val withFlare: Boolean
) : SimpleThing(models, textures, MODELS[which % MODELS.size], TEXTURES[which % TEXTURES.size]) {

    override val engineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)

    private val texNameFlare by lazy { textures[FLARES[which % MODELS.size]] }

    private var flashIntensity = 0.0f

    private fun calculateCloudRangeX(): Float {
        return ((origin.y * CLOUD_X_RANGE) / 90.0f + abs(
            scale.x.toDouble()
        )).toFloat()
    }

    fun randomizeScale() {
        scale.set(
            3.5f + GlobalRand.floatRange(0.0f, 2.0f),
            3.0f,
            3.5f + GlobalRand.floatRange(0.0f, 2.0f)
        )
    }

    override fun render(gl: GL10) {
        particleSystem?.render(gl as GL11, origin)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.glId)

        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glPushMatrix()
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        gl.glRotatef(
            angles.a,
            angles.r,
            angles.g,
            angles.b
        )
        if (!pref_minimalist) {
            model.render()
        }
        if (withFlare && flashIntensity > 0.0f) {
            gl.glDisable(GL10.GL_LIGHTING)
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texNameFlare.glId)
            gl.glColor4f(
                pref_boltEngineColor.r,
                pref_boltEngineColor.g,
                pref_boltEngineColor.b,
                flashIntensity
            )
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            model.render()
            gl.glEnable(GL10.GL_LIGHTING)
        }
        gl.glPopMatrix()
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val rangX = calculateCloudRangeX()
        if (origin.x > rangX) {
            origin.x = -rangX
        } else if (origin.x < (-rangX)) {
            origin.x = rangX
        }
        engineColor.r = 0.2f
        engineColor.g = 0.2f
        engineColor.b = 0.2f
        if (sTimeElapsed < 2.0f) {
            val alpha = sTimeElapsed * 0.5f
            engineColor *= alpha
            engineColor.a = alpha
        }
        if (withFlare) {
            if (flashIntensity > 0.0f) {
                flashIntensity -= 1.25f * timeDelta
            }
            if (flashIntensity <= 0.0f && GlobalRand.floatRange(0.0f, 4.5f) < timeDelta) {
                flashIntensity = 0.5f
            }
        }
    }

    companion object {
        var pref_boltEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
        var pref_minimalist: Boolean = false
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
