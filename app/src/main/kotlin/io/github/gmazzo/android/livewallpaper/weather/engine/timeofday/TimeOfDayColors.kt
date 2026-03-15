package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color

data class TimeOfDayColors(
    val sunrise: Color,
    val day: Color,
    val sunset: Color,
    val night: Color,
)

fun Resources.timeOfDayColors(
    @ColorRes sunrise: Int,
    @ColorRes day: Int,
    @ColorRes sunset: Int,
    @ColorRes night: Int,
) = TimeOfDayColors(
    Color(getColor(sunrise, null)),
    Color(getColor(day, null)),
    Color(getColor(sunset, null)),
    Color(getColor(night, null)),
)

operator fun TimeOfDayColors.get(color: TimeOfDay.TintColor) = when (color) {
    TimeOfDay.TintColor.SUNRISE -> sunrise
    TimeOfDay.TintColor.DAY -> day
    TimeOfDay.TintColor.SUNSET -> sunset
    TimeOfDay.TintColor.NIGHT -> night
}
