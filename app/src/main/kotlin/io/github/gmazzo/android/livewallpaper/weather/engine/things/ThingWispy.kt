package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingWispy @AssistedInject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    private val timeOfDayTint: TimeOfDayTint,
    @Assisted which: Int,
) : Thing(gl, models[R.raw.plane_16x16], textures[WISPY_TEXTURES[which % WISPY_TEXTURES.size]]) {

    override val engineColor: EngineColor? = null

    init {
        scale = Vector(
            x = Random.nextFloat(1f, 3f),
            y = 1f,
            z = Random.nextFloat(1f, 1.5f)
        )
        origin = Vector(
            x = 0f,
            y = Random.nextFloat(87.5f, 175f),
            z = Random.nextFloat(-40f, -20f),
        )
    }

    override fun render() {
        gl.glColor4f(
            timeOfDayTint.color.r,
            timeOfDayTint.color.g,
            timeOfDayTint.color.b,
            (timeOfDayTint.color.r + timeOfDayTint.color.g) + (timeOfDayTint.color.b / 3f)
        )
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        super.render()
    }

    override fun update() {
        super.update()

        if (origin.x > 123.75f) {
            origin = origin.let { it.copy(x = it.x - 247.5f) }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(which: Int): ThingWispy
    }

    companion object {
        private val WISPY_TEXTURES = intArrayOf(R.raw.wispy1, R.raw.wispy2, R.raw.wispy3)
    }

}
