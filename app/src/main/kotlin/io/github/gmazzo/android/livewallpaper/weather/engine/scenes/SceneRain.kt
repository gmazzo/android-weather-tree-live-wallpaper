package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesRain
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_AMBIENT
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_DIFFUSE
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11

class SceneRain @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    timeOfDayTint: TimeOfDayTint,
    private val particles: ParticlesRain,
) : Scene(time, gl, models, textures, things, timeOfDayTint) {

    private val particleRainOrigin= Vector(0f, 25f, 10f)
    private val lightDiffuse = floatArrayOf(.1f, .1f, .1f, 1f)
    private val lightDiffuseColor = EngineColor(.5f, .5f, .5f, 1f)

    private fun renderBackground() = gl.pushMatrix {
        val stormBg = textures[R.drawable.storm_bg]

        gl.glBindTexture(GL_TEXTURE_2D, stormBg.glId)
        gl.glColor4f(timeOfDayTint.color.r, timeOfDayTint.color.g, timeOfDayTint.color.b, 1f)
        gl.glMatrixMode(GL_MODELVIEW)

        gl.glTranslatef(0f, 250f, 35f)
        gl.glScalef(bgPadding * 2f, bgPadding, bgPadding)
        gl.glMatrixMode(GL_TEXTURE)

        gl.pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * time.deltaSeconds) * -.005f) % 1f,
                0f,
                0f
            )
            val mesh = models[R.raw.plane_16x16]
            mesh.render()
        }

        gl.glMatrixMode(GL_MODELVIEW)
    }

    private fun renderRain() = gl.pushMatrix {
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glTranslatef(0f, 0f, -5f)
        gl.glColor4f(1f, 1f, 1f, 1f)
        particles.update(time.deltaSeconds)
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO)
        particles.render(particleRainOrigin)
    }

    override fun draw() {
        super.draw()

        timeOfDayTint.update(lightDiffuseColor)
        lightDiffuseColor.setToArray(lightDiffuse)

        gl.glEnable(GL_LIGHTING)
        gl.glEnable(GL_COLOR_BUFFER_BIT)
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_DIFFUSE, lightDiffuse, 0)
        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_AMBIENT, lightDiffuse, 0)
        renderBackground()
        renderRain()
        gl.glTranslatef(0f, 0f, 40f)
        gl.glDisable(GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL_LIGHTING)

        things.render()
        drawTree()
    }

}
