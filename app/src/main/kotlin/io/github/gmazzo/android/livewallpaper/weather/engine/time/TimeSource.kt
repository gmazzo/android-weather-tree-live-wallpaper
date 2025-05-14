package io.github.gmazzo.android.livewallpaper.weather.engine.time

import java.time.ZonedDateTime

fun interface TimeSource {
    fun now(): ZonedDateTime
}
