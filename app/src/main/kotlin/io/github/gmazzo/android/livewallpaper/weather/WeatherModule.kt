package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {
    private const val TAG = "WeatherModule"

    @Provides
    @Reusable
    fun provideWeatherConditions(): MutableStateFlow<WeatherConditions> =
        MutableStateFlow(WeatherConditions())

    @Provides
    @Reusable
    fun provideWeatherState(state: MutableStateFlow<WeatherConditions>): StateFlow<WeatherConditions> =
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