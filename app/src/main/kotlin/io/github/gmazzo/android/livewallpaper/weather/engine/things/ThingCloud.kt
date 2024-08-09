package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.StaticModel
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import javax.microedition.khronos.opengles.GL11
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class ThingCloud(
    gl: GL11,
    model: StaticModel,
    texture: Texture,
    time: GlobalTime,
    private val cloudsColor: EngineColor,
) : ThingMoving(
    gl, model, texture, time,
    velocity = Vector(WIND_SPEED * 1.5f, 0f, 0f),
) {

    init {
        scale = Vector(
            (3.5f + Random.nextFloat(0f, 2f)).let { if (Random.nextBoolean()) it else -it },
            3f,
            3.5f + Random.nextFloat(0f, 2f)
        )
    }

    override fun update() {
        super.update()

        val rangX = origin.y / 2 + (scale.x * 6).absoluteValue
        if (origin.x > rangX) {
            origin = origin.copy(x = Random.nextFloat((-rangX) - 5f, (-rangX) + 5f))
            timeElapsed = 0f
        }

        color.set(cloudsColor, color.a)

        if (timeElapsed < 2f) {
            val alpha = (timeElapsed * .5f).coerceIn(0f, 1f)

            color *= alpha
            color.a = alpha
        }
    }

    interface Factory<Type : ThingCloud> {
        fun create(which: Int): Type
    }

    companion object {
        @JvmStatic
        protected val MODELS = intArrayOf(
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m
        )
    }

}
