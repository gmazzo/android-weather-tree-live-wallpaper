package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesSnow
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneSnow @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    timeOfDayTint: TimeOfDayTint,
    particle: ParticlesSnow.Factory,
) : Scene(time, gl, models, textures, things, timeOfDayTint) {

    private val snowPositions = arrayOf(
        Vector(0f, 20f, -20f),
        Vector(8f, 15f, -20f),
        Vector(-8f, 10f, -20f)
    )

    private val particles =
        snowPositions.map { particle.create(timeOfDayTint.color) }

    override fun draw() {
        super.draw()

        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.elapsedSeconds)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)

        things.render()

        renderSnow()
        drawTree()
    }

    private fun renderSnow() = particles.forEachIndexed { i, it ->
        it.update(time.deltaSeconds)
        it.render(snowPositions[i])
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[R.drawable.bg2].glId)
        gl.glColor4f(timeOfDayTint.color.r, timeOfDayTint.color.g, timeOfDayTint.color.b, 1f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)

        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(bgPadding * 2.0f, bgPadding, bgPadding)
        gl.glMatrixMode(GL10.GL_TEXTURE)

        gl.pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * timeDelta) * -0.005f) % 1.0f,
                0.0f,
                0.0f
            )
            val mesh = models[R.raw.plane_16x16]
            mesh.render()
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW)
    }

}
