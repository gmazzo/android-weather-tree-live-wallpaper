package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleRain
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneRain @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    @Named("timeOfDay") timeOfDayColor: EngineColor,
    private val particles: ParticleRain,
) : Scene(gl, models, textures, things, timeOfDayColor, raining = true) {
    private val particleRainOrigin= Vector(0.0f, 25.0f, 10.0f)
    private val lightDiffuse = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
    private val lightDiffuseColor = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)

    override fun updateTimeOfDay(tod: TimeOfDay) {
        lightDiffuseColor.blend(
            timeOfDayColors[tod.mainIndex],
            timeOfDayColors[tod.blendIndex],
            tod.blendAmount
        )
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
        val stormBg = textures[R.drawable.storm_bg]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, stormBg.glId)
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

    private fun renderRain(timeDelta: Float) = gl.pushMatrix {
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glTranslatef(0.0f, 0.0f, -5.0f)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        particles.update(timeDelta)
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO)
        particles.render(particleRainOrigin)
    }

    override fun draw(time: GlobalTime) {
        things.update(time.sTimeDelta)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        lightDiffuse[0] = lightDiffuseColor.r
        lightDiffuse[1] = lightDiffuseColor.g
        lightDiffuse[2] = lightDiffuseColor.b
        lightDiffuse[3] = lightDiffuseColor.a
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4609, this.lightDiffuse, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4608, this.lightDiffuse, 0)
        renderBackground(time.sTimeElapsed)
        renderRain(time.sTimeDelta)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHTING)

        things.render()
        drawTree(time.sTimeDelta)
    }

}
