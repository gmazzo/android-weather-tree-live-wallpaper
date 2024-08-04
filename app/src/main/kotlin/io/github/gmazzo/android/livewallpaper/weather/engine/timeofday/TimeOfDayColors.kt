package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

typealias TimeOfDayColors = Array<Int>

fun Resources.timeOfDayColors(
    @ColorRes sunrise: Int,
    @ColorRes midday: Int,
    @ColorRes noon: Int,
    @ColorRes sunset: Int,
) = arrayOf(
    getColor(sunrise, null),
    getColor(midday, null),
    getColor(noon, null),
    getColor(sunset, null),
)

@ColorInt
operator fun TimeOfDayColors.get(color: TimeOfDay.TintColor) = this[when (color) {
    TimeOfDay.TintColor.SUNRISE -> 0
    TimeOfDay.TintColor.MIDDAY -> 1
    TimeOfDay.TintColor.NOON -> 2
    TimeOfDay.TintColor.SUNSET -> 3
}]
