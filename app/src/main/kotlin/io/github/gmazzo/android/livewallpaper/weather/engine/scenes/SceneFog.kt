package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.res.Resources
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayColors
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_FOG
import javax.microedition.khronos.opengles.GL10.GL_FOG_COLOR
import javax.microedition.khronos.opengles.GL10.GL_FOG_DENSITY
import javax.microedition.khronos.opengles.GL10.GL_FOG_END
import javax.microedition.khronos.opengles.GL10.GL_FOG_HINT
import javax.microedition.khronos.opengles.GL10.GL_FOG_MODE
import javax.microedition.khronos.opengles.GL10.GL_FOG_START
import javax.microedition.khronos.opengles.GL10.GL_LINEAR
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11

class SceneFog @Inject constructor(
    resources: Resources,
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    timeOfDayTint: TimeOfDayTint,
) : Scene(time, gl, models, textures, things, timeOfDayTint) {

    private val fogEngineColorFinal =
        EngineColor()

    private val fogTimeOfDayColors = TimeOfDayColors(
        night = resources.getColor(R.color.timeOfDay_fog_night, null),
        dawn = resources.getColor(R.color.timeOfDay_fog_dawn, null),
        day = resources.getColor(R.color.timeOfDay_fog_day, null),
        dusk = resources.getColor(R.color.timeOfDay_fog_dusk, null),
    )

    private val fogColors =
        floatArrayOf(.8f, .8f, .8f, 1f)

    override fun draw() {
        super.draw()

        timeOfDayTint.update(fogEngineColorFinal, fogTimeOfDayColors)
        fogEngineColorFinal.setToArray(fogColors)

        gl.glMatrixMode(GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glEnable(GL_FOG)
        gl.glFogf(GL_FOG_MODE, GL_LINEAR.toFloat())
        gl.glFogfv(GL_FOG_COLOR, fogColors, 0)
        gl.glFogf(GL_FOG_DENSITY, FOG_DENSITY)
        gl.glFogf(GL_FOG_START, -10f)
        gl.glFogf(GL_FOG_END, 190f)
        gl.glFogf(GL_FOG_HINT, 4352f)
        renderBackground()
        gl.glTranslatef(0f, 0f, 40f)

        gl.glDisable(GL_FOG)
        things.render()

        gl.glEnable(GL_FOG)
        drawTree()
        gl.glDisable(GL_FOG)
    }

    private fun renderBackground() = gl.pushMatrix {
        gl.glBindTexture(GL_TEXTURE_2D, textures[R.drawable.bg1].glId)
        gl.glColor4f(timeOfDayTint.color.r, timeOfDayTint.color.g, timeOfDayTint.color.b, 1f)
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glTranslatef(0f, 250f, 35f)
        gl.glScalef(bgPadding * 2f, bgPadding, bgPadding)
        gl.glMatrixMode(GL_TEXTURE)

        pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * time.deltaSeconds) * -.005f) % 1f,
                0f,
                0f
            )
            val model = models[R.raw.plane_16x16]
            model.render()
        }

        gl.glMatrixMode(GL_MODELVIEW)
    }

    companion object {
        private const val FOG_DENSITY = .2f
    }
}
