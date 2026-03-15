package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.compose.ui.graphics.Color
import io.github.gmazzo.android.livewallpaper.weather.engine.blendWith
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneScoped
import javax.inject.Inject

@SceneScoped
class TimeOfDayTint @Inject constructor(
    private val timeOfDay: TimeOfDay,
    private val timeOfDayColors: TimeOfDayColors,
) {

    var color = Color.White

    fun update() = with(timeOfDay.tintSpec) {
        color = timeOfDayColors[main].blendWith(timeOfDayColors[blend], amount)
    }

}
