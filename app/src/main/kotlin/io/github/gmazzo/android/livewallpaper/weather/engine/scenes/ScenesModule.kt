package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayColors
import javax.inject.Provider

@Module
@DisableInstallInCheck
internal object ScenesModule {

    @Provides
    @SceneScoped
    fun scene(
        mode: SceneMode,
        clear: Provider<SceneClear>,
        cloudy: Provider<SceneCloudy>,
        rain: Provider<SceneRain>,
        snow: Provider<SceneSnow>,
        storm: Provider<SceneStorm>,
        fog: Provider<SceneFog>
    ): Scene = when (mode) {
        SceneMode.CLEAR -> clear.get()
        SceneMode.CLOUDY -> cloudy.get()
        SceneMode.RAIN -> rain.get()
        SceneMode.SNOW -> snow.get()
        SceneMode.STORM -> storm.get()
        SceneMode.FOG -> fog.get()
    }

    @Provides
    @SceneScoped
    fun timeOfDayColors(
        resources: Resources,
        mode: SceneMode,
    ): TimeOfDayColors = when (mode) {
        SceneMode.RAIN, SceneMode.STORM -> TimeOfDayColors(
            night = resources.getColor(R.color.timeOfDay_rain_night, null),
            dawn = resources.getColor(R.color.timeOfDay_rain_dawn, null),
            day = resources.getColor(R.color.timeOfDay_rain_day, null),
            dusk = resources.getColor(R.color.timeOfDay_rain_dusk, null),
        )

        else -> TimeOfDayColors(
            night = resources.getColor(R.color.timeOfDay_night, null),
            dawn = resources.getColor(R.color.timeOfDay_dawn, null),
            day = resources.getColor(R.color.timeOfDay_day, null),
            dusk = resources.getColor(R.color.timeOfDay_dusk, null),
        )
    }

}
