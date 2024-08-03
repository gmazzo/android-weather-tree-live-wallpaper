package io.github.gmazzo.android.livewallpaper.weather.engine.timeofday

import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

sealed interface TimeOfDayTintMode : (TimeOfDayColors) -> Int {

    data object Day : TimeOfDayTintMode, (TimeOfDayColors) -> Int by TimeOfDayColors::day

    data object Night : TimeOfDayTintMode, (TimeOfDayColors) -> Int by TimeOfDayColors::night

    data class Blend(
        val main: (TimeOfDayColors) -> Int,
        val secondary: (TimeOfDayColors) -> Int,
        @FloatRange(from = 0.0, to = 1.0) val amount: Float,
    ) : TimeOfDayTintMode {

        init {
            check(amount in 0f..1f) { "Invalid blend amount: $amount" }
        }

        override fun invoke(colors: TimeOfDayColors) =
            ColorUtils.blendARGB(main(colors), secondary(colors), amount)

    }

}
