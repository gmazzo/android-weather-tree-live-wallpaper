package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ParticlesSnow @Inject constructor(
    random: Random,
    gl: GL11,
    models: Models,
    textures: Textures,
    private val timeOfDayTint: TimeOfDayTint,
    @param:Named("homeOffset") private val homeOffset: MutableStateFlow<Float>,
) : Particles(
    random, gl,
    model = models[R.raw.flakes],
    texture = textures[if (random.nextBoolean()) R.raw.p_snow1 else R.raw.p_snow2],
    spawnRate = .25f,
    spawnRateVariance = .05f,
    spawnRangeX = 20f,
    translucent = true,
) {

    override fun particleSetup(particle: Particle) {
        super.particleSetup(particle)

        val bias: Float = ((homeOffset.value * 2f) - 1f) * 4f
        particle.lifetime = 4.5f
        particle.startScale = Vector(random.nextFloat(.15f, .3f))
        particle.destScale = Vector(random.nextFloat(.15f, .3f))

        val randZ = random.nextFloat(-(3f + (SNOW_GRAVITY * 1.5f)), -3f)
        particle.startVelocity = Vector(
            x = (random.nextFloat(-6f, 6f) * SNOW_NOISE) + bias,
            y = random.nextFloat(-2f, 2f),
            z = randZ
        )
        particle.destVelocity = Vector(
            x = (random.nextFloat(-8f, 8f) * SNOW_NOISE) + bias,
            y = random.nextFloat(-2f, 2f),
            z = randZ
        )
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)

        val color = timeOfDayTint.color
        startEngineColor.set(color, 1f)
        destEngineColor.set(color, 0f)
    }

    companion object {
        private const val SNOW_NOISE = 7 * .1f
        private const val SNOW_GRAVITY = 2 * .5f
    }

}
