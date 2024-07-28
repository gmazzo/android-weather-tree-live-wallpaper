package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.SkyManager
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.roundToInt

class ThingMoon @Inject constructor(
    gl: GL11,
    models: Models,
    private val textures: Textures,
    @Named("sunPosition") private val todSunPosition: MutableStateFlow<Float>,
) : Thing(gl, models, R.raw.plane_16x16) {

    override val engineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)

    override var texture: Texture = reload

    private var phase = 0

    override fun render() {
        if (texture === reload) {
            texture = textures[PHASES[phase]]
        }
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        super.render()
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val position: Float = todSunPosition.value
        if (position >= 0.0f) {
            scale.set(0.0f)

        } else {
            scale.set(2.0f)
            val altitude = position * -175.0f
            var alpha = altitude / 25.0f
            if (alpha > 1.0f) {
                alpha = 1.0f
            }
            engineColor.a = alpha
            origin.z = altitude - 80.0f
            if (origin.z > 0.0f) {
                origin.z = 0.0f
            }
        }

        val currentPhase = phase
        this.phase = ((SkyManager.getMoonPhase() * PHASES.size).roundToInt()) % PHASES.size
        if (currentPhase != phase) {
            texture = reload
        }
    }

    companion object {
        private val reload = Texture(-1, -1)

        private val PHASES = intArrayOf(
            R.drawable.moon_0, R.drawable.moon_1, R.drawable.moon_2, R.drawable.moon_3,
            R.drawable.moon_4, R.drawable.moon_5, R.drawable.moon_6, R.drawable.moon_7,
            R.drawable.moon_8, R.drawable.moon_9, R.drawable.moon_10, R.drawable.moon_11
        )

    }

}
