package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
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

}
