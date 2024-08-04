package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesSnow
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.inject.Inject
import javax.inject.Provider
import javax.microedition.khronos.opengles.GL11

class SceneSnow @Inject constructor(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    timeOfDay: TimeOfDay,
    timeOfDayTint: TimeOfDayTint,
    private val particle: Provider<ParticlesSnow>,
) : Scene(time, gl, models, textures, things, timeOfDay, timeOfDayTint, R.drawable.bg2) {

    private val snowPositions = arrayOf(
        Vector(0f, 20f, -20f),
        Vector(8f, 15f, -20f),
        Vector(-8f, 10f, -20f)
    )

    private val particles =
        snowPositions.map { particle.get() }

    override fun drawForeground() {
        super.drawForeground()

        renderSnow()
    }

    private fun renderSnow() = particles.forEachIndexed { i, it ->
        it.update(time.deltaSeconds)
        it.render(snowPositions[i])
    }

}
