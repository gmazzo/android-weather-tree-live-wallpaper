package io.github.gmazzo.android.livewallpaper.weather.engine

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class GlobalTime @Inject constructor() {

    var currentMillis = System.currentTimeMillis()
        private set

    var deltaSeconds = 0f
        private set

    var elapsedSeconds = 0f
        private set

    fun updateTime() {
        val prev = currentMillis
        currentMillis = System.currentTimeMillis()
        deltaSeconds = max(min((currentMillis - prev) / 1000f, 0.3333333f), 0f)
        elapsedSeconds += deltaSeconds
    }

}
