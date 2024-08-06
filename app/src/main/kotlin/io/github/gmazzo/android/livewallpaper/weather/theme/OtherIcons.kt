@file:Suppress("UnusedReceiverParameter")

package io.github.gmazzo.android.livewallpaper.weather.theme

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Speed by lazy {
    ImageVector.Builder(
        name = "speed",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 40.0f,
        viewportHeight = 40.0f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(17.542f, 25.75f)
            quadToRelative(1f, 0.958f, 2.541f, 0.958f)
            quadToRelative(1.542f, 0f, 2.292f, -1.125f)
            lineToRelative(7.25f, -10.916f)
            quadToRelative(0.292f, -0.459f, -0.083f, -0.834f)
            quadToRelative(-0.375f, -0.375f, -0.834f, -0.083f)
            lineToRelative(-10.916f, 7.292f)
            quadToRelative(-1.084f, 0.708f, -1.146f, 2.229f)
            quadToRelative(-0.063f, 1.521f, 0.896f, 2.479f)
            close()
            moveToRelative(-9.167f, 7.333f)
            quadToRelative(-0.75f, 0f, -1.437f, -0.333f)
            quadToRelative(-0.688f, -0.333f, -1.063f, -1.042f)
            quadToRelative(-1.042f, -1.875f, -1.604f, -3.937f)
            quadToRelative(-0.563f, -2.063f, -0.563f, -4.354f)
            quadTo(3.708f, 20f, 4.979f, 17f)
            reflectiveQuadToRelative(3.5f, -5.229f)
            quadToRelative(2.229f, -2.229f, 5.208f, -3.521f)
            quadToRelative(2.98f, -1.292f, 6.355f, -1.292f)
            quadToRelative(3.375f, 0f, 6.333f, 1.292f)
            reflectiveQuadToRelative(5.167f, 3.521f)
            quadTo(33.75f, 14f, 35.042f, 17f)
            quadToRelative(1.291f, 3f, 1.291f, 6.417f)
            quadToRelative(0f, 2.291f, -0.521f, 4.354f)
            quadToRelative(-0.52f, 2.062f, -1.645f, 3.937f)
            quadToRelative(-0.459f, 0.875f, -1.063f, 1.125f)
            quadToRelative(-0.604f, 0.25f, -1.437f, 0.25f)
            close()
        }
    }.build()

}
