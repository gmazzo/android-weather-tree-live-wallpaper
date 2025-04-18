package io.github.gmazzo.android.livewallpaper.weather

import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode

enum class WeatherType(
    val scene: SceneMode,
    val clouds: Int,
    val wisps: Int,
) {
    SUNNY_DAY(SceneMode.CLEAR, 2, 2),
    MOSTLY_SUNNY_DAY(SceneMode.CLEAR, 3, 3),
    PARTLY_SUNNY_DAY(SceneMode.CLEAR, 4, 4),
    INTERMITTENT_CLOUDS_DAY(SceneMode.CLEAR, 5, 5),
    HAZY_SUNSHINE_DAY(SceneMode.CLEAR, 5, 10),
    MOSTLY_CLOUDY_DAY(SceneMode.CLOUDY, 15, 5),
    CLOUDY(SceneMode.CLOUDY, 20, 5),
    DREARY_OVERCAST(SceneMode.CLOUDY, 25, 5),
    FOG(SceneMode.FOG, 0, 80),
    SHOWERS(SceneMode.RAIN, 10, 5),
    MOSTLY_CLOUDY_WITH_SHOWERS_DAY(SceneMode.RAIN, 20, 5),
    PARTLY_SUNNY_WITH_SHOWERS_DAY(SceneMode.RAIN, 15, 5),
    THUNDER_STORMS(SceneMode.STORM, 20, 5),
    MOSTLY_CLOUDY_WITH_THUNDER_STORMS_DAY(SceneMode.STORM, 25, 5),
    PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY(SceneMode.STORM, 15, 5),
    RAIN(SceneMode.RAIN, 20, 5),
    FLURRIES(SceneMode.SNOW, 20, 5),
    MOSTLY_CLOUDY_WITH_FLURRIES_DAY(SceneMode.SNOW, 25, 5),
    PARTLY_SUNNY_WITH_FLURRIES_DAY(SceneMode.SNOW, 15, 5),
    SNOW(SceneMode.SNOW, 20, 5),
    MOSTLY_CLOUDY_WITH_SNOW_DAY(SceneMode.SNOW, 25, 5),
    ICE(SceneMode.SNOW, 20, 5),
    SLEET(SceneMode.SNOW, 20, 5),
    FREEZING_RAIN(SceneMode.RAIN, 20, 5),
    RAIN_AND_SNOW(SceneMode.RAIN, 20, 5),
    CLEAR_NIGHT(SceneMode.CLEAR, 2, 2),
    MOSTLY_CLEAR_NIGHT(SceneMode.CLEAR, 4, 4),
    PARTLY_CLOUDY_NIGHT(SceneMode.CLOUDY, 8, 5),
    INTERMITTENT_CLOUDS_NIGHT(SceneMode.CLOUDY, 15, 5),
    HAZY_MOONLIGHT_NIGHT(SceneMode.CLOUDY, 2, 20),
    MOSTLY_CLOUDY_NIGHT(SceneMode.CLOUDY, 15, 5),
    PARTLY_CLOUDY_WITH_SHOWERS_NIGHT(SceneMode.RAIN, 15, 5),
    MOSTLY_CLOUDY_WITH_SHOWERS_NIGHT(SceneMode.RAIN, 25, 5),
    PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT(SceneMode.STORM, 15, 5),
    MOSTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT(SceneMode.STORM, 25, 5),
    MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT(SceneMode.SNOW, 20, 5),
    MOSTLY_CLOUDY_WITH_SNOW_NIGHT(SceneMode.SNOW, 20, 5),

    UNKNOWN(SceneMode.CLEAR, 2, 2);

    companion object {
        fun valueOf(scene: SceneMode) = WeatherType.entries.first { it.scene == scene }
    }
}
