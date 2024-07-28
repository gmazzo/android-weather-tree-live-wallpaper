package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene.Companion.todEngineColorFinal
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneClear.Companion.CLOUD_X_RANGE
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs

class ThingCloud(
    models: Models,
    textures: Textures,
    which: Int,
) : SimpleThing(models, textures, MODELS[which % MODELS.size], TEXTURES[which % TEXTURES.size]) {

    override val engineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)

    init {
        this.vis_width = 0.0f
        origin.x = -100.0f
        origin.y = 15.0f
        origin.z = 50.0f
    }

    fun randomizeScale() {
        scale.set(
            3.5f + GlobalRand.floatRange(0.0f, 2.0f),
            3.0f,
            3.5f + GlobalRand.floatRange(0.0f, 2.0f)
        )
    }

    private fun calculateCloudRangeX(): Float {
        return ((origin.y * CLOUD_X_RANGE) / 90.0f + abs(
            (scale.x * 6.0f).toDouble()
        )).toFloat()
    }

    override fun render(gl: GL10) {
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        super.render(gl)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val rangX = calculateCloudRangeX()
        if (origin.x > rangX) {
            origin.x = GlobalRand.floatRange((-rangX) - 5.0f, (-rangX) + 5.0f)
            engineColor.set(0)
            sTimeElapsed = 0.0f
            randomizeScale()
        }
        val todColors: EngineColor = todEngineColorFinal!!
        engineColor.r = todColors.r
        engineColor.g = todColors.g
        engineColor.b = todColors.b
        if (sTimeElapsed < 2.0f) {
            val alpha = sTimeElapsed * 0.5f
            engineColor *= alpha
            engineColor.a = alpha
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
    }
}
