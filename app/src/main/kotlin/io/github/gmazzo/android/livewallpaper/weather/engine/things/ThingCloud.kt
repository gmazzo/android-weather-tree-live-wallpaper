package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.microedition.khronos.opengles.GL11
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class ThingCloud(
    protected val random: Random,
    gl: GL11,
    model: Model,
    texture: Texture,
    clock: MutableStateFlow<Clock>,
    private val cloudsColor: EngineColor,
) : ThingMoving(
    gl, model, texture, clock,
    velocity = Vector(WIND_SPEED * 1.5f, 0f, 0f),
) {

    init {
        scale = Vector(
            (3.5f + random.nextFloat(0f, 2f)).let { if (random.nextBoolean()) it else -it },
            3f,
            3.5f + random.nextFloat(0f, 2f)
        )
    }

    override fun update() {
        super.update()

        val rangX = origin.y / 2 + (scale.x * 6).absoluteValue
        if (origin.x > rangX) {
            origin = origin.copy(x = random.nextFloat((-rangX) - 5f, (-rangX) + 5f))
            timeElapsed = 0f
        }

        color.set(cloudsColor, color.a)

        if (timeElapsed < 2f) {
            val alpha = (timeElapsed * .5f).coerceIn(0f, 1f)

            color *= alpha
            color.a = alpha
        }
    }

    fun interface Factory<Type : ThingCloud> {
        fun create(which: Int): Type
    }

    abstract class Resources(models: Models) {
        val cloud1m = models[R.raw.cloud1m]
        val cloud2m = models[R.raw.cloud2m]
        val cloud3m = models[R.raw.cloud3m]
        val cloud4m = models[R.raw.cloud4m]
        val cloud5m = models[R.raw.cloud5m]

        val models = arrayOf(cloud1m, cloud2m, cloud3m, cloud4m, cloud5m)
    }

}
