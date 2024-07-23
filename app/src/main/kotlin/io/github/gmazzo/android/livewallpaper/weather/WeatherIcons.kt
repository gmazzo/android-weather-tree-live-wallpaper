package io.github.gmazzo.android.livewallpaper.weather

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object WeatherIcons {

    val sunny: ImageVector by lazy {
        ImageVector.Builder(
            name = "sunny",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(20f, 7.958f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.916f)
                verticalLineTo(3.042f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                reflectiveQuadToRelative(0.938f, 0.396f)
                quadToRelative(0.395f, 0.396f, 0.395f, 0.938f)
                verticalLineToRelative(3.625f)
                quadToRelative(0f, 0.541f, -0.395f, 0.916f)
                quadToRelative(-0.396f, 0.375f, -0.938f, 0.375f)
                close()
                moveToRelative(8.5f, 3.542f)
                quadToRelative(-0.417f, -0.375f, -0.396f, -0.917f)
                quadToRelative(0.021f, -0.541f, 0.396f, -0.916f)
                lineToRelative(2.542f, -2.584f)
                quadToRelative(0.375f, -0.375f, 0.916f, -0.375f)
                quadToRelative(0.542f, 0f, 0.959f, 0.375f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
                reflectiveQuadToRelative(-0.375f, 0.917f)
                lineTo(30.333f, 11.5f)
                quadToRelative(-0.375f, 0.375f, -0.916f, 0.375f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                close()
                moveToRelative(4.833f, 9.792f)
                quadToRelative(-0.541f, 0f, -0.916f, -0.375f)
                reflectiveQuadTo(32.042f, 20f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.395f, 0.916f, -0.395f)
                horizontalLineToRelative(3.625f)
                quadToRelative(0.542f, 0f, 0.938f, 0.395f)
                quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                reflectiveQuadToRelative(-0.938f, 0.375f)
                close()
                moveTo(20f, 38.25f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.917f)
                verticalLineToRelative(-3.625f)
                quadToRelative(0f, -0.541f, 0.375f, -0.937f)
                reflectiveQuadTo(20f, 32f)
                quadToRelative(0.542f, 0f, 0.938f, 0.396f)
                quadToRelative(0.395f, 0.396f, 0.395f, 0.937f)
                verticalLineToRelative(3.625f)
                quadToRelative(0f, 0.542f, -0.395f, 0.917f)
                quadToRelative(-0.396f, 0.375f, -0.938f, 0.375f)
                close()
                moveTo(9.667f, 11.5f)
                lineTo(7.083f, 8.958f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.916f)
                quadToRelative(0f, -0.542f, 0.417f, -0.959f)
                quadToRelative(0.375f, -0.375f, 0.896f, -0.375f)
                reflectiveQuadToRelative(0.896f, 0.375f)
                lineTo(11.5f, 9.667f)
                quadToRelative(0.375f, 0.375f, 0.396f, 0.916f)
                quadToRelative(0.021f, 0.542f, -0.396f, 0.917f)
                quadToRelative(-0.417f, 0.375f, -0.938f, 0.375f)
                quadToRelative(-0.52f, 0f, -0.895f, -0.375f)
                close()
                moveToRelative(21.375f, 21.417f)
                lineTo(28.5f, 30.333f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.916f)
                quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                reflectiveQuadToRelative(0.917f, -0.375f)
                quadToRelative(0.541f, 0f, 0.916f, 0.375f)
                lineToRelative(2.625f, 2.542f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.916f)
                quadToRelative(0f, 0.542f, -0.416f, 0.959f)
                quadToRelative(-0.375f, 0.375f, -0.917f, 0.395f)
                quadToRelative(-0.542f, 0.021f, -0.958f, -0.395f)
                close()
                moveToRelative(-28f, -11.625f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                reflectiveQuadTo(1.75f, 20f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.395f, 0.917f, -0.395f)
                horizontalLineToRelative(3.625f)
                quadToRelative(0.541f, 0f, 0.937f, 0.395f)
                quadTo(8f, 19.458f, 8f, 20f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                reflectiveQuadToRelative(-0.937f, 0.375f)
                close()
                moveToRelative(4.041f, 11.625f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.917f)
                reflectiveQuadToRelative(0.375f, -0.917f)
                lineTo(9.667f, 28.5f)
                quadToRelative(0.375f, -0.375f, 0.916f, -0.375f)
                quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                quadToRelative(0.375f, 0.417f, 0.375f, 0.938f)
                quadToRelative(0f, 0.52f, -0.375f, 0.937f)
                lineToRelative(-2.542f, 2.542f)
                quadToRelative(-0.375f, 0.375f, -0.937f, 0.375f)
                quadToRelative(-0.563f, 0f, -0.938f, -0.375f)
                close()
                moveToRelative(12.917f, -3f)
                quadToRelative(-4.125f, 0f, -7.021f, -2.896f)
                reflectiveQuadTo(10.083f, 20f)
                quadToRelative(0f, -4.125f, 2.896f, -7.042f)
                quadToRelative(2.896f, -2.916f, 7.021f, -2.916f)
                reflectiveQuadToRelative(7.042f, 2.916f)
                quadToRelative(2.916f, 2.917f, 2.916f, 7.042f)
                reflectiveQuadToRelative(-2.916f, 7.021f)
                quadTo(24.125f, 29.917f, 20f, 29.917f)
                close()
                moveToRelative(0f, -2.625f)
                quadToRelative(3.042f, 0f, 5.167f, -2.125f)
                reflectiveQuadTo(27.292f, 20f)
                quadToRelative(0f, -3.042f, -2.125f, -5.167f)
                reflectiveQuadTo(20f, 12.708f)
                quadToRelative(-3.042f, 0f, -5.167f, 2.125f)
                reflectiveQuadTo(12.708f, 20f)
                quadToRelative(0f, 3.042f, 2.125f, 5.167f)
                reflectiveQuadTo(20f, 27.292f)
                close()
            }
        }.build()
    }

    val cloudy by lazy {
        ImageVector.Builder(
            name = "partly_cloudy_day",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(20f, 7.958f)
                quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.916f)
                verticalLineTo(3.042f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                reflectiveQuadToRelative(0.938f, 0.396f)
                quadToRelative(0.395f, 0.396f, 0.395f, 0.938f)
                verticalLineToRelative(3.625f)
                quadToRelative(0f, 0.541f, -0.395f, 0.916f)
                quadToRelative(-0.396f, 0.375f, -0.938f, 0.375f)
                close()
                moveToRelative(8.5f, 3.542f)
                quadToRelative(-0.375f, -0.417f, -0.375f, -0.937f)
                quadToRelative(0f, -0.521f, 0.375f, -0.938f)
                lineToRelative(2.542f, -2.542f)
                quadToRelative(0.416f, -0.416f, 0.937f, -0.395f)
                quadToRelative(0.521f, 0.02f, 0.938f, 0.395f)
                quadToRelative(0.375f, 0.417f, 0.375f, 0.938f)
                quadToRelative(0f, 0.521f, -0.375f, 0.896f)
                lineTo(30.333f, 11.5f)
                quadToRelative(-0.416f, 0.375f, -0.916f, 0.375f)
                reflectiveQuadTo(28.5f, 11.5f)
                close()
                moveToRelative(4.833f, 9.792f)
                quadToRelative(-0.541f, 0f, -0.916f, -0.375f)
                reflectiveQuadTo(32.042f, 20f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.395f, 0.916f, -0.395f)
                horizontalLineToRelative(3.625f)
                quadToRelative(0.542f, 0f, 0.938f, 0.395f)
                quadToRelative(0.396f, 0.396f, 0.396f, 0.938f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                reflectiveQuadToRelative(-0.938f, 0.375f)
                close()
                moveToRelative(-2.291f, 11.625f)
                lineToRelative(-2.5f, -2.542f)
                quadToRelative(-0.417f, -0.417f, -0.417f, -0.937f)
                quadToRelative(0f, -0.521f, 0.417f, -0.938f)
                quadToRelative(0.375f, -0.375f, 0.916f, -0.375f)
                quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                lineToRelative(2.542f, 2.542f)
                quadToRelative(0.458f, 0.416f, 0.437f, 0.937f)
                quadToRelative(-0.021f, 0.521f, -0.437f, 0.938f)
                quadToRelative(-0.375f, 0.375f, -0.896f, 0.395f)
                quadToRelative(-0.521f, 0.021f, -0.979f, -0.395f)
                close()
                moveTo(9.667f, 11.5f)
                lineTo(7.083f, 8.917f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.896f)
                reflectiveQuadToRelative(0.375f, -0.938f)
                quadTo(7.5f, 6.708f, 8f, 6.688f)
                quadToRelative(0.5f, -0.021f, 0.958f, 0.395f)
                lineTo(11.5f, 9.667f)
                quadToRelative(0.375f, 0.416f, 0.375f, 0.916f)
                reflectiveQuadToRelative(-0.375f, 0.917f)
                quadToRelative(-0.417f, 0.375f, -0.917f, 0.375f)
                reflectiveQuadToRelative(-0.916f, -0.375f)
                close()
                moveTo(10f, 34.917f)
                quadToRelative(-3.458f, 0f, -5.854f, -2.396f)
                reflectiveQuadTo(1.75f, 26.667f)
                quadToRelative(0f, -3.417f, 2.417f, -5.855f)
                quadTo(6.583f, 18.375f, 10f, 18.375f)
                horizontalLineTo(10.208f)
                quadToRelative(0.584f, -3.583f, 3.334f, -5.958f)
                reflectiveQuadTo(20f, 10.042f)
                quadToRelative(4.125f, 0f, 7.042f, 2.916f)
                quadToRelative(2.916f, 2.917f, 2.916f, 7.042f)
                quadToRelative(0f, 3.167f, -1.854f, 5.75f)
                reflectiveQuadToRelative(-4.896f, 3.625f)
                quadToRelative(-0.166f, 2.167f, -1.833f, 3.854f)
                quadToRelative(-1.667f, 1.688f, -3.875f, 1.688f)
                close()
                moveToRelative(0f, -2.625f)
                horizontalLineToRelative(7.5f)
                quadToRelative(1.375f, 0f, 2.25f, -0.875f)
                reflectiveQuadToRelative(0.875f, -2.25f)
                quadToRelative(0f, -1.375f, -0.875f, -2.271f)
                quadTo(18.875f, 26f, 17.542f, 26f)
                horizontalLineToRelative(-1.875f)
                lineToRelative(-0.75f, -1.75f)
                quadToRelative(-0.625f, -1.5f, -1.959f, -2.354f)
                quadToRelative(-1.333f, -0.854f, -2.958f, -0.854f)
                quadToRelative(-2.292f, 0f, -3.958f, 1.666f)
                quadToRelative(-1.667f, 1.667f, -1.667f, 3.959f)
                quadToRelative(0f, 2.375f, 1.625f, 4f)
                reflectiveQuadToRelative(4f, 1.625f)
                close()
                moveToRelative(12.708f, -5.542f)
                quadToRelative(2.084f, -0.833f, 3.334f, -2.688f)
                quadToRelative(1.25f, -1.854f, 1.25f, -4.062f)
                quadToRelative(0f, -3.042f, -2.125f, -5.167f)
                reflectiveQuadTo(20f, 12.708f)
                quadToRelative(-2.667f, 0f, -4.729f, 1.75f)
                quadToRelative(-2.063f, 1.75f, -2.438f, 4.417f)
                quadToRelative(1.542f, 0.708f, 2.729f, 1.854f)
                quadToRelative(1.188f, 1.146f, 1.813f, 2.646f)
                quadToRelative(1.667f, 0f, 3.125f, 0.979f)
                reflectiveQuadToRelative(2.208f, 2.396f)
                close()
            }
        }.build()
    }

    val foggy by lazy {
        ImageVector.Builder(
            name = "foggy",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(30f, 31.458f)
                quadToRelative(-0.625f, 0f, -1.042f, -0.416f)
                quadToRelative(-0.416f, -0.417f, -0.416f, -1.042f)
                reflectiveQuadToRelative(0.416f, -1.042f)
                quadToRelative(0.417f, -0.416f, 1.042f, -0.416f)
                reflectiveQuadToRelative(1.042f, 0.416f)
                quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
                reflectiveQuadToRelative(-0.416f, 1.042f)
                quadToRelative(-0.417f, 0.416f, -1.042f, 0.416f)
                close()
                moveToRelative(-18.333f, 5.167f)
                quadToRelative(-0.625f, 0f, -1.042f, -0.437f)
                quadToRelative(-0.417f, -0.438f, -0.417f, -1.063f)
                quadToRelative(0f, -0.583f, 0.417f, -1.021f)
                quadToRelative(0.417f, -0.437f, 1.042f, -0.437f)
                reflectiveQuadToRelative(1.041f, 0.416f)
                quadToRelative(0.417f, 0.417f, 0.417f, 1.042f)
                reflectiveQuadToRelative(-0.417f, 1.063f)
                quadToRelative(-0.416f, 0.437f, -1.041f, 0.437f)
                close()
                moveTo(10f, 31.458f)
                quadToRelative(-0.625f, 0f, -1.042f, -0.416f)
                quadToRelative(-0.416f, -0.417f, -0.416f, -1.042f)
                reflectiveQuadToRelative(0.416f, -1.042f)
                quadToRelative(0.417f, -0.416f, 1.042f, -0.416f)
                horizontalLineToRelative(15f)
                quadToRelative(0.625f, 0f, 1.042f, 0.416f)
                quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
                reflectiveQuadToRelative(-0.416f, 1.042f)
                quadToRelative(-0.417f, 0.416f, -1.042f, 0.416f)
                close()
                moveToRelative(6.667f, 5.167f)
                quadToRelative(-0.625f, 0f, -1.042f, -0.437f)
                quadToRelative(-0.417f, -0.438f, -0.417f, -1.063f)
                quadToRelative(0f, -0.583f, 0.417f, -1.021f)
                quadToRelative(0.417f, -0.437f, 1.042f, -0.437f)
                horizontalLineToRelative(11.666f)
                quadToRelative(0.625f, 0f, 1.042f, 0.416f)
                quadToRelative(0.417f, 0.417f, 0.417f, 1.042f)
                reflectiveQuadToRelative(-0.417f, 1.063f)
                quadToRelative(-0.417f, 0.437f, -1.042f, 0.437f)
                close()
                moveToRelative(-4.459f, -10.583f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
                quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
                quadToRelative(0f, -3.291f, 2.312f, -5.875f)
                quadTo(8.042f, 8.75f, 11.5f, 8.458f)
                quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
                quadTo(17.292f, 3.375f, 20f, 3.375f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, 0.167f, 5.084f, 2.25f)
                quadToRelative(1.958f, 2.083f, 1.958f, 4.875f)
                quadToRelative(0f, 2.958f, -2.104f, 5.063f)
                quadToRelative(-2.104f, 2.104f, -5.063f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.313f)
                quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.063f, -4.959f)
                quadTo(22.917f, 6.042f, 20f, 6.042f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.083f)
                lineToRelative(-0.292f, 0.792f)
                horizontalLineToRelative(-1.125f)
                quadToRelative(-2.542f, 0.083f, -4.313f, 1.875f)
                quadToRelative(-1.77f, 1.791f, -1.77f, 4.291f)
                quadToRelative(0f, 2.584f, 1.812f, 4.375f)
                quadToRelative(1.813f, 1.792f, 4.354f, 1.792f)
                close()
                moveTo(20f, 14.708f)
                close()
            }
        }.build()
    }

    val rainy by lazy {
        ImageVector.Builder(
            name = "rainy",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(23.208f, 36.458f)
                quadToRelative(-0.458f, 0.25f, -1f, 0.063f)
                quadToRelative(-0.541f, -0.188f, -0.791f, -0.646f)
                lineToRelative(-2.75f, -5.5f)
                quadToRelative(-0.25f, -0.5f, -0.084f, -1.042f)
                quadToRelative(0.167f, -0.541f, 0.667f, -0.791f)
                quadToRelative(0.5f, -0.209f, 1.021f, -0.042f)
                quadToRelative(0.521f, 0.167f, 0.771f, 0.667f)
                lineToRelative(2.75f, 5.5f)
                quadToRelative(0.25f, 0.5f, 0.083f, 1.021f)
                quadToRelative(-0.167f, 0.52f, -0.667f, 0.77f)
                close()
                moveToRelative(10f, 0f)
                quadToRelative(-0.458f, 0.209f, -1f, 0.042f)
                quadToRelative(-0.541f, -0.167f, -0.791f, -0.667f)
                lineToRelative(-2.75f, -5.5f)
                quadToRelative(-0.25f, -0.5f, -0.084f, -1.021f)
                quadToRelative(0.167f, -0.52f, 0.667f, -0.77f)
                reflectiveQuadToRelative(1.021f, -0.063f)
                quadToRelative(0.521f, 0.188f, 0.771f, 0.646f)
                lineToRelative(2.75f, 5.5f)
                quadToRelative(0.25f, 0.5f, 0.083f, 1.042f)
                quadToRelative(-0.167f, 0.541f, -0.667f, 0.791f)
                close()
                moveToRelative(-20f, 0f)
                quadToRelative(-0.458f, 0.209f, -1f, 0.042f)
                quadToRelative(-0.541f, -0.167f, -0.791f, -0.625f)
                lineToRelative(-2.75f, -5.5f)
                quadToRelative(-0.25f, -0.5f, -0.063f, -1.042f)
                quadToRelative(0.188f, -0.541f, 0.688f, -0.791f)
                quadToRelative(0.458f, -0.209f, 1f, -0.042f)
                quadToRelative(0.541f, 0.167f, 0.791f, 0.625f)
                lineToRelative(2.75f, 5.542f)
                quadToRelative(0.25f, 0.5f, 0.063f, 1.021f)
                quadToRelative(-0.188f, 0.52f, -0.688f, 0.77f)
                close()
                moveToRelative(-1f, -10.416f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
                quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
                quadToRelative(0f, -3.291f, 2.312f, -5.875f)
                quadTo(8.042f, 8.75f, 11.5f, 8.458f)
                quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
                quadTo(17.292f, 3.375f, 20f, 3.375f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, 0.167f, 5.084f, 2.25f)
                quadToRelative(1.958f, 2.083f, 1.958f, 4.875f)
                quadToRelative(0f, 2.958f, -2.104f, 5.063f)
                quadToRelative(-2.104f, 2.104f, -5.063f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.313f)
                quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.063f, -4.959f)
                quadTo(22.917f, 6.042f, 20f, 6.042f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.083f)
                lineToRelative(-0.334f, 0.792f)
                horizontalLineToRelative(-1.083f)
                quadToRelative(-2.542f, 0.083f, -4.313f, 1.875f)
                quadToRelative(-1.77f, 1.791f, -1.77f, 4.291f)
                quadToRelative(0f, 2.584f, 1.812f, 4.375f)
                quadToRelative(1.813f, 1.792f, 4.354f, 1.792f)
                close()
                moveTo(20f, 14.708f)
                close()
            }
        }.build()
    }

    val snowy by lazy {
        ImageVector.Builder(
            name = "weather_snowy",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(10.833f, 31.292f)
                quadToRelative(-0.708f, 0f, -1.208f, -0.5f)
                reflectiveQuadToRelative(-0.5f, -1.209f)
                quadToRelative(0f, -0.708f, 0.5f, -1.229f)
                quadToRelative(0.5f, -0.521f, 1.208f, -0.521f)
                quadToRelative(0.709f, 0f, 1.229f, 0.521f)
                quadToRelative(0.521f, 0.521f, 0.521f, 1.229f)
                quadToRelative(0f, 0.709f, -0.521f, 1.209f)
                quadToRelative(-0.52f, 0.5f, -1.229f, 0.5f)
                close()
                moveToRelative(5f, 5.291f)
                quadToRelative(-0.708f, 0f, -1.208f, -0.5f)
                reflectiveQuadToRelative(-0.5f, -1.208f)
                quadToRelative(0f, -0.75f, 0.5f, -1.25f)
                reflectiveQuadToRelative(1.208f, -0.5f)
                quadToRelative(0.709f, 0f, 1.229f, 0.521f)
                quadToRelative(0.521f, 0.521f, 0.521f, 1.229f)
                quadToRelative(0f, 0.708f, -0.521f, 1.208f)
                quadToRelative(-0.52f, 0.5f, -1.229f, 0.5f)
                close()
                moveToRelative(5f, -5.291f)
                quadToRelative(-0.708f, 0f, -1.208f, -0.5f)
                reflectiveQuadToRelative(-0.5f, -1.209f)
                quadToRelative(0f, -0.708f, 0.5f, -1.229f)
                quadToRelative(0.5f, -0.521f, 1.208f, -0.521f)
                quadToRelative(0.709f, 0f, 1.229f, 0.521f)
                quadToRelative(0.521f, 0.521f, 0.521f, 1.229f)
                quadToRelative(0f, 0.709f, -0.521f, 1.209f)
                quadToRelative(-0.52f, 0.5f, -1.229f, 0.5f)
                close()
                moveToRelative(10f, 0f)
                quadToRelative(-0.708f, 0f, -1.208f, -0.5f)
                reflectiveQuadToRelative(-0.5f, -1.209f)
                quadToRelative(0f, -0.708f, 0.5f, -1.229f)
                quadToRelative(0.5f, -0.521f, 1.208f, -0.521f)
                quadToRelative(0.709f, 0f, 1.229f, 0.521f)
                quadToRelative(0.521f, 0.521f, 0.521f, 1.229f)
                quadToRelative(0f, 0.709f, -0.521f, 1.209f)
                quadToRelative(-0.52f, 0.5f, -1.229f, 0.5f)
                close()
                moveToRelative(-5f, 5.291f)
                quadToRelative(-0.708f, 0f, -1.208f, -0.5f)
                reflectiveQuadToRelative(-0.5f, -1.208f)
                quadToRelative(0f, -0.75f, 0.5f, -1.25f)
                reflectiveQuadToRelative(1.208f, -0.5f)
                quadToRelative(0.709f, 0f, 1.229f, 0.521f)
                quadToRelative(0.521f, 0.521f, 0.521f, 1.229f)
                quadToRelative(0f, 0.708f, -0.521f, 1.208f)
                quadToRelative(-0.52f, 0.5f, -1.229f, 0.5f)
                close()
                moveTo(12.208f, 24.375f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.583f)
                quadToRelative(-2.583f, -2.584f, -2.583f, -6.25f)
                quadToRelative(0f, -3.292f, 2.312f, -5.875f)
                quadTo(8.042f, 7.083f, 11.5f, 6.792f)
                quadToRelative(1.333f, -2.334f, 3.562f, -3.709f)
                quadTo(17.292f, 1.708f, 20f, 1.708f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, 0.167f, 5.084f, 2.25f)
                quadToRelative(1.958f, 2.084f, 1.958f, 4.875f)
                quadToRelative(0f, 2.959f, -2.104f, 5.063f)
                quadToRelative(-2.104f, 2.104f, -5.063f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.312f)
                quadToRelative(1.312f, -1.313f, 1.312f, -3.188f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.063f, -4.958f)
                quadTo(22.917f, 4.375f, 20f, 4.375f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.083f)
                lineToRelative(-0.334f, 0.792f)
                horizontalLineToRelative(-1.083f)
                quadToRelative(-2.542f, 0.083f, -4.313f, 1.875f)
                quadToRelative(-1.77f, 1.792f, -1.77f, 4.292f)
                quadToRelative(0f, 2.583f, 1.812f, 4.375f)
                quadToRelative(1.813f, 1.791f, 4.354f, 1.791f)
                close()
                moveTo(20f, 13.042f)
                close()
            }
        }.build()
    }

    val storm by lazy {
        ImageVector.Builder(
            name = "thunderstorm",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(23.708f, 35.417f)
                lineToRelative(-1.791f, -0.917f)
                quadToRelative(-0.584f, -0.292f, -0.709f, -0.896f)
                quadToRelative(-0.125f, -0.604f, 0.334f, -1.104f)
                lineToRelative(3.125f, -3.667f)
                quadToRelative(0.208f, -0.208f, 0.458f, -0.333f)
                quadToRelative(0.25f, -0.125f, 0.542f, -0.125f)
                quadToRelative(0.833f, 0f, 1.187f, 0.792f)
                quadToRelative(0.354f, 0.791f, -0.229f, 1.375f)
                lineToRelative(-2f, 2.375f)
                lineToRelative(1.792f, 0.916f)
                quadToRelative(0.583f, 0.292f, 0.708f, 0.896f)
                quadToRelative(0.125f, 0.604f, -0.292f, 1.104f)
                lineTo(23.667f, 39.5f)
                quadToRelative(-0.209f, 0.208f, -0.459f, 0.312f)
                quadToRelative(-0.25f, 0.105f, -0.541f, 0.105f)
                quadToRelative(-0.834f, 0f, -1.188f, -0.771f)
                reflectiveQuadToRelative(0.229f, -1.396f)
                close()
                moveToRelative(-10f, 0f)
                lineToRelative(-1.791f, -0.917f)
                quadToRelative(-0.584f, -0.292f, -0.709f, -0.896f)
                quadToRelative(-0.125f, -0.604f, 0.334f, -1.104f)
                lineToRelative(3.125f, -3.667f)
                quadToRelative(0.208f, -0.208f, 0.458f, -0.333f)
                quadToRelative(0.25f, -0.125f, 0.542f, -0.125f)
                quadToRelative(0.833f, 0f, 1.187f, 0.792f)
                quadToRelative(0.354f, 0.791f, -0.229f, 1.375f)
                lineToRelative(-2f, 2.375f)
                lineToRelative(1.792f, 0.916f)
                quadToRelative(0.583f, 0.292f, 0.708f, 0.896f)
                quadToRelative(0.125f, 0.604f, -0.292f, 1.104f)
                lineTo(13.667f, 39.5f)
                quadToRelative(-0.209f, 0.208f, -0.459f, 0.312f)
                quadToRelative(-0.25f, 0.105f, -0.541f, 0.105f)
                quadToRelative(-0.834f, 0f, -1.188f, -0.771f)
                reflectiveQuadToRelative(0.229f, -1.396f)
                close()
                moveToRelative(-1.5f, -9.375f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
                quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
                quadToRelative(0f, -3.291f, 2.312f, -5.875f)
                quadTo(8.042f, 8.75f, 11.5f, 8.458f)
                quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
                quadTo(17.292f, 3.375f, 20f, 3.375f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, 0.167f, 5.084f, 2.25f)
                quadToRelative(1.958f, 2.083f, 1.958f, 4.875f)
                quadToRelative(0f, 2.958f, -2.104f, 5.063f)
                quadToRelative(-2.104f, 2.104f, -5.063f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.313f)
                quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.063f, -4.959f)
                quadTo(22.917f, 6.042f, 20f, 6.042f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.083f)
                lineToRelative(-0.334f, 0.792f)
                horizontalLineToRelative(-1.083f)
                quadToRelative(-2.542f, 0.083f, -4.313f, 1.875f)
                quadToRelative(-1.77f, 1.791f, -1.77f, 4.291f)
                quadToRelative(0f, 2.584f, 1.812f, 4.375f)
                quadToRelative(1.813f, 1.792f, 4.354f, 1.792f)
                close()
                moveTo(20f, 14.708f)
                close()
            }
        }.build()
    }

}
