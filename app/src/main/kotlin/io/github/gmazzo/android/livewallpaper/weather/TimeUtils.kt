package io.github.gmazzo.android.livewallpaper.weather

import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes

val ZonedDateTime.minutesSinceMidnight
    get() = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, this).minutes
