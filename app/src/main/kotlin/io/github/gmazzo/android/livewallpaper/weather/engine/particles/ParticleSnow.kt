package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherRenderer
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneSnow
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10

class ParticleSnow @Inject constructor(
    models: Models,
    textures: Textures,
) : ParticleSystem(models[R.raw.flakes], textures[R.raw.p_snow1]) {

    init {
        this.spawnRate = 0.25f
        this.spawnRateVariance = 0.05f
        startEngineColor.set(1.0f, 1.0f, 1.0f, 3.0f)
        destEngineColor.set(1.0f, 1.0f, 1.0f, 0.0f)
        this.spawnRangeX = 20.0f
    }

    override fun particleSetup(particle: Particle?) {
        super.particleSetup(particle)
        val bias: Float = ((WeatherRenderer.Companion.homeOffsetPercentage * 2.0f) - 1.0f) * 4.0f
        particle!!.lifetime = 4.5f
        particle.startScale.set(GlobalRand.floatRange(0.15f, 0.3f))
        particle.destScale.set(GlobalRand.floatRange(0.15f, 0.3f))
        val randX1: Float =
            (GlobalRand.floatRange(-6.0f, 6.0f) * SceneSnow.Companion.pref_snowNoise) + bias
        val randX2: Float =
            (GlobalRand.floatRange(-8.0f, 8.0f) * SceneSnow.Companion.pref_snowNoise) + bias
        val randY1 = GlobalRand.floatRange(-2.0f, 2.0f)
        val randY2 = GlobalRand.floatRange(-2.0f, 2.0f)
        val randZ =
            GlobalRand.floatRange(-(3.0f + (SceneSnow.Companion.pref_snowGravity * 1.5f)), -3.0f)
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
            SceneBase.todEngineColorFinal!!.r,
            SceneBase.todEngineColorFinal!!.g,
            SceneBase.todEngineColorFinal!!.b,
            3.0f
        )
        destEngineColor.set(
            SceneBase.todEngineColorFinal!!.r,
            SceneBase.todEngineColorFinal!!.g,
            SceneBase.todEngineColorFinal!!.b,
            0.0f
        )
    }
}