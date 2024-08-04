package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class ThingCloud(
    gl: GL11,
    model: Model,
    texture: Texture,
    time: GlobalTime,
    private val cloudColor: EngineColor,
) : ThingMoving(gl, model, texture, time) {

    final override val engineColor = EngineColor()

    init {
        scale = Vector(
            (3.5f + Random.nextFloat(0f, 2f)).let { if (Random.nextBoolean()) it else -it },
            3f,
            3.5f + Random.nextFloat(0f, 2f)
        )
    }

    override fun render() {
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        super.render()
    }

    override fun update() {
        super.update()

        val rangX = origin.y / 2 + (scale.x * 6).absoluteValue
        if (origin.x > rangX) {
            origin = origin.copy(x = Random.nextFloat((-rangX) - 5f, (-rangX) + 5f))
            timeElapsed = 0f
        }

        engineColor.set(cloudColor)

        if (timeElapsed < 2f) {
            val alpha = timeElapsed * .5f

            engineColor *= alpha
            engineColor.a = alpha
        }
    }

    companion object {
        @JvmStatic
        protected val MODELS = intArrayOf(
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m
        )
    }

}
