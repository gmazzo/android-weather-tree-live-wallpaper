package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

typealias TimeOfDayColors = Array<Int>

fun Resources.timeOfDayColors(
    @ColorRes sunrise: Int,
    @ColorRes day: Int,
    @ColorRes sunset: Int,
    @ColorRes night: Int,
) = arrayOf(
    getColor(sunrise, null),
    getColor(day, null),
    getColor(sunset, null),
    getColor(night, null),
)

@ColorInt
operator fun TimeOfDayColors.get(color: TimeOfDay.TintColor) = this[when (color) {
    TimeOfDay.TintColor.SUNRISE -> 0
    TimeOfDay.TintColor.DAY -> 1
    TimeOfDay.TintColor.SUNSET -> 2
    TimeOfDay.TintColor.NIGHT -> 3
}]
