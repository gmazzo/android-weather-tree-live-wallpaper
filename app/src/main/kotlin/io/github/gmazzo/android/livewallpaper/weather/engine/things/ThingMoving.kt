package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import javax.microedition.khronos.opengles.GL11

sealed class ThingMoving(
    gl: GL11,
    model: Model,
    texture: Texture,
    protected val time: GlobalTime,
    private val velocity: Vector = Vector(WIND_SPEED * 1.5f, 0f, 0f),
) : Thing(gl, model, texture) {

    protected var timeElapsed = 0f

    @CallSuper
    override fun update() {
        super.update()
        val timeDelta = time.deltaSeconds

        timeElapsed += timeDelta
        origin += velocity * timeDelta
    }

}
