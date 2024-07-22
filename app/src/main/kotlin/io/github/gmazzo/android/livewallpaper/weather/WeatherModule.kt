package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Reusable
    fun provideWeatherMutableState(@ApplicationContext context: Context): MutableStateFlow<WeatherState> =
        MutableStateFlow(
            WeatherState(
                context.latitude ?: Float.NaN,
                context.longitude ?: Float.NaN,
                context.weatherConditions,
            )
        )

    @Provides
    @Reusable
    fun provideWeatherState(state: MutableStateFlow<WeatherState>): StateFlow<WeatherState> =
        state.asStateFlow()

}
