package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay.Companion.GOLDER_HOUR_FACTOR
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_LIGHT1
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11

open class SceneClear @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    private val timeOfDay: TimeOfDay,
    timeOfDayTint: TimeOfDayTint,
) : Scene(time, gl, models, textures, things, timeOfDayTint) {

    open val backgroundId: Int = R.drawable.bg3

    override fun draw() {
        super.draw()

        gl.glDisable(GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL_LIGHT1)
        gl.glDisable(GL_LIGHTING)
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.elapsedSeconds)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)

        things.render()
        drawTree()
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
        gl.glBindTexture(GL_TEXTURE_2D, textures[backgroundId].glId)
        gl.glColor4f(timeOfDayTint.color.r, timeOfDayTint.color.g, timeOfDayTint.color.b, 1f)
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(bgPadding * 2.0f, bgPadding, bgPadding)
        gl.glMatrixMode(GL_TEXTURE)

        pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * timeDelta) * -0.005f) % 1.0f,
                0.0f,
                0.0f
            )
            val mesh = models[R.raw.plane_16x16]
            mesh.render()
            renderStars(timeDelta)
        }

        gl.glMatrixMode(GL_MODELVIEW)
    }

    private fun renderStars(timeDelta: Float) {
        val position = timeOfDay.sunPosition
        val alpha = ((-position + GOLDER_HOUR_FACTOR / 2) / GOLDER_HOUR_FACTOR / 2).coerceIn(0f, 1f)

        if (alpha > 0) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, alpha)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            val starMesh = models[R.raw.stars]
            val noise = textures[R.drawable.noise]
            val star = textures[R.drawable.stars]
            gl.glTranslatef((0.1f * timeDelta) % 1.0f, 300.0f, -100.0f)
            starMesh.renderFrameMultiTexture(noise, star, GL_MODULATE, false)
        }
    }

}
