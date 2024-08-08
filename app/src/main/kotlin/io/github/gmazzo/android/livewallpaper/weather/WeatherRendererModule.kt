package io.github.gmazzo.android.livewallpaper.weather

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import javax.inject.Named
import javax.inject.Provider

@DisableInstallInCheck
@Module(subcomponents = [SceneComponent::class])
object WeatherRendererModule {

    @Provides
    @WeatherRendererScoped
    @Named("scaled")
    fun fastTime(
        @Named("fastTime") fastTime: Boolean,
        real: Provider<GlobalTime>,
        fast: Provider<GlobalTime.Fast>,
    ): GlobalTime = (if (fastTime) fast else real).get()

    @Provides
    @WeatherRendererScoped
    fun coroutineJob(): CompletableJob = Job()

    @Provides
    @WeatherRendererScoped
    fun coroutineScope(
        dispatcher: GLDispatcher,
        job: CompletableJob,
    ): CoroutineScope = CoroutineScope(dispatcher + job)

}
