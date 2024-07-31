package io.github.gmazzo.android.livewallpaper.weather.engine

import android.text.format.DateUtils
import io.github.gmazzo.android.livewallpaper.weather.OpenGLScoped
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@OpenGLScoped
open class GlobalTime(
    private val timeScale: Long,
    private val derived: GlobalTime? = null,
) {

    @Inject constructor(derived: Fast) : this(timeScale = 1, derived)

    private var currentMillis =
        System.currentTimeMillis()

    var deltaSeconds = 0f
        private set

    var elapsedSeconds = 0f
        private set

    fun update(delta: Long = System.currentTimeMillis() - currentMillis) {
        currentMillis += delta * timeScale
        deltaSeconds = (delta / 1000f).coerceIn(0f, 1f / 3)
        elapsedSeconds += deltaSeconds

        derived?.update(delta)
    }

    val now: ZonedDateTime
        get() = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentMillis), ZoneId.systemDefault())

    @OpenGLScoped
    class Fast @Inject constructor(
    ) : GlobalTime(timeScale = DateUtils.DAY_IN_MILLIS / (10 * DateUtils.SECOND_IN_MILLIS))

}
