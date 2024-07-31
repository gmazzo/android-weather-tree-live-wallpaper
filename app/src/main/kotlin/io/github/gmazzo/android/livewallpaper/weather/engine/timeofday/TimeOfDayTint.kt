package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneScoped
import javax.inject.Inject

@SceneScoped
class TimeOfDayTint @Inject constructor(
    private val timeOfDay: TimeOfDay,
    private val timeOfDayColors: TimeOfDayColors,
) {

    val color = EngineColor().set(Color.WHITE)

    fun update(
        into: EngineColor = color,
        colors: TimeOfDayColors = timeOfDayColors,
    ) {
        color.set(Color.WHITE)

        val main = timeOfDay.tintMainColor(colors)
        val blend = timeOfDay.tintBlendColor(colors)
        val amount = timeOfDay.tintBlendAmount

        into.set(ColorUtils.blendARGB(main, blend, amount))
    }

}
