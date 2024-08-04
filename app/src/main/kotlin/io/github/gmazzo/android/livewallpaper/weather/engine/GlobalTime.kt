package io.github.gmazzo.android.livewallpaper.weather.engine

import android.text.format.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    @Inject
    fun update() {
        val now = ZonedDateTime.now()
        val delta = ChronoUnit.MILLIS.between(this.time.value, now)

        update(now, delta)
    }

    private fun update(now: ZonedDateTime, delta: Long) {
        currentMillis += delta * timeScale
        deltaSeconds = (delta / 1000f).coerceIn(0f, 1f / 3f)
        elapsedSeconds += deltaSeconds
        time.value = computeNow()

        derived?.update(now, delta)
    }

    private val zoneId = ZoneId.systemDefault()

    private fun computeNow() =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentMillis), zoneId)

    val time = MutableStateFlow<ZonedDateTime>(computeNow())

    @Singleton
    class Fast @Inject constructor(
    ) : GlobalTime(timeScale = DateUtils.DAY_IN_MILLIS / (15 * DateUtils.SECOND_IN_MILLIS))

}
