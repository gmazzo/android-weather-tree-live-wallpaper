package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.time.ZonedDateTime
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [WeatherModule.ReplaceableDependencies::class]
)
object TestModule {

    @Provides
    @Singleton
    fun now(): () -> ZonedDateTime = currentTime

    @Provides
    @Singleton
    fun random(): Random = DeterministicRandom()

    @Provides
    @Singleton
    fun workManager(@ApplicationContext context: Context): WorkManager {
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(SynchronousExecutor())
                .build()
        )
        return WorkManager.getInstance(context)
    }

}
