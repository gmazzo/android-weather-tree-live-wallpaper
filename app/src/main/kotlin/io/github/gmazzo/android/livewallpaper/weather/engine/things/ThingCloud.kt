package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase.Companion.todEngineColorFinal
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneClear
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs

class ThingCloud : Thing() {
    var which: Int = 0
    var fade: Float = 0f

    init {
        this.engineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
        this.vis_width = 0.0f
        origin.x = -100.0f
        origin.y = 15.0f
        origin.z = 50.0f
    }

    private fun setAlpha(alpha: Float) {
        engineColor!!.times(alpha)
        engineColor!!.a = alpha
    }

    fun randomizeScale() {
        scale.set(
            3.5f + GlobalRand.floatRange(0.0f, 2.0f),
            3.0f,
            3.5f + GlobalRand.floatRange(0.0f, 2.0f)
        )
    }

    private fun calculateCloudRangeX(): Float {
        return ((origin.y * SceneClear.Companion.CLOUD_X_RANGE) / 90.0f + abs(
            (scale.x * 6.0f).toDouble()
        )).toFloat()
    }

    override fun render(gl: GL10, textures: Textures?, models: Models?) {
        if (model == null) {
            model = models!![MODELS[which - 1]]
            texture = textures!![TEXTURES[which - 1]]
        }
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        super.render(gl, textures, models)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val rangX = calculateCloudRangeX()
        if (origin.x > rangX) {
            origin.x = GlobalRand.floatRange((-rangX) - 5.0f, (-rangX) + 5.0f)
            this.fade = 0.0f
            setAlpha(this.fade)
            this.sTimeElapsed = 0.0f
            randomizeScale()
        }
        val todColors: EngineColor = todEngineColorFinal!!
        engineColor!!.r = todColors.r
        engineColor!!.g = todColors.g
        engineColor!!.b = todColors.b
        if (this.sTimeElapsed < 2.0f) {
            setAlpha(this.sTimeElapsed * 0.5f)
        }
    }

    companion object {
        private val MODELS = intArrayOf(
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m
        )
        private val TEXTURES = intArrayOf(
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5
        )
        const val CLOUD_FADE_START_X: Float = 25.0f
        const val CLOUD_FADE_START_Y: Float = 25.0f
        const val CLOUD_RESET_X: Float = 10.0f
        const val CLOUD_RESET_Y: Float = 10.0f
    }
}
