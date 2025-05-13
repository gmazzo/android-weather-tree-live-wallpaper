package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class GlobalTime protected constructor(
    private val clock: Clock,
    private val derived: GlobalTime? = null,
) {

    @Inject
    constructor(clock: Clock, fast: Fast) : this(clock, derived = fast)

    val time = MutableStateFlow(clock.now())

    var deltaSeconds = 0f
        private set

    var elapsedSeconds = 0f
        private set

    protected open val Long.scaled
        get() = this

    @Inject
    fun update() {
        val delta = ChronoUnit.MILLIS.between(time.value, clock.now())

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
        clock: Clock,
        @Named("fastTimeSpeed") private val speed: MutableStateFlow<Float>,
    ) : GlobalTime(clock) {

        override val Long.scaled
            get() = (this * speed.value).toLong()

    }

}
