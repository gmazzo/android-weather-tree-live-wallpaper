package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime.Companion.waveCos
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleRain
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingLightning
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class SceneStorm @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    @Named("timeOfDay") timeOfDayColor: EngineColor,
    private val particles: ParticleRain,
    private val lightningProvider: Provider<ThingLightning>,
) : Scene(gl, models, textures, things, timeOfDayColor, raining = true, darkClouds = true) {
    private val lightAmbientLight = FloatArray(4)
    private var lightFlashTime = 0f
    private var lightFlashX = 0f
    private val ambientLight = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    private val flashColor: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    private val position = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    private val specularLight = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
    private val particleRainOrigin = Vector(0.0f, 25.0f, 10.0f)
    private val boltFrequency = 5f
    private val diffuseLight = floatArrayOf(1.5f, 1.5f, 1.5f, 1.0f)
    private val flashLights = true
    private val lightAmbientLightColor = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
    private val stormBg = textures[R.drawable.storm_bg]

    override fun unload() {
        super.unload()

        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        lightAmbientLightColor.blend(
            timeOfDayColors[tod.mainIndex],
            timeOfDayColors[tod.blendIndex],
            tod.blendAmount
        )
    }

    override fun draw(time: GlobalTime) {
        things.update(time.sTimeDelta)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.sTimeElapsed)
        renderRain(time.sTimeDelta)
        checkForLightning(time.sTimeDelta)
        updateLightValues(time.sTimeDelta)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)

        things.render()
        drawTree(time.sTimeDelta)
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
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

        pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * timeDelta) * -0.005f) % 1.0f,
                0.0f,
                0.0f
            )
            if (!flashLights || lightFlashTime <= 0.0f) {
                gl.glEnable(GL10.GL_LIGHTING)
                gl.glEnable(GL10.GL_LIGHT1)
                lightAmbientLight[0] = lightAmbientLightColor.r
                lightAmbientLight[1] = lightAmbientLightColor.g
                lightAmbientLight[2] = lightAmbientLightColor.b
                lightAmbientLight[3] = lightAmbientLightColor.a
                gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientLight, 0)
            }
            val mesh = models[R.raw.plane_16x16]
            mesh.render()
            gl.glDisable(GL10.GL_LIGHT1)
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

    private fun spawnLightning() {
        val lightning = lightningProvider.get()

        lightning.origin.set(
            Random.nextFloat(-25.0f, 25.0f),
            Random.nextFloat(95.0f, 168.0f),
            20.0f
        )
        if (Random.nextInt(2) == 0) {
            lightning.scale.z *= -1.0f
        }
        things.add(lightning)
        lightFlashTime = 0.25f
        lightFlashX = lightning.origin.x
    }

    private fun checkForLightning(timeDelta: Float) {
        if (Random.nextFloat(0.0f, boltFrequency * 0.75f) < timeDelta) {
            spawnLightning()
        }
    }

    private fun updateLightValues(timeDelta: Float) {
        val lightPosX: Float = waveCos(0.0f, 500.0f, 0.0f, 0.005f)

        if (!flashLights || lightFlashTime <= 0.0f) {
            position[0] = lightPosX
            gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4610, specularLight, 0)

        } else {
            val flashRemaining = lightFlashTime / 0.25f
            position[0] =
                (lightFlashX * flashRemaining) + ((1.0f - flashRemaining) * lightPosX)

            gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4610, flashColor, 0)
            lightFlashTime -= timeDelta
        }

        position[1] = 50.0f
        position[2] = GlobalTime.Companion.waveSin(0.0f, 500.0f, 0.0f, 0.005f)

        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4608, ambientLight, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4609, diffuseLight, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4611, position, 0)
    }

}
