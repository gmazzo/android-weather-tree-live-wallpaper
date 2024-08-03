package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class ThingCloud(
    gl: GL11,
    model: Model,
    texture: Texture,
    time: GlobalTime,
    private val color: EngineColor,
) : ThingMoving(gl, model, texture, time) {

    override val engineColor = EngineColor(1f, 1f, 1f, 1f)

    init {
        scale = Vector(
            (3.5f + Random.nextFloat(0f, 2f)).let { if (Random.nextBoolean()) it else -it },
            3f,
            3.5f + Random.nextFloat(0f, 2f)
        )
    }

    override fun render() {
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        super.render()
    }

    override fun update() {
        super.update()

        val rangX = origin.y / 2 + (scale.x * 6).absoluteValue
        if (origin.x > rangX) {
            origin = origin.copy(x = Random.nextFloat((-rangX) - 5f, (-rangX) + 5f))
            timeElapsed = 0f
        }

        engineColor.set(color)

        if (timeElapsed < 2f) {
            val alpha = timeElapsed * .5f

            engineColor *= alpha
            engineColor.a = alpha
        }
    }

}
