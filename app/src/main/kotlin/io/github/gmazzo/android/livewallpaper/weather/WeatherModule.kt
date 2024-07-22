package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {
    private const val TAG = "WeatherModule"

    @Provides
    @Reusable
    fun provideWeatherMutableState(@ApplicationContext context: Context): MutableStateFlow<WeatherState> =
        MutableStateFlow(context.weatherState).also { flow ->
            CoroutineScope(Dispatchers.IO).launch {
                flow.collectLatest { context.weatherState = it }
            }
        }

    @Provides
    @Reusable
    fun provideWeatherState(state: MutableStateFlow<WeatherState>): StateFlow<WeatherState> =
        state.asStateFlow()

    @Provides
    @Singleton
    fun provideWorkManager(
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
    fun provideLocation(@ApplicationContext context: Context): Location? {
        if (!context.hasLocationPermission) {
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
