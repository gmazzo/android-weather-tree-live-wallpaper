package io.github.gmazzo.android.livewallpaper.weather

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent
import javax.inject.Named
import javax.inject.Provider

@DisableInstallInCheck
@Module(subcomponents = [SceneComponent::class])
object WeatherRendererModule {

    @Provides
    @OpenGLScoped
    @Named("scaled")
    fun fastTime(
        @Named("fastTime") fastTime: Boolean,
        real: Provider<GlobalTime>,
        fast: Provider<GlobalTime.Fast>,
    ): GlobalTime = (if (fastTime) fast else real).get()

}
