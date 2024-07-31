package io.github.gmazzo.android.livewallpaper.weather.engine

val Number.degrees: Degrees
    get() = Degrees(this)

@JvmInline
value class Degrees(val value: Number) {

    init {
        check(value in 0 until 360) { "Degrees must be in range [0, 360)" }
    }

}
