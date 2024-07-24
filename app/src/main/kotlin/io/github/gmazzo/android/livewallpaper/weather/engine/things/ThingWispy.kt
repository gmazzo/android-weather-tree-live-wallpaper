package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase.Companion.todEngineColorFinal
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10

class ThingWispy(
    models: Models,
    textures: Textures,
    which: Int,
) : SimpleThing(models, textures, R.raw.plane_16x16, WISPY_TEXTURES[which % WISPY_TEXTURES.size]) {

    override val engineColor: EngineColor? = null

    override fun render(gl: GL10) {
        val todEngineColor: EngineColor = todEngineColorFinal!!
        gl.glColor4f(
            todEngineColor.r,
            todEngineColor.g,
            todEngineColor.b,
            (todEngineColor.r + todEngineColor.g) + (todEngineColor.b / 3.0f)
        )
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        super.render(gl)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)

        if (origin.x > 123.75f) {
            val vector = this.origin
            vector.x -= 247.5f
        }
    }

    companion object {
        private val WISPY_TEXTURES = intArrayOf(R.raw.wispy1, R.raw.wispy2, R.raw.wispy3)
    }

}
