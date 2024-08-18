package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.graphics.Color
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.Wave
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesRain
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingLightning
import io.github.gmazzo.android.livewallpaper.weather.engine.withFlags
import javax.inject.Inject
import javax.inject.Provider
import javax.microedition.khronos.opengles.GL10.GL_AMBIENT
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_DIFFUSE
import javax.microedition.khronos.opengles.GL10.GL_LIGHT1
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_POSITION
import kotlin.time.Duration.Companion.milliseconds

class SceneStorm @Inject constructor(
    dependencies: SceneDependencies,
    private val particles: ParticlesRain,
    private val lightningProvider: Provider<ThingLightning>,
) : Scene(
    dependencies,
    background = R.drawable.storm_bg,
    backgroundTint = EngineColor(Color.WHITE),
) {

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
    private val wave = Wave(0.0, 500.0, 0.0, .005)

    override fun draw() {
        super.draw()

        timeOfDayTint.color.toArray(lightAmbientLight)
        if (random.nextFloat(0f, boltFrequency * .75f) < time.deltaSeconds) {
            spawnLightning()
        }
    }

    override fun drawBackground() {
        updateLightValues()

        if (lightFlashTime <= 0f) {
            gl.withFlags(GL_LIGHTING, GL_LIGHT1) {
                glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbientLight, 0)
                super.drawBackground()
            }

        } else {
            super.drawBackground()
        }

        renderRain()
    }

    private fun renderRain() = gl.pushMatrix(GL_MODELVIEW) {
        gl.glTranslatef(0f, 0f, -5f)

        particles.update(time.deltaSeconds)
        particles.render(particleRainOrigin)
    }

    private fun spawnLightning() {
        val lightning = lightningProvider.get()

        lightning.origin = Vector(
            random.nextFloat(-25f, 25f),
            random.nextFloat(95f, 168f),
            20f
        )
        if (random.nextInt(2) == 0) {
            lightning.scale = lightning.scale.let { it.copy(z = it.z * -1f) }
        }
        things.add(lightning)
        lightFlashTime = .25f
        lightFlashX = lightning.origin.x
    }

    private fun updateLightValues() {
        val timeDelta = time.deltaSeconds

        wave.timeElapsed += (timeDelta * 1000).toInt().milliseconds

        val lightPosX = wave.cos.toFloat()

        if (lightFlashTime <= 0f) {
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
