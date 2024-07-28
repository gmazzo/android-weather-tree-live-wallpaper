package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import io.github.gmazzo.android.livewallpaper.weather.OpenGLComponent
import javax.inject.Provider

@Module
@InstallIn(OpenGLComponent::class)
internal object ScenesModule {

    @Provides
    @Reusable
    fun sceneFactory(
        clear: Provider<SceneClear>,
        cloudy: Provider<SceneCloudy>,
        rain: Provider<SceneRain>,
        snow: Provider<SceneSnow>,
        storm: Provider<SceneStorm>,
        fog: Provider<SceneFog>
    ) = SceneFactory {
        when (it) {
            SceneMode.CLEAR -> clear.get()
            SceneMode.CLOUDY -> cloudy.get()
            SceneMode.RAIN -> rain.get()
            SceneMode.SNOW -> snow.get()
            SceneMode.STORM -> storm.get()
            SceneMode.FOG -> fog.get()
        }
    }

}
