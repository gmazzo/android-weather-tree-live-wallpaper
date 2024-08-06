package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.graphics.Color
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesRain
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.withFlags
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_AMBIENT
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_DIFFUSE
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW

class SceneRain @Inject constructor(
    dependencies: SceneDependencies,
    private val particles: ParticlesRain,
) : Scene(
    dependencies,
    background = R.drawable.storm_bg,
    backgroundTint = EngineColor(Color.WHITE),
) {

    private val particleRainOrigin = Vector(0f, 25f, 10f)
    private val lightDiffuse = floatArrayOf(.1f, .1f, .1f, 1f)

    override fun draw() = gl.withFlags(GL_LIGHTING, GL_COLOR_BUFFER_BIT) {
        super.draw()

        timeOfDayTint.color.toArray(lightDiffuse)
        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_DIFFUSE, lightDiffuse, 0)
        gl.glLightfv(GL_COLOR_BUFFER_BIT, GL_AMBIENT, lightDiffuse, 0)
    }

    override fun drawBackground() {
        super.drawBackground()

        renderRain()
    }

    private fun renderRain() = gl.pushMatrix(GL_MODELVIEW) {
        gl.glTranslatef(0f, 0f, -5f)

        particles.update(time.deltaSeconds)
        particles.render(particleRainOrigin)
    }

}
