package io.github.gmazzo.android.livewallpaper.weather.engine.things

import android.graphics.Color
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingWispy @AssistedInject constructor(
    random: Random,
    gl: GL11,
    models: Models,
    textures: Textures,
    @Named("real") clock: MutableStateFlow<Clock>,
    @Named("clouds") private val cloudsColor: EngineColor,
    @Assisted which: Int,
) : ThingMoving(
    gl,
    model = models[R.raw.plane_16x16],
    texture = textures[WISPY_TEXTURES[which % WISPY_TEXTURES.size]],
    clock,
    velocity = Vector(WIND_SPEED / 2, 0f, 0f),
) {

    init {
        foreground = true
        scale = Vector(
            x = random.nextFloat(1f, 3f),
            y = 1f,
            z = random.nextFloat(1f, 1.5f)
        )
    }

    override fun render() =
        super.render(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    override fun update() {
        super.update()

        color.set(Color.WHITE).a = ((cloudsColor.r + cloudsColor.g + cloudsColor.b) / 3).coerceIn(.2f, 1f)

        if (origin.x > 123.75f) {
            origin = origin.let { it.copy(x = it.x - 247.5f) }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(which: Int): ThingWispy
    }

    companion object {
        private val WISPY_TEXTURES = intArrayOf(R.raw.wispy1, R.raw.wispy2, R.raw.wispy3)
    }

}
