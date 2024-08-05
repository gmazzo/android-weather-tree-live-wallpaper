package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.res.Resources
import android.graphics.Color
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingCloud
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingDarkCloud
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingLightCloud
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayColors
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.timeOfDayColors
import javax.inject.Named
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
        SceneMode.RAIN, SceneMode.STORM -> resources.timeOfDayColors(
            sunrise = R.color.timeOfDay_rain_sunrise,
            day = R.color.timeOfDay_rain_day,
            sunset = R.color.timeOfDay_rain_sunset,
            night = R.color.timeOfDay_rain_night,
        )
        SceneMode.FOG -> resources.timeOfDayColors(
            sunrise = R.color.timeOfDay_fog_sunrise,
            day = R.color.timeOfDay_fog_day,
            sunset = R.color.timeOfDay_fog_sunset,
            night = R.color.timeOfDay_fog_night,
        )
        else -> resources.timeOfDayColors(
            sunrise = R.color.timeOfDay_sunrise,
            day = R.color.timeOfDay_day,
            sunset = R.color.timeOfDay_sunset,
            night = R.color.timeOfDay_night,
        )
    }

    @Provides
    @SceneScoped
    fun cloudFactory(
        mode: SceneMode,
        cloudLightFactory: ThingLightCloud.Factory,
        cloudDarkFactory: ThingDarkCloud.Factory,
    ) : ThingCloud.Factory<*> = when (mode) {
        SceneMode.STORM, SceneMode.RAIN -> cloudDarkFactory
        else -> cloudLightFactory
    }

    @Provides
    @SceneScoped
    @Named("clouds")
    fun cloudsColor(
        mode: SceneMode,
        timeOfDayTint: TimeOfDayTint,
    ) = when (mode) {
        SceneMode.STORM -> EngineColor(.2f, .2f, .2f)
        SceneMode.RAIN -> EngineColor(Color.WHITE)
        else -> timeOfDayTint.color
    }

}
