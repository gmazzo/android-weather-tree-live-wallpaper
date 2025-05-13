package io.github.gmazzo.android.livewallpaper.weather.engine

import java.time.ZonedDateTime

fun interface Clock {
    fun now(): ZonedDateTime
}
