package io.github.gmazzo.android.livewallpaper.weather

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

}
