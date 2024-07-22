package io.github.gmazzo.android.livewallpaper.weather.work

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import java.util.concurrent.TimeUnit

class ScheduleWorkInitializer : Initializer<Unit> {

    private val workPolicy =
        if (BuildConfig.DEBUG) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
        else ExistingPeriodicWorkPolicy.KEEP

    override fun create(context: Context) {
        EntryPointAccessors.fromApplication(context, InitializerEntryPoint::class.java)
            .workManager
            .enqueueUniquePeriodicWork(
                "weatherUpdate",
                workPolicy,
                PeriodicWorkRequestBuilder<WeatherConditionsUpdateWorker>(
                    1,
                    TimeUnit.HOURS,
                    8,
                    TimeUnit.HOURS
                ).setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                ).build()
            )
    }

    override fun dependencies() = emptyList<Class<Initializer<*>>>()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface InitializerEntryPoint {
        val workManager: WorkManager
    }

}
