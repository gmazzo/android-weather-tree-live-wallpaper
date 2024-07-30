package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ParticlesRain @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
) : Particles(gl, models[R.raw.rain], textures[R.drawable.raindrop]) {

    init {
        this.spawnRate = 1.0f / RAIN_DENSITY
        this.spawnRateVariance = 0.05f
        this.spawnRangeX = 15.0f
        this.spawnRangeY = 5.0f
        this.spawnRangeZ = 0.0f
    }

    override fun particleSetup(particle: Particle?) {
        super.particleSetup(particle)
        particle!!.lifetime = 1.0f

        val startScale = Random.nextFloat(1.0f, 1.5f)
        particle.startScale = Vector(startScale)
        particle.destScale = Vector(startScale)

        val velocity = Random.nextFloat(0.95f, 1.05f)
        particle.startVelocity = Vector(8.0f, 0.0f, -15.0f)
        particle.destVelocity = Vector(9.45f * velocity, 0.0f, -35.0f * velocity)
    }

    public override fun renderEnd(gl: GL10?) {
    }

    public override fun renderStart(gl: GL10) {
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    companion object {
        const val RAIN_DENSITY = 10f
    }

}
