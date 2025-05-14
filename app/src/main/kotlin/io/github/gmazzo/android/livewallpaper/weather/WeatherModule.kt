package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.content.res.Resources
import android.os.HandlerThread
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import io.github.gmazzo.android.livewallpaper.weather.engine.time.TimeSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Named
import javax.inject.Singleton
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Module(subcomponents = [WeatherRendererComponent::class])
@InstallIn(SingletonComponent::class)
object WeatherModule {
    private val settingLastWeather = stringPreferencesKey("weather")

    @Provides
    @Singleton
    fun resources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    @Named("real")
    fun clock(source: TimeSource) = MutableStateFlow(Clock(source.now()))

    @Provides
    @Singleton
    @Named("fast")
    fun fastClock(
        @Named("real") real: MutableStateFlow<Clock>,
        @Named("fastTimeSpeed") speed: MutableStateFlow<Double>,
    ) = MutableStateFlow<Clock>(real.value)

    @Provides
    @Singleton
    @Named("fastTimeSpeed")
    fun fastTimeSpeed() = MutableStateFlow(1.days / 15.seconds)

    @Provides
    @Singleton
    fun location(manager: LocationManager): StateFlow<Location?> =
        manager.flow.asStateFlow()

    @Provides
    @Singleton
    @Named("homeOffset")
    fun homeOffset() = MutableStateFlow(.5f)

    @Provides
    @Singleton
    @Named("forecast")
    fun forecastWeatherType() = MutableStateFlow<WeatherType>(WeatherType.UNKNOWN)

    @Provides
    @Singleton
    @Named("takeSnapshot")
    fun providerTakeSnapshotHandlerThread() = HandlerThread("TakeSnapshot").apply { start() }

    @Module
    @InstallIn(SingletonComponent::class)
    object ReplaceableDependencies {

        @Provides
        @Singleton
        fun random(): Random = Random

        @Provides
        @Singleton
        fun timeSource() = TimeSource(ZonedDateTime::now)

        @Provides
        @Singleton
        fun weatherType(
            dataStore: DataStore<Preferences>,
            @Named("forecast") forecast: MutableStateFlow<WeatherType>,
        ): MutableStateFlow<WeatherType> = MutableStateFlow(WeatherType.UNKNOWN).apply {
            MainScope().launch {
                dataStore.data.firstOrNull()?.get(settingLastWeather)?.let {
                    value = WeatherType.valueOf(it)
                }

                launch {
                    collectLatest { weather ->
                        dataStore.edit { it[settingLastWeather] = weather.name }
                    }
                }

                forecast.collectLatest {
                    if (it != WeatherType.UNKNOWN) {
                        value = it
                    }
                }
            }
        }

        @Provides
        @Singleton
        fun workManager(
            @ApplicationContext context: Context,
            workerFactory: HiltWorkerFactory,
        ): WorkManager {
            WorkManager.initialize(
                context,
                Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build()
            )
            return WorkManager.getInstance(context)
        }

    }

}
