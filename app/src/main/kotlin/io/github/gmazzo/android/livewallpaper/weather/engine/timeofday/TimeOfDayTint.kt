package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.core.graphics.ColorUtils
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneScoped
import javax.inject.Inject

@SceneScoped
class TimeOfDayTint @Inject constructor(
    private val timeOfDay: TimeOfDay,
    private val timeOfDayColors: TimeOfDayColors,
) {

    val color = EngineColor()

    fun update() = with(timeOfDay.tintSpec) {
        color.set(ColorUtils.blendARGB(timeOfDayColors[main], timeOfDayColors[blend], amount))
    }

}
