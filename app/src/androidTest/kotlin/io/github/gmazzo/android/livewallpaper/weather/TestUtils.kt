package io.github.gmazzo.android.livewallpaper.weather

import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private val DATE_FORMAT = DateTimeFormatter.ofPattern("YYYYMMdd'T'HHmmss")

val REFERENCE_DATE: ZonedDateTime = ZonedDateTime.of(
    LocalDateTime.of(2024, 8, 10, 0, 0),
    ZoneId.of("Europe/Madrid"),
)

fun discriminatorFor(prefix: String, vararg values: Any?) = buildString {
    append(prefix)
    values.forEach {
        if (it != null) {
            append('_')
            append(when(it) {
                is SceneMode -> it.name.lowercase()
                is ZonedDateTime -> it.format(DATE_FORMAT)
                is Random -> it.nextInt(0x1000000).toString(16)
                else -> it.toString()
            })
        }
    }
}
