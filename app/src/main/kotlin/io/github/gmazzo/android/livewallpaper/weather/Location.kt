package io.github.gmazzo.android.livewallpaper.weather

data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
)
