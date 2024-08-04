package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.DrawableRes
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import org.shredzone.commons.suncalc.MoonPhase
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.math.min

class ThingMoon @Inject constructor(
    gl: GL11,
    models: Models,
    private val textures: Textures,
    private val time: GlobalTime,
    private val timeOfDay: TimeOfDay,
) : Thing(gl, models[R.raw.plane_16x16], textures[PHASES[0]]) {

    override lateinit var texture: Texture

    private var recalculateAt: ZonedDateTime? = null

    private lateinit var phases: List<Phase>

    override fun render() =
        super.render(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    override fun update() {
        super.update()

        val now = time.time.value

        if (recalculateAt == null || now >= recalculateAt) {
            recalculateAt = now + Duration.ofDays(1)

            val builder = MoonPhase.compute().on(now).plusDays(-2 * 29 / PHASES.size)
            phases = PHASES.mapIndexed { i, textureId ->
                val phase = builder.phase(360.0 / PHASES.size * i).execute()
                val scale = when {
                    phase.isSuperMoon -> SIZE_SUPER_MOON
                    phase.isMicroMoon -> SIZE_MICRO_MOON
                    else -> SIZE_DEFAULT_MOON
                }

                Phase(phase.time, scale, textureId)
            }.sortedBy { it.time }
        }

        val phase = phases.first { it.time <= now }

        texture = textures[phase.textureId]
        scale = Vector(phase.scale)

        val altitude = timeOfDay.moonPosition * 175f
        color.a = (altitude / 25f).coerceIn(0f, 1f)
        origin = origin.copy(z = min(altitude - 80f, 0f))
    }

    private data class Phase(
        val time: ZonedDateTime,
        val scale: Float,
        @get:DrawableRes val textureId: Int
    )

    companion object {
        private const val SIZE_DEFAULT_MOON = 2f
        private const val SIZE_MICRO_MOON = 1.25f
        private const val SIZE_SUPER_MOON = 2.75f

        private val PHASES = intArrayOf(
            R.drawable.moon_0, R.drawable.moon_1, R.drawable.moon_2, R.drawable.moon_3,
            R.drawable.moon_4, R.drawable.moon_5, R.drawable.moon_6, R.drawable.moon_7,
            R.drawable.moon_8, R.drawable.moon_9, R.drawable.moon_10, R.drawable.moon_11
        )

    }

}
