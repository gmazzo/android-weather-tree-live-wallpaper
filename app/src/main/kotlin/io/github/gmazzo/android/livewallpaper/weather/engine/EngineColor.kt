package io.github.gmazzo.android.livewallpaper.weather.engine

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.blendWith(
    other: Color,
    @FloatRange(from = 0.0, to = 1.0) percentage: Float
) = Color(ColorUtils.blendARGB(toArgb(), other.toArgb(), percentage))

fun Color.toArray(into: FloatArray) {
    check(into.size == 4) { "into must be a float[4]" }

    into[0] = red
    into[1] = green
    into[2] = blue
    into[3] = alpha
}
