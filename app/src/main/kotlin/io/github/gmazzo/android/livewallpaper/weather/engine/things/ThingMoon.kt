package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.Location
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.shredzone.commons.suncalc.MoonIllumination
import org.shredzone.commons.suncalc.MoonPosition
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.math.min

class ThingMoon @Inject constructor(
    gl: GL11,
    models: Models,
    private val resources: Resources,
    private val clock: MutableStateFlow<Clock>,
    private val timeOfDay: TimeOfDay,
    private val location: StateFlow<Location?>,
) : Thing(
    gl,
    model = models[R.raw.plane_16x16],
    texture = resources.phases[0],
) {

    override lateinit var texture: Texture

    init {
        scale = Vector(2f)
    }

    override fun render() =
        super.render(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    override fun update() {
        super.update()

        val now = clock.value.time
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

        val phase = ((illumination.phase + 180) / 360 * resources.phases.size).toInt()

        texture = resources.phases[phase]

        val altitude = position * 175f
        color.a = (altitude / 25f).coerceIn(0f, 1f)
        origin = origin.copy(z = min(altitude - 80f, 0f))
    }

    class Resources @Inject constructor(
        textures: Textures,
    ) {
        val moon0 = textures[R.drawable.moon_0]
        val moon1 = textures[R.drawable.moon_1]
        val moon2 = textures[R.drawable.moon_2]
        val moon3 = textures[R.drawable.moon_3]
        val moon4 = textures[R.drawable.moon_4]
        val moon5 = textures[R.drawable.moon_5]
        val moon6 = textures[R.drawable.moon_6]
        val moon7 = textures[R.drawable.moon_7]
        val moon8 = textures[R.drawable.moon_8]
        val moon9 = textures[R.drawable.moon_9]
        val moon10 = textures[R.drawable.moon_10]
        val moon11 = textures[R.drawable.moon_11]

        val phases = arrayOf(
            moon0, moon1, moon2, moon3, moon4, moon5, moon6, moon7, moon8, moon9, moon10, moon11
        )
    }

}
