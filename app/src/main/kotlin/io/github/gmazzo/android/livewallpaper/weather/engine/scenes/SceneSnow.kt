package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticlesSnow
import javax.inject.Inject
import javax.inject.Provider

@SceneScoped
class SceneSnow @Inject constructor(
    dependencies: SceneDependencies,
    private val particle: Provider<ParticlesSnow>,
) : Scene(
    dependencies,
    background = R.drawable.bg2,
) {

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
