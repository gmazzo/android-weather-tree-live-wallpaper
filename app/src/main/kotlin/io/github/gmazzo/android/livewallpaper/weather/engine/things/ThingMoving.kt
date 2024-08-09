package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.StaticModel
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL11

sealed class ThingMoving(
    gl: GL11,
    model: StaticModel,
    texture: Texture,
    protected val time: GlobalTime,
    private val velocity: Vector,
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
