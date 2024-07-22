package io.github.gmazzo.android.livewallpaper.weather.engine.things

import android.util.Log
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.SkyManager
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10

class ThingMoon : Thing() {
    private var mOldPhase = 6
    private var mPhase = 0

    init {
        this.texture = null
        this.engineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun render(gl: GL10, textures: Textures?, models: Models?) {
        if (model == null) {
            model = models!![R.raw.plane_16x16]
        }
        if (this.texture == null || this.mPhase != this.mOldPhase) {
            this.texture = textures!![PHASES[mPhase]]
            this.mOldPhase = this.mPhase
        }
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        super.render(gl, textures, models)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val position: Float = SceneBase.Companion.todSunPosition
        if (position >= 0.0f) {
            scale.set(0.0f)
        } else {
            scale.set(2.0f)
            val altitude = position * -175.0f
            var alpha = altitude / 25.0f
            if (alpha > 1.0f) {
                alpha = 1.0f
            }
            engineColor!!.a = alpha
            origin.z = altitude - 80.0f
            if (origin.z > 0.0f) {
                origin.z = 0.0f
            }
        }
        val moon_phase = SkyManager.GetMoonPhase()
        this.mPhase = Math.round((moon_phase.toFloat()) * 12.0f)
        if (this.mPhase > 11) {
            this.mPhase = 0
        }
        if (this.mPhase != this.mOldPhase) {
            Log.i(TAG, "moon_phase=" + moon_phase + " tex=" + this.mPhase)
        }
    }

    companion object {
        private const val TAG = "Moon"
        private val PHASES = intArrayOf(
            R.drawable.moon_0, R.drawable.moon_1, R.drawable.moon_2, R.drawable.moon_3,
            R.drawable.moon_4, R.drawable.moon_5, R.drawable.moon_6, R.drawable.moon_7,
            R.drawable.moon_8, R.drawable.moon_9, R.drawable.moon_10, R.drawable.moon_11
        )
    }
}
