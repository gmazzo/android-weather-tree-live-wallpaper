package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.Wave
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesRain
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingLightning
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.inject.Inject
import javax.inject.Provider
import javax.microedition.khronos.opengles.GL10.GL_AMBIENT
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_DIFFUSE
import javax.microedition.khronos.opengles.GL10.GL_LIGHT1
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_POSITION
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_ZERO
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class SceneStorm @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    timeOfDayTint: TimeOfDayTint,
    private val particles: ParticlesRain,
    private val lightningProvider: Provider<ThingLightning>,
) : Scene(time, gl, models, textures, things, timeOfDayTint, darkClouds = true) {

    private val lightAmbientLight = FloatArray(4)
    private var lightFlashTime = 0f
    private var lightFlashX = 0f
    private val ambientLight = floatArrayOf(1f, 1f, 1f, 1f)
    private val flashColor: FloatArray = floatArrayOf(1f, 1f, 1f, 1f)
    private val position = floatArrayOf(0f, 0f, 0f, 1f)
    private val specularLight = floatArrayOf(.1f, .1f, .1f, 1f)
    private val particleRainOrigin = Vector(0f, 25f, 10f)
    private val boltFrequency = 5f
    private val diffuseLight = floatArrayOf(1.5f, 1.5f, 1.5f, 1f)
    private val flashLights = true
    private val lightAmbientLightColor = EngineColor(.5f, .5f, .5f, 1f)
    private val wave = Wave(0.0, 500.0, 0.0, .005)
    private val stormBg = textures[R.drawable.storm_bg]

    override fun unload() {
        super.unload()

        gl.glDisable(GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL_LIGHT1)
        gl.glDisable(GL_LIGHTING)
    }

    override fun draw() {
        super.draw()

        timeOfDayTint.update(lightAmbientLightColor)
        lightAmbientLightColor.setToArray(lightAmbientLight)

        gl.glMatrixMode(GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.elapsedSeconds)
        renderRain(time.deltaSeconds)
        checkForLightning(time.deltaSeconds)
        updateLightValues(time.deltaSeconds)
        gl.glTranslatef(0f, 0f, 40f)

        things.render()
        drawTree()
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
        gl.glBindTexture(GL_TEXTURE_2D, stormBg.glId)
        gl.glColor4f(timeOfDayTint.color.r, timeOfDayTint.color.g, timeOfDayTint.color.b, 1f)
        gl.glMatrixMode(GL_MODELVIEW)

        gl.glTranslatef(0f, 250f, 35f)
        gl.glScalef(bgPadding * 2f, bgPadding, bgPadding)
        gl.glMatrixMode(GL_TEXTURE)

        pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * timeDelta) * -.005f) % 1f,
                0f,
                0f
            )
            if (!flashLights || lightFlashTime <= 0f) {
                gl.glEnable(GL_LIGHTING)
                gl.glEnable(GL_LIGHT1)
                gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientLight, 0)
            }
            val mesh = models[R.raw.plane_16x16]
            mesh.render()
            gl.glDisable(GL_LIGHT1)
        }

        gl.glMatrixMode(GL_MODELVIEW)
    }

    private fun renderRain(timeDelta: Float) = gl.pushMatrix {
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glTranslatef(0f, 0f, -5f)
        gl.glColor4f(1f, 1f, 1f, 1f)
        particles.update(timeDelta)
        gl.glBlendFunc(GL_ONE, GL_ZERO)
        particles.render(particleRainOrigin)
    }

    private fun spawnLightning() {
        val lightning = lightningProvider.get()

        lightning.origin = Vector(
            Random.nextFloat(-25f, 25f),
            Random.nextFloat(95f, 168f),
            20f
        )
        if (Random.nextInt(2) == 0) {
            lightning.scale = lightning.scale.let { it.copy(z = it.z * -1f) }
        }
        things.add(lightning)
        lightFlashTime = .25f
        lightFlashX = lightning.origin.x
    }

    private fun checkForLightning(timeDelta: Float) {
        if (Random.nextFloat(0f, boltFrequency * .75f) < timeDelta) {
            spawnLightning()
        }
    }

    private fun updateLightValues(timeDelta: Float) {
        wave.timeElapsed += timeDelta.toLong()

        val lightPosX = wave.cos.toFloat()

        if (!flashLights || lightFlashTime <= 0f) {
            position[0] = lightPosX
            gl.glLightfv(GL_COLOR_BUFFER_BIT, 4610, specularLight, 0)

        } else {
            val flashRemaining = lightFlashTime / .25f
            position[0] =
                (lightFlashX * flashRemaining) + ((1f - flashRemaining) * lightPosX)

            gl.glLightfv(GL_COLOR_BUFFER_BIT, 4610, flashColor, 0)
            lightFlashTime -= timeDelta
        }

        position[1] = 50f
        position[2] = wave.sin.toFloat()

        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_AMBIENT, ambientLight, 0)
        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_DIFFUSE, diffuseLight, 0)
        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_POSITION, position, 0)
    }

}
