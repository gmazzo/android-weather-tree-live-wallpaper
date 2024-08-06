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

val Icons.TimeOfDay get() = TimeOfDayIcons

object TimeOfDayIcons {

    val Day by lazy {
        ImageVector.Builder(
            name = "clear_day",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 40f,
            viewportHeight = 40f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 7.958f)
                quadToRelative(-.542f, 0f, -.917f, -.375f)
                reflectiveQuadToRelative(-.375f, -.916f)
                verticalLineTo(3.42f)
                quadToRelative(0f, -.542f, .375f, -.938f)
                quadToRelative(.375f, -.396f, .917f, -.396f)
                reflectiveQuadToRelative(.938f, .396f)
                quadToRelative(.395f, .396f, .395f, .938f)
                verticalLineToRelative(3.625f)
                quadToRelative(0f, .541f, -.395f, .916f)
                quadToRelative(-.396f, .375f, -.938f, .375f)
                close()
                moveToRelative(8.5f, 3.542f)
                quadToRelative(-.417f, -.375f, -.396f, -.917f)
                quadToRelative(.021f, -.541f, .396f, -.916f)
                lineToRelative(2.542f, -2.584f)
                quadToRelative(.375f, -.375f, .916f, -.375f)
                quadToRelative(.542f, 0f, .959f, .375f)
                quadToRelative(.375f, .375f, .375f, .917f)
                reflectiveQuadToRelative(-.375f, .917f)
                lineTo(30.333f, 11.5f)
                quadToRelative(-.375f, .375f, -.916f, .375f)
                quadToRelative(-.542f, 0f, -.917f, -.375f)
                close()
                moveToRelative(4.833f, 9.792f)
                quadToRelative(-.541f, 0f, -.916f, -.375f)
                reflectiveQuadTo(32.42f, 20f)
                quadToRelative(0f, -.542f, .375f, -.938f)
                quadToRelative(.375f, -.395f, .916f, -.395f)
                horizontalLineToRelative(3.625f)
                quadToRelative(.542f, 0f, .938f, .395f)
                quadToRelative(.396f, .396f, .396f, .938f)
                quadToRelative(0f, .542f, -.396f, .917f)
                reflectiveQuadToRelative(-.938f, .375f)
                close()
                moveTo(20f, 38.25f)
                quadToRelative(-.542f, 0f, -.917f, -.375f)
                reflectiveQuadToRelative(-.375f, -.917f)
                verticalLineToRelative(-3.625f)
                quadToRelative(0f, -.541f, .375f, -.937f)
                reflectiveQuadTo(20f, 32f)
                quadToRelative(.542f, 0f, .938f, .396f)
                quadToRelative(.395f, .396f, .395f, .937f)
                verticalLineToRelative(3.625f)
                quadToRelative(0f, .542f, -.395f, .917f)
                quadToRelative(-.396f, .375f, -.938f, .375f)
                close()
                moveTo(9.667f, 11.5f)
                lineTo(7.83f, 8.958f)
                quadToRelative(-.375f, -.375f, -.375f, -.916f)
                quadToRelative(0f, -.542f, .417f, -.959f)
                quadToRelative(.375f, -.375f, .896f, -.375f)
                reflectiveQuadToRelative(.896f, .375f)
                lineTo(11.5f, 9.667f)
                quadToRelative(.375f, .375f, .396f, .916f)
                quadToRelative(.021f, .542f, -.396f, .917f)
                quadToRelative(-.417f, .375f, -.938f, .375f)
                quadToRelative(-.52f, 0f, -.895f, -.375f)
                close()
                moveToRelative(21.375f, 21.417f)
                lineTo(28.5f, 30.333f)
                quadToRelative(-.375f, -.375f, -.375f, -.916f)
                quadToRelative(0f, -.542f, .375f, -.917f)
                reflectiveQuadToRelative(.917f, -.375f)
                quadToRelative(.541f, 0f, .916f, .375f)
                lineToRelative(2.625f, 2.542f)
                quadToRelative(.375f, .375f, .375f, .916f)
                quadToRelative(0f, .542f, -.416f, .959f)
                quadToRelative(-.375f, .375f, -.917f, .395f)
                quadToRelative(-.542f, .021f, -.958f, -.395f)
                close()
                moveToRelative(-28f, -11.625f)
                quadToRelative(-.542f, 0f, -.917f, -.375f)
                reflectiveQuadTo(1.75f, 20f)
                quadToRelative(0f, -.542f, .375f, -.938f)
                quadToRelative(.375f, -.395f, .917f, -.395f)
                horizontalLineToRelative(3.625f)
                quadToRelative(.541f, 0f, .937f, .395f)
                quadTo(8f, 19.458f, 8f, 20f)
                quadToRelative(0f, .542f, -.396f, .917f)
                reflectiveQuadToRelative(-.937f, .375f)
                close()
                moveToRelative(4.41f, 11.625f)
                quadToRelative(-.375f, -.375f, -.375f, -.917f)
                reflectiveQuadToRelative(.375f, -.917f)
                lineTo(9.667f, 28.5f)
                quadToRelative(.375f, -.375f, .916f, -.375f)
                quadToRelative(.542f, 0f, .917f, .375f)
                quadToRelative(.375f, .417f, .375f, .938f)
                quadToRelative(0f, .52f, -.375f, .937f)
                lineToRelative(-2.542f, 2.542f)
                quadToRelative(-.375f, .375f, -.937f, .375f)
                quadToRelative(-.563f, 0f, -.938f, -.375f)
                close()
                moveToRelative(12.917f, -3f)
                quadToRelative(-4.125f, 0f, -7.21f, -2.896f)
                reflectiveQuadTo(10.83f, 20f)
                quadToRelative(0f, -4.125f, 2.896f, -7.42f)
                quadToRelative(2.896f, -2.916f, 7.21f, -2.916f)
                reflectiveQuadToRelative(7.42f, 2.916f)
                quadToRelative(2.916f, 2.917f, 2.916f, 7.42f)
                reflectiveQuadToRelative(-2.916f, 7.21f)
                quadTo(24.125f, 29.917f, 20f, 29.917f)
                close()
            }
        }.build()
    }

    val Night by lazy {
        ImageVector.Builder(
            name = "clear_night",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 40f,
            viewportHeight = 40f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 33.625f)

                moveToRelative(0f, 2.625f)
                quadToRelative(-3.5f, 0f, -6.5f, -1.229f)
                reflectiveQuadToRelative(-5.208f, -3.417f)
                quadToRelative(-2.209f, -2.187f, -3.48f, -5.146f)
                quadTo(3.542f, 23.5f, 3.542f, 20f)
                quadToRelative(0f, -6.83f, 3.854f, -10.625f)
                reflectiveQuadToRelative(9.479f, -5.625f)
                quadToRelative(1.83f, -.208f, 1.604f, .479f)
                quadToRelative(.521f, .688f, .146f, 1.854f)
                quadToRelative(-1.125f, 3.5f, -.75f, 7.84f)
                quadToRelative(.375f, 3.583f, 2.63f, 6.645f)
                quadToRelative(1.687f, 3.63f, 4.562f, 5.271f)
                quadToRelative(2.875f, 2.209f, 6.75f, 2.917f)
                quadToRelative(1.167f, .25f, 1.479f, 1f)
                quadToRelative(.313f, .75f, -.396f, 1.542f)
                quadToRelative(-2.291f, 2.583f, -5.541f, 4.146f)
                quadTo(23.542f, 36.25f, 20f, 36.25f)
                close()
            }
        }.build()
    }

}
