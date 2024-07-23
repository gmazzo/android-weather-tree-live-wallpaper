package io.github.gmazzo.android.livewallpaper.weather

import android.app.Application
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application() {

    @Inject
    @Suppress("UnusedReceiverParameter")
    fun WorkManager.init() {
    }

}
