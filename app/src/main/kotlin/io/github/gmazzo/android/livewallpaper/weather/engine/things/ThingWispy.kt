package io.github.gmazzo.android.livewallpaper.weather.engine.things

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class ThingWispy @AssistedInject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    @Named("timeOfDay") private val timeOfDayColor: EngineColor,
    @Assisted which: Int,
) : ThingSimple(gl, models, textures, R.raw.plane_16x16, WISPY_TEXTURES[which % WISPY_TEXTURES.size]) {

    override val engineColor: EngineColor? = null

    override fun render() {
        gl.glColor4f(
            timeOfDayColor.r,
            timeOfDayColor.g,
            timeOfDayColor.b,
            (timeOfDayColor.r + timeOfDayColor.g) + (timeOfDayColor.b / 3.0f)
        )
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        super.render()
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)

        if (origin.x > 123.75f) {
            origin.x -= 247.5f
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
