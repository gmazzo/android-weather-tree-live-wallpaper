package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.abs
import kotlin.random.Random

class ThingCloud @AssistedInject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    @Named("timeOfDay") private val timeOfDayColor: EngineColor,
    @Assisted which: Int,
) : ThingSimple(gl, models, textures, MODELS[which % MODELS.size], TEXTURES[which % TEXTURES.size]) {

    override val engineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)

    init {
        this.visWidth = 0.0f
        origin.x = -100.0f
        origin.y = 15.0f
        origin.z = 50.0f
    }

    private fun calculateCloudRangeX(): Float {
        return ((origin.y * CLOUD_X_RANGE) / 90.0f + abs(
            (scale.x * 6.0f).toDouble()
        )).toFloat()
    }

    override fun render() {
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        super.render()
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val rangX = calculateCloudRangeX()
        if (origin.x > rangX) {
            origin.x = Random.nextFloat((-rangX) - 5.0f, (-rangX) + 5.0f)
            engineColor.set(0)
            timeElapsed = 0.0f
            randomizeScale()
        }

        engineColor.r = timeOfDayColor.r
        engineColor.g = timeOfDayColor.g
        engineColor.b = timeOfDayColor.b
        if (timeElapsed < 2.0f) {
            val alpha = timeElapsed * 0.5f
            engineColor *= alpha
            engineColor.a = alpha
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(which: Int): ThingCloud
    }

    companion object {
        const val CLOUD_X_RANGE: Float = 45.0f

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
