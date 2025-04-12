package io.github.gmazzo.android.livewallpaper.weather.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class SettingsScreenSnapshotTest {

    private val date = ZonedDateTime.of(
        LocalDateTime.of(2024, 8, 10, 0, 0),
        ZoneId.of("Europe/Madrid"),
    )

    @Composable
    @Preview(showBackground = true)
    fun Default(@PreviewParameter(Scenes::class) scene: SceneMode) = SettingsScreen(
        now = date,
        weather = WeatherType.valueOf(scene),
    )

    @Composable
    @Preview(showBackground = true)
    fun RetrievingLocation() = SettingsScreen(
        now = date,
        weather = WeatherType.UNKNOWN,
        updateLocationEnabled = true,
    )

    class Scenes : PreviewParameterProvider<SceneMode> {
        override val values = SceneMode.entries.asSequence()
        override fun toString() = "scene"
    }

}
