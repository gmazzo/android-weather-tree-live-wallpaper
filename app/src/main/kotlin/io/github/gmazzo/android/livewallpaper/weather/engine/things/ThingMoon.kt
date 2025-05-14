package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.Location
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import kotlinx.coroutines.flow.StateFlow
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPosition
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.math.min

class ThingMoon @Inject constructor(
    gl: GL11,
    models: Models,
    private val textures: Textures,
    @Named("forPreview") private val time: GlobalTime,
    private val timeOfDay: TimeOfDay,
    private val location: StateFlow<Location?>,
) : Thing(
    gl,
    model = models[R.raw.plane_16x16],
    texture = textures[PHASES[0]],
) {

    override lateinit var texture: Texture

    init {
        scale = Vector(2f)
    }

    override fun render() =
        super.render(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    override fun update() {
        super.update()

        val now = time.time.value
        val location = location.value

        val position = if (location != null) {
            MoonPosition.compute()
                .on(now)
                .at(location.latitude, location.longitude)
                .execute()
                .altitude.toFloat() / 90f

        } else {
            -timeOfDay.sunPosition
        }

        val illumination = MoonIllumination.compute()
            .on(now)
            .apply { if (location != null) at(location.latitude, location.longitude) }
            .execute()

        val phase = ((illumination.phase + 180) / 360 * PHASES.size).toInt()

        texture = textures[PHASES[phase]]

        val altitude = position * 175f
        color.a = (altitude / 25f).coerceIn(0f, 1f)
        origin = origin.copy(z = min(altitude - 80f, 0f))
    }

    companion object {
        private val PHASES = intArrayOf(
            R.drawable.moon_0, R.drawable.moon_1, R.drawable.moon_2, R.drawable.moon_3,
            R.drawable.moon_4, R.drawable.moon_5, R.drawable.moon_6, R.drawable.moon_7,
            R.drawable.moon_8, R.drawable.moon_9, R.drawable.moon_10, R.drawable.moon_11
        )
    }

}
