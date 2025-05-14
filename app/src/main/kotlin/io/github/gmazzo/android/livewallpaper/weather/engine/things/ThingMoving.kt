package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.microedition.khronos.opengles.GL11

sealed class ThingMoving(
    gl: GL11,
    model: Model,
    texture: Texture,
    @Named("real") protected val clock: MutableStateFlow<Clock>,
    private val velocity: Vector,
) : Thing(gl, model, texture) {

    protected var timeElapsed = 0f

    @CallSuper
    override fun update() {
        super.update()
        val timeDelta = clock.value.deltaSeconds

        timeElapsed += timeDelta
        origin += velocity * timeDelta
    }

}
