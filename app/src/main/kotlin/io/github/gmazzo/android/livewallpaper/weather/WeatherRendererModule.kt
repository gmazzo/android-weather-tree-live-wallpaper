package io.github.gmazzo.android.livewallpaper.weather

import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent

@DisableInstallInCheck
@Module(subcomponents = [SceneComponent::class])
interface WeatherRendererModule
