package io.github.gmazzo.android.livewallpaper.weather.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object WeatherIcons {

    private val defaultSize = 24.dp

    val sunny: ImageVector by lazy {
        ImageVector.Builder(
            name = "sunny",
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
                moveToRelative(0f, -2.625f)
                quadToRelative(3.42f, 0f, 5.167f, -2.125f)
                reflectiveQuadTo(27.292f, 20f)
                quadToRelative(0f, -3.42f, -2.125f, -5.167f)
                reflectiveQuadTo(20f, 12.708f)
                quadToRelative(-3.42f, 0f, -5.167f, 2.125f)
                reflectiveQuadTo(12.708f, 20f)
                quadToRelative(0f, 3.42f, 2.125f, 5.167f)
                reflectiveQuadTo(20f, 27.292f)
                close()
            }
        }.build()
    }

    val cloudy by lazy {
        ImageVector.Builder(
            name = "partly_cloudy_day",
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
                quadToRelative(-.375f, -.417f, -.375f, -.937f)
                quadToRelative(0f, -.521f, .375f, -.938f)
                lineToRelative(2.542f, -2.542f)
                quadToRelative(.416f, -.416f, .937f, -.395f)
                quadToRelative(.521f, .02f, .938f, .395f)
                quadToRelative(.375f, .417f, .375f, .938f)
                quadToRelative(0f, .521f, -.375f, .896f)
                lineTo(30.333f, 11.5f)
                quadToRelative(-.416f, .375f, -.916f, .375f)
                reflectiveQuadTo(28.5f, 11.5f)
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
                moveToRelative(-2.291f, 11.625f)
                lineToRelative(-2.5f, -2.542f)
                quadToRelative(-.417f, -.417f, -.417f, -.937f)
                quadToRelative(0f, -.521f, .417f, -.938f)
                quadToRelative(.375f, -.375f, .916f, -.375f)
                quadToRelative(.542f, 0f, .917f, .375f)
                lineToRelative(2.542f, 2.542f)
                quadToRelative(.458f, .416f, .437f, .937f)
                quadToRelative(-.021f, .521f, -.437f, .938f)
                quadToRelative(-.375f, .375f, -.896f, .395f)
                quadToRelative(-.521f, .021f, -.979f, -.395f)
                close()
                moveTo(9.667f, 11.5f)
                lineTo(7.83f, 8.917f)
                quadToRelative(-.375f, -.375f, -.375f, -.896f)
                reflectiveQuadToRelative(.375f, -.938f)
                quadTo(7.5f, 6.708f, 8f, 6.688f)
                quadToRelative(.5f, -.021f, .958f, .395f)
                lineTo(11.5f, 9.667f)
                quadToRelative(.375f, .416f, .375f, .916f)
                reflectiveQuadToRelative(-.375f, .917f)
                quadToRelative(-.417f, .375f, -.917f, .375f)
                reflectiveQuadToRelative(-.916f, -.375f)
                close()
                moveTo(10f, 34.917f)
                quadToRelative(-3.458f, 0f, -5.854f, -2.396f)
                reflectiveQuadTo(1.75f, 26.667f)
                quadToRelative(0f, -3.417f, 2.417f, -5.855f)
                quadTo(6.583f, 18.375f, 10f, 18.375f)
                horizontalLineTo(10.208f)
                quadToRelative(.584f, -3.583f, 3.334f, -5.958f)
                reflectiveQuadTo(20f, 10.42f)
                quadToRelative(4.125f, 0f, 7.42f, 2.916f)
                quadToRelative(2.916f, 2.917f, 2.916f, 7.42f)
                quadToRelative(0f, 3.167f, -1.854f, 5.75f)
                reflectiveQuadToRelative(-4.896f, 3.625f)
                quadToRelative(-.166f, 2.167f, -1.833f, 3.854f)
                quadToRelative(-1.667f, 1.688f, -3.875f, 1.688f)
                close()
                moveToRelative(0f, -2.625f)
                horizontalLineToRelative(7.5f)
                quadToRelative(1.375f, 0f, 2.25f, -.875f)
                reflectiveQuadToRelative(.875f, -2.25f)
                quadToRelative(0f, -1.375f, -.875f, -2.271f)
                quadTo(18.875f, 26f, 17.542f, 26f)
                horizontalLineToRelative(-1.875f)
                lineToRelative(-.75f, -1.75f)
                quadToRelative(-.625f, -1.5f, -1.959f, -2.354f)
                quadToRelative(-1.333f, -.854f, -2.958f, -.854f)
                quadToRelative(-2.292f, 0f, -3.958f, 1.666f)
                quadToRelative(-1.667f, 1.667f, -1.667f, 3.959f)
                quadToRelative(0f, 2.375f, 1.625f, 4f)
                reflectiveQuadToRelative(4f, 1.625f)
                close()
                moveToRelative(12.708f, -5.542f)
                quadToRelative(2.84f, -.833f, 3.334f, -2.688f)
                quadToRelative(1.25f, -1.854f, 1.25f, -4.62f)
                quadToRelative(0f, -3.42f, -2.125f, -5.167f)
                reflectiveQuadTo(20f, 12.708f)
                quadToRelative(-2.667f, 0f, -4.729f, 1.75f)
                quadToRelative(-2.63f, 1.75f, -2.438f, 4.417f)
                quadToRelative(1.542f, .708f, 2.729f, 1.854f)
                quadToRelative(1.188f, 1.146f, 1.813f, 2.646f)
                quadToRelative(1.667f, 0f, 3.125f, .979f)
                reflectiveQuadToRelative(2.208f, 2.396f)
                close()
            }
        }.build()
    }

    val foggy by lazy {
        ImageVector.Builder(
            name = "foggy",
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
                moveTo(30f, 31.458f)
                quadToRelative(-.625f, 0f, -1.42f, -.416f)
                quadToRelative(-.416f, -.417f, -.416f, -1.42f)
                reflectiveQuadToRelative(.416f, -1.42f)
                quadToRelative(.417f, -.416f, 1.42f, -.416f)
                reflectiveQuadToRelative(1.42f, .416f)
                quadToRelative(.416f, .417f, .416f, 1.42f)
                reflectiveQuadToRelative(-.416f, 1.42f)
                quadToRelative(-.417f, .416f, -1.42f, .416f)
                close()
                moveToRelative(-18.333f, 5.167f)
                quadToRelative(-.625f, 0f, -1.42f, -.437f)
                quadToRelative(-.417f, -.438f, -.417f, -1.63f)
                quadToRelative(0f, -.583f, .417f, -1.21f)
                quadToRelative(.417f, -.437f, 1.42f, -.437f)
                reflectiveQuadToRelative(1.41f, .416f)
                quadToRelative(.417f, .417f, .417f, 1.42f)
                reflectiveQuadToRelative(-.417f, 1.63f)
                quadToRelative(-.416f, .437f, -1.41f, .437f)
                close()
                moveTo(10f, 31.458f)
                quadToRelative(-.625f, 0f, -1.42f, -.416f)
                quadToRelative(-.416f, -.417f, -.416f, -1.42f)
                reflectiveQuadToRelative(.416f, -1.42f)
                quadToRelative(.417f, -.416f, 1.42f, -.416f)
                horizontalLineToRelative(15f)
                quadToRelative(.625f, 0f, 1.42f, .416f)
                quadToRelative(.416f, .417f, .416f, 1.42f)
                reflectiveQuadToRelative(-.416f, 1.42f)
                quadToRelative(-.417f, .416f, -1.42f, .416f)
                close()
                moveToRelative(6.667f, 5.167f)
                quadToRelative(-.625f, 0f, -1.42f, -.437f)
                quadToRelative(-.417f, -.438f, -.417f, -1.63f)
                quadToRelative(0f, -.583f, .417f, -1.21f)
                quadToRelative(.417f, -.437f, 1.42f, -.437f)
                horizontalLineToRelative(11.666f)
                quadToRelative(.625f, 0f, 1.42f, .416f)
                quadToRelative(.417f, .417f, .417f, 1.42f)
                reflectiveQuadToRelative(-.417f, 1.63f)
                quadToRelative(-.417f, .437f, -1.42f, .437f)
                close()
                moveToRelative(-4.459f, -10.583f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
                quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
                quadToRelative(0f, -3.291f, 2.312f, -5.875f)
                quadTo(8.42f, 8.75f, 11.5f, 8.458f)
                quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
                quadTo(17.292f, 3.375f, 20f, 3.375f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, .167f, 5.84f, 2.25f)
                quadToRelative(1.958f, 2.83f, 1.958f, 4.875f)
                quadToRelative(0f, 2.958f, -2.104f, 5.63f)
                quadToRelative(-2.104f, 2.104f, -5.63f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.313f)
                quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.63f, -4.959f)
                quadTo(22.917f, 6.42f, 20f, 6.42f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.83f)
                lineToRelative(-.292f, .792f)
                horizontalLineToRelative(-1.125f)
                quadToRelative(-2.542f, .083f, -4.313f, 1.875f)
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
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
                moveTo(23.208f, 36.458f)
                quadToRelative(-.458f, .25f, -1f, .063f)
                quadToRelative(-.541f, -.188f, -.791f, -.646f)
                lineToRelative(-2.75f, -5.5f)
                quadToRelative(-.25f, -.5f, -.084f, -1.42f)
                quadToRelative(.167f, -.541f, .667f, -.791f)
                quadToRelative(.5f, -.209f, 1.21f, -.042f)
                quadToRelative(.521f, .167f, .771f, .667f)
                lineToRelative(2.75f, 5.5f)
                quadToRelative(.25f, .5f, .083f, 1.21f)
                quadToRelative(-.167f, .52f, -.667f, .77f)
                close()
                moveToRelative(10f, 0f)
                quadToRelative(-.458f, .209f, -1f, .042f)
                quadToRelative(-.541f, -.167f, -.791f, -.667f)
                lineToRelative(-2.75f, -5.5f)
                quadToRelative(-.25f, -.5f, -.084f, -1.21f)
                quadToRelative(.167f, -.52f, .667f, -.77f)
                reflectiveQuadToRelative(1.21f, -.063f)
                quadToRelative(.521f, .188f, .771f, .646f)
                lineToRelative(2.75f, 5.5f)
                quadToRelative(.25f, .5f, .083f, 1.42f)
                quadToRelative(-.167f, .541f, -.667f, .791f)
                close()
                moveToRelative(-20f, 0f)
                quadToRelative(-.458f, .209f, -1f, .042f)
                quadToRelative(-.541f, -.167f, -.791f, -.625f)
                lineToRelative(-2.75f, -5.5f)
                quadToRelative(-.25f, -.5f, -.063f, -1.42f)
                quadToRelative(.188f, -.541f, .688f, -.791f)
                quadToRelative(.458f, -.209f, 1f, -.042f)
                quadToRelative(.541f, .167f, .791f, .625f)
                lineToRelative(2.75f, 5.542f)
                quadToRelative(.25f, .5f, .063f, 1.21f)
                quadToRelative(-.188f, .52f, -.688f, .77f)
                close()
                moveToRelative(-1f, -10.416f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
                quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
                quadToRelative(0f, -3.291f, 2.312f, -5.875f)
                quadTo(8.42f, 8.75f, 11.5f, 8.458f)
                quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
                quadTo(17.292f, 3.375f, 20f, 3.375f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, .167f, 5.84f, 2.25f)
                quadToRelative(1.958f, 2.83f, 1.958f, 4.875f)
                quadToRelative(0f, 2.958f, -2.104f, 5.63f)
                quadToRelative(-2.104f, 2.104f, -5.63f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.313f)
                quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.63f, -4.959f)
                quadTo(22.917f, 6.42f, 20f, 6.42f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.83f)
                lineToRelative(-.334f, .792f)
                horizontalLineToRelative(-1.83f)
                quadToRelative(-2.542f, .083f, -4.313f, 1.875f)
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
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
                moveTo(10.833f, 31.292f)
                quadToRelative(-.708f, 0f, -1.208f, -.5f)
                reflectiveQuadToRelative(-.5f, -1.209f)
                quadToRelative(0f, -.708f, .5f, -1.229f)
                quadToRelative(.5f, -.521f, 1.208f, -.521f)
                quadToRelative(.709f, 0f, 1.229f, .521f)
                quadToRelative(.521f, .521f, .521f, 1.229f)
                quadToRelative(0f, .709f, -.521f, 1.209f)
                quadToRelative(-.52f, .5f, -1.229f, .5f)
                close()
                moveToRelative(5f, 5.291f)
                quadToRelative(-.708f, 0f, -1.208f, -.5f)
                reflectiveQuadToRelative(-.5f, -1.208f)
                quadToRelative(0f, -.75f, .5f, -1.25f)
                reflectiveQuadToRelative(1.208f, -.5f)
                quadToRelative(.709f, 0f, 1.229f, .521f)
                quadToRelative(.521f, .521f, .521f, 1.229f)
                quadToRelative(0f, .708f, -.521f, 1.208f)
                quadToRelative(-.52f, .5f, -1.229f, .5f)
                close()
                moveToRelative(5f, -5.291f)
                quadToRelative(-.708f, 0f, -1.208f, -.5f)
                reflectiveQuadToRelative(-.5f, -1.209f)
                quadToRelative(0f, -.708f, .5f, -1.229f)
                quadToRelative(.5f, -.521f, 1.208f, -.521f)
                quadToRelative(.709f, 0f, 1.229f, .521f)
                quadToRelative(.521f, .521f, .521f, 1.229f)
                quadToRelative(0f, .709f, -.521f, 1.209f)
                quadToRelative(-.52f, .5f, -1.229f, .5f)
                close()
                moveToRelative(10f, 0f)
                quadToRelative(-.708f, 0f, -1.208f, -.5f)
                reflectiveQuadToRelative(-.5f, -1.209f)
                quadToRelative(0f, -.708f, .5f, -1.229f)
                quadToRelative(.5f, -.521f, 1.208f, -.521f)
                quadToRelative(.709f, 0f, 1.229f, .521f)
                quadToRelative(.521f, .521f, .521f, 1.229f)
                quadToRelative(0f, .709f, -.521f, 1.209f)
                quadToRelative(-.52f, .5f, -1.229f, .5f)
                close()
                moveToRelative(-5f, 5.291f)
                quadToRelative(-.708f, 0f, -1.208f, -.5f)
                reflectiveQuadToRelative(-.5f, -1.208f)
                quadToRelative(0f, -.75f, .5f, -1.25f)
                reflectiveQuadToRelative(1.208f, -.5f)
                quadToRelative(.709f, 0f, 1.229f, .521f)
                quadToRelative(.521f, .521f, .521f, 1.229f)
                quadToRelative(0f, .708f, -.521f, 1.208f)
                quadToRelative(-.52f, .5f, -1.229f, .5f)
                close()
                moveTo(12.208f, 24.375f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.583f)
                quadToRelative(-2.583f, -2.584f, -2.583f, -6.25f)
                quadToRelative(0f, -3.292f, 2.312f, -5.875f)
                quadTo(8.42f, 7.83f, 11.5f, 6.792f)
                quadToRelative(1.333f, -2.334f, 3.562f, -3.709f)
                quadTo(17.292f, 1.708f, 20f, 1.708f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, .167f, 5.84f, 2.25f)
                quadToRelative(1.958f, 2.84f, 1.958f, 4.875f)
                quadToRelative(0f, 2.959f, -2.104f, 5.63f)
                quadToRelative(-2.104f, 2.104f, -5.63f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.312f)
                quadToRelative(1.312f, -1.313f, 1.312f, -3.188f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.63f, -4.958f)
                quadTo(22.917f, 4.375f, 20f, 4.375f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.83f)
                lineToRelative(-.334f, .792f)
                horizontalLineToRelative(-1.83f)
                quadToRelative(-2.542f, .083f, -4.313f, 1.875f)
                quadToRelative(-1.77f, 1.792f, -1.77f, 4.292f)
                quadToRelative(0f, 2.583f, 1.812f, 4.375f)
                quadToRelative(1.813f, 1.791f, 4.354f, 1.791f)
                close()
                moveTo(20f, 13.42f)
                close()
            }
        }.build()
    }

    val storm by lazy {
        ImageVector.Builder(
            name = "thunderstorm",
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
                moveTo(23.708f, 35.417f)
                lineToRelative(-1.791f, -.917f)
                quadToRelative(-.584f, -.292f, -.709f, -.896f)
                quadToRelative(-.125f, -.604f, .334f, -1.104f)
                lineToRelative(3.125f, -3.667f)
                quadToRelative(.208f, -.208f, .458f, -.333f)
                quadToRelative(.25f, -.125f, .542f, -.125f)
                quadToRelative(.833f, 0f, 1.187f, .792f)
                quadToRelative(.354f, .791f, -.229f, 1.375f)
                lineToRelative(-2f, 2.375f)
                lineToRelative(1.792f, .916f)
                quadToRelative(.583f, .292f, .708f, .896f)
                quadToRelative(.125f, .604f, -.292f, 1.104f)
                lineTo(23.667f, 39.5f)
                quadToRelative(-.209f, .208f, -.459f, .312f)
                quadToRelative(-.25f, .105f, -.541f, .105f)
                quadToRelative(-.834f, 0f, -1.188f, -.771f)
                reflectiveQuadToRelative(.229f, -1.396f)
                close()
                moveToRelative(-10f, 0f)
                lineToRelative(-1.791f, -.917f)
                quadToRelative(-.584f, -.292f, -.709f, -.896f)
                quadToRelative(-.125f, -.604f, .334f, -1.104f)
                lineToRelative(3.125f, -3.667f)
                quadToRelative(.208f, -.208f, .458f, -.333f)
                quadToRelative(.25f, -.125f, .542f, -.125f)
                quadToRelative(.833f, 0f, 1.187f, .792f)
                quadToRelative(.354f, .791f, -.229f, 1.375f)
                lineToRelative(-2f, 2.375f)
                lineToRelative(1.792f, .916f)
                quadToRelative(.583f, .292f, .708f, .896f)
                quadToRelative(.125f, .604f, -.292f, 1.104f)
                lineTo(13.667f, 39.5f)
                quadToRelative(-.209f, .208f, -.459f, .312f)
                quadToRelative(-.25f, .105f, -.541f, .105f)
                quadToRelative(-.834f, 0f, -1.188f, -.771f)
                reflectiveQuadToRelative(.229f, -1.396f)
                close()
                moveToRelative(-1.5f, -9.375f)
                quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
                quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
                quadToRelative(0f, -3.291f, 2.312f, -5.875f)
                quadTo(8.42f, 8.75f, 11.5f, 8.458f)
                quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
                quadTo(17.292f, 3.375f, 20f, 3.375f)
                quadToRelative(3.75f, 0f, 6.375f, 2.396f)
                reflectiveQuadToRelative(3.208f, 5.979f)
                quadToRelative(3.125f, .167f, 5.84f, 2.25f)
                quadToRelative(1.958f, 2.83f, 1.958f, 4.875f)
                quadToRelative(0f, 2.958f, -2.104f, 5.63f)
                quadToRelative(-2.104f, 2.104f, -5.63f, 2.104f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineToRelative(17.25f)
                quadToRelative(1.875f, 0f, 3.188f, -1.313f)
                quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
                quadToRelative(0f, -1.875f, -1.312f, -3.187f)
                quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
                horizontalLineToRelative(-2.416f)
                verticalLineToRelative(-1.333f)
                quadToRelative(0f, -2.917f, -2.63f, -4.959f)
                quadTo(22.917f, 6.42f, 20f, 6.42f)
                quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
                reflectiveQuadToRelative(-2.583f, 3.83f)
                lineToRelative(-.334f, .792f)
                horizontalLineToRelative(-1.83f)
                quadToRelative(-2.542f, .083f, -4.313f, 1.875f)
                quadToRelative(-1.77f, 1.791f, -1.77f, 4.291f)
                quadToRelative(0f, 2.584f, 1.812f, 4.375f)
                quadToRelative(1.813f, 1.792f, 4.354f, 1.792f)
                close()
                moveTo(20f, 14.708f)
                close()
            }
        }.build()
    }

    val day by lazy {
        ImageVector.Builder(
            name = "clear_day",
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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

    val night by lazy {
        ImageVector.Builder(
            name = "clear_night",
            defaultWidth = defaultSize,
            defaultHeight = defaultSize,
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
