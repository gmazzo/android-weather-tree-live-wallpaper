package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesSnow
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneSnow @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    @Named("timeOfDay") timeOfDayColor: EngineColor,
    particle: Provider<ParticlesSnow>,
) : Scene(gl, models, textures, things, timeOfDayColor) {

    private val snowPositions = arrayOf(
        Vector(0f, 20f, -20f),
        Vector(8f, 15f, -20f),
        Vector(-8f, 10f, -20f)
    )
    private val particles = snowPositions.map { particle.get() }

    override fun draw(time: GlobalTime) {
        things.update(time.sTimeDelta)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.sTimeElapsed)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)

        things.render()
        renderSnow(time.sTimeDelta)
        drawTree(time.sTimeDelta)
    }

    private fun renderSnow(timeDelta: Float) = particles.forEachIndexed { i, it ->
        it.update(timeDelta)
        it.render(snowPositions[i])
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[R.drawable.bg2].glId)
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
