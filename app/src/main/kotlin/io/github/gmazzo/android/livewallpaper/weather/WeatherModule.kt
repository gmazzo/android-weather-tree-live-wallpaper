package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.content.res.Resources
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    fun random(): Random = Random

    @Provides
    @Singleton
    fun now(): () -> ZonedDateTime = ZonedDateTime::now

    @Provides
    @Singleton
    @Named("fastTimeSpeed")
    fun fastTimeSpeed() = MutableStateFlow((1.days / 15.seconds).toFloat())

    @Provides
    @Singleton
    fun location() = MutableStateFlow<Location?>(null)

    @Provides
    @Singleton
    @Named("homeOffset")
    fun homeOffset() = MutableStateFlow(.5f)

    @Provides
    @Singleton
    fun weatherType(dataStore: DataStore<Preferences>): MutableStateFlow<WeatherType> =
        runBlocking {
            val current =
                dataStore.data.firstOrNull()?.get(settingLastWeather)?.let(WeatherType::valueOf)
            val flow =
                MutableStateFlow(current ?: WeatherType.SUNNY_DAY)

            MainScope().launch {
                flow.collectLatest { weather ->
                    dataStore.edit { it[settingLastWeather] = weather.name }
                }
            }
            return@runBlocking flow
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

    @Module
    @InstallIn(SingletonComponent::class)
    object Nondeterministic {

        @Provides
        @Singleton
        fun random(): Random = Random

        @Provides
        @Singleton
        fun now(): () -> ZonedDateTime = ZonedDateTime::now

    }
}
