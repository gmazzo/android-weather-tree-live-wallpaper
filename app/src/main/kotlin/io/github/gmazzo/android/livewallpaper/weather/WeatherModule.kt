package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.getSystemService
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
import javax.inject.Named
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@Module(subcomponents = [OpenGLComponent::class])
@InstallIn(SingletonComponent::class)
object WeatherModule {
    private const val TAG = "WeatherModule"

    private val settingLastWeather = stringPreferencesKey("weather")

    @Provides
    @Singleton
    fun resources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    @Named("fastTimeSpeed")
    fun fastTimeSpeed(): MutableStateFlow<Float> =
        MutableStateFlow((1.days / 15.seconds).toFloat())

    @Provides
    @Singleton
    fun state(dataStore: DataStore<Preferences>): MutableStateFlow<WeatherState> = runBlocking {
        val current =
            dataStore.data.firstOrNull()?.get(settingLastWeather)?.let(WeatherType::valueOf)
        val flow =
            MutableStateFlow(WeatherState(weatherType = current ?: WeatherType.SUNNY_DAY))

        MainScope().launch {
            flow.collectLatest { state ->
                dataStore.edit { it[settingLastWeather] = state.weatherType.name }
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

    @Provides
    fun location(@ApplicationContext context: Context): Location? {
        if (checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            Log.e(TAG, "Missing $ACCESS_COARSE_LOCATION to access location")
            return null
        }

        val manager: LocationManager = context.getSystemService() ?: return null
        return manager.allProviders.asSequence()
            .mapNotNull(manager::getLastKnownLocation)
            .firstOrNull()
            ?.also { Log.i(TAG, "LastKnownLocation: lat=${it.latitude}, lng=${it.longitude}") }
    }

}
