package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherViewRenderer.Companion.homeOffsetPercentage
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ParticlesSnow @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    @Named("timeOfDay") private val timeOfDayColor: EngineColor,
) : Particles(
    gl,
    models[R.raw.flakes],
    textures[if (Random.nextBoolean()) R.raw.p_snow1 else R.raw.p_snow2],
) {

    init {
        this.spawnRate = 0.25f
        this.spawnRateVariance = 0.05f
        startEngineColor.set(1.0f, 1.0f, 1.0f, 3.0f)
        destEngineColor.set(1.0f, 1.0f, 1.0f, 0.0f)
        this.spawnRangeX = 20.0f
    }

    override fun particleSetup(particle: Particle?) {
        super.particleSetup(particle)
        val bias: Float = ((homeOffsetPercentage * 2.0f) - 1.0f) * 4.0f
        particle!!.lifetime = 4.5f
        particle.startScale.set(Random.nextFloat(0.15f, 0.3f))
        particle.destScale.set(Random.nextFloat(0.15f, 0.3f))
        val randX1: Float =
            (Random.nextFloat(-6.0f, 6.0f) * SNOW_NOISE) + bias
        val randX2: Float =
            (Random.nextFloat(-8.0f, 8.0f) * SNOW_NOISE) + bias
        val randY1 = Random.nextFloat(-2.0f, 2.0f)
        val randY2 = Random.nextFloat(-2.0f, 2.0f)
        val randZ =
            Random.nextFloat(-(3.0f + (SNOW_GRAVITY * 1.5f)), -3.0f)
        particle.startVelocity.set(randX1, randY1, randZ)
        particle.destVelocity.set(randX2, randY2, randZ)
    }

    public override fun renderEnd(gl: GL10?) {
    }

    public override fun renderStart(gl: GL10) {
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        startEngineColor.set(
            timeOfDayColor.r,
            timeOfDayColor.g,
            timeOfDayColor.b,
            3.0f
        )
        destEngineColor.set(
            timeOfDayColor.r,
            timeOfDayColor.g,
            timeOfDayColor.b,
            0.0f
        )
    }

    companion object {
        private const val SNOW_NOISE = 7 * 0.1f
        private const val SNOW_GRAVITY = 2 * 0.5f
    }

}
