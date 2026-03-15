package io.github.gmazzo.android.livewallpaper.weather.engine

import android.graphics.Color
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

fun Color.withAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) =
    Color.valueOf( red(), green(), blue(), alpha)

fun Color.blendWith(
    other: Color,
    @FloatRange(from = 0.0, to = 1.0) percentage: Float
) = Color.valueOf(ColorUtils.blendARGB(toArgb(), other.toArgb(), percentage))

fun Color.toArray(into: FloatArray) =
    components.copyInto(into)
