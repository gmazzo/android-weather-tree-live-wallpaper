package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.annotation.ColorInt

data class TimeOfDayColors(
    @get:ColorInt val night: Int,
    @get:ColorInt val dawn: Int,
    @get:ColorInt val day: Int,
    @get:ColorInt val dusk: Int,
)
