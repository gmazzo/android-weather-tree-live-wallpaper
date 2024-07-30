package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneFog @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    @Named("timeOfDay") timeOfDayColor: EngineColor,
) : Scene(time, gl, models, textures, things, timeOfDayColor) {

    private val fogEngineColorFinal = EngineColor()

    private val fogTimeOfDayColors = arrayOf(
        EngineColor(0.2f, 0.2f, 0.2f, 1.0f),
        EngineColor(0.5f, 0.5f, 0.5f, 1.0f),
        EngineColor(0.8f, 0.8f, 0.8f, 1.0f),
        EngineColor(0.5f, 0.5f, 0.5f, 1.0f),
    )

    override fun updateTimeOfDay(tod: TimeOfDay) {
        super.updateTimeOfDay(tod)

        fogEngineColorFinal.blend(
            fogTimeOfDayColors[tod.mainIndex],
            fogTimeOfDayColors[tod.blendIndex],
            tod.blendAmount
        )
        fogEngineColorFinal.setToArray(FOG_COLOR)
    }

    override fun draw() {
        things.update()
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glEnable(GL10.GL_FOG)
        gl.glFogf(GL10.GL_FOG_MODE, GL10.GL_LINEAR.toFloat())
        gl.glFogfv(GL10.GL_FOG_COLOR, FOG_COLOR, 0)
        gl.glFogf(GL10.GL_FOG_DENSITY, FOG_DENSITY)
        gl.glFogf(GL10.GL_FOG_START, -10.0f)
        gl.glFogf(GL10.GL_FOG_END, 190.0f)
        gl.glFogf(GL10.GL_FOG_HINT, 4352.0f)
        renderBackground()
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        gl.glDisable(GL10.GL_FOG)

        things.render()
        drawTree()
    }

    private fun renderBackground() = gl.pushMatrix {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[R.drawable.bg1].glId)
        gl.glColor4f(
            timeOfDayColor.r,
            timeOfDayColor.g,
            timeOfDayColor.b,
            1.0f
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(bgPadding * 2.0f, bgPadding, bgPadding)
        gl.glMatrixMode(GL10.GL_TEXTURE)

        pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * time.deltaSeconds) * -0.005f) % 1.0f,
                0.0f,
                0.0f
            )
            val model = models[R.raw.plane_16x16]
            model.render()
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW)
    }

    companion object {
        private val FOG_COLOR: FloatArray = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f)
        private const val FOG_DENSITY = .2f
    }
}
