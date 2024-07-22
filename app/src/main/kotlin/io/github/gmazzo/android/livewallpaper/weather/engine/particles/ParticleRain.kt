package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class ParticleRain(density: Int) : ParticleSystem() {
    init {
        this.spawnRate = 1.0f / (density.toFloat())
        this.spawnRateVariance = 0.05f
        this.spawnRangeX = 15.0f
        this.spawnRangeY = 5.0f
        this.spawnRangeZ = 0.0f
    }

    override fun render(gl: GL11, systemOrigin: Vector?) {
        if (model == null) {
            model = Scene.Companion.sModels!!.get(R.raw.rain)
            texture = Scene.Companion.sTextures!!.get(R.drawable.raindrop)
        }

        super.render(gl, systemOrigin)
    }

    override fun particleSetup(particle: Particle?) {
        super.particleSetup(particle)
        particle!!.lifetime = 1.0f
        val startScale = GlobalRand.floatRange(1.0f, 1.5f)
        particle.startScale.set(startScale, startScale, startScale)
        particle.destScale.set(startScale, startScale, startScale)
        val velocity = GlobalRand.floatRange(0.95f, 1.05f)
        particle.startVelocity.set(8.0f, 0.0f, -15.0f)
        particle.destVelocity.set(9.45f * velocity, 0.0f, -35.0f * velocity)
    }

    public override fun renderEnd(gl: GL10?) {
    }

    public override fun renderStart(gl: GL10) {
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }
}
