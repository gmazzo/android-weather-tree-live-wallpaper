package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class GlobalTime protected constructor(
    private val now: () -> ZonedDateTime,
    private val derived: GlobalTime? = null,
) {

    @Inject
    constructor(now: () -> ZonedDateTime, fast: Fast) : this(now, derived = fast)

    val time = MutableStateFlow(now())

    var deltaSeconds = 0f
        private set

    var elapsedSeconds = 0f
        private set

    protected open val Long.scaled
        get() = this

    @Inject
    fun update() {
        val delta = ChronoUnit.MILLIS.between(time.value, now())

        update(delta)
        derived?.update(delta)
    }

    private fun update(delta: Long) {
        val scaledDelta = delta.scaled

        time.value = time.value.plus(scaledDelta, ChronoUnit.MILLIS)
        deltaSeconds = scaledDelta / 1000f
        elapsedSeconds += deltaSeconds
    }

    @Singleton
    class Fast @Inject constructor(
        now: () -> ZonedDateTime,
        @Named("fastTimeSpeed") private val speed: MutableStateFlow<Float>,
    ) : GlobalTime(now) {

        override val Long.scaled
            get() = (this * speed.value).toLong()

    }

}
