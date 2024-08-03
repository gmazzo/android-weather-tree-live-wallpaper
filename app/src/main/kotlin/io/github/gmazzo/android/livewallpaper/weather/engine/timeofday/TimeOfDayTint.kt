package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import android.graphics.Color
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
        into.set(timeOfDay.tintMode(colors))
    }

}
