package io.github.gmazzo.android.livewallpaper.weather

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named

@DisableInstallInCheck
@Module(subcomponents = [SceneComponent::class])
object WeatherRendererModule {

    @Provides
    @WeatherRendererScoped
    fun fastTime(
        @Named("real") real: MutableStateFlow<Clock>,
        @Named("fast") fast: MutableStateFlow<Clock>,
        @Named("fastTime") fastTime: Boolean,
    ): MutableStateFlow<Clock> = if (fastTime) fast else real

    @Provides
    @Named("renderer")
    @WeatherRendererScoped
    fun coroutineScope(dispatcher: GLDispatcher) =
        CoroutineScope(SupervisorJob() + dispatcher)

}
