package io.github.gmazzo.android.livewallpaper.weather

data class WeatherState(
    val location: Location? = null,
    val weatherType: WeatherType = WeatherType.SUNNY_DAY,
) {

    data class Location(
        val latitude: Double,
        val longitude: Double,
    )

}
