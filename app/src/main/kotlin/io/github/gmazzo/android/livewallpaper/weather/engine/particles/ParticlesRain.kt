package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ParticlesRain @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
) : Particles(
    gl, models[R.raw.rain], textures[R.drawable.raindrop],
    spawnRate = 1f / RAIN_DENSITY,
    spawnRateVariance = .05f,
    spawnRangeX = 15f,
    spawnRangeY = 5f,
    spawnRangeZ = 0f,
) {

    override fun particleSetup(particle: Particle) {
        super.particleSetup(particle)
        particle.lifetime = 1.0f

        val startScale = Random.nextFloat(1.0f, 1.5f)
        particle.startScale = Vector(startScale)
        particle.destScale = Vector(startScale)

        val velocity = Random.nextFloat(0.95f, 1.05f)
        particle.startVelocity = Vector(8.0f, 0.0f, -15.0f)
        particle.destVelocity = Vector(9.45f * velocity, 0.0f, -35.0f * velocity)
    }

    companion object {
        const val RAIN_DENSITY = 10f
    }

}
