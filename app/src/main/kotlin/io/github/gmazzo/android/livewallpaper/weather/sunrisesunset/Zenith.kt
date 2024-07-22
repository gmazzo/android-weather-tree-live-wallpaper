package io.github.gmazzo.android.livewallpaper.weather.sunrisesunset

import java.math.BigDecimal

class Zenith(degrees: Double) {
    private val degrees: BigDecimal = BigDecimal.valueOf(degrees)

    fun degrees(): BigDecimal {
        return this.degrees
    }

    companion object {
        val ASTRONOMICAL: Zenith = Zenith(108.0)
        val CIVIL: Zenith = Zenith(96.0)
        val NAUTICAL: Zenith = Zenith(102.0)
        val OFFICIAL: Zenith = Zenith(90.8333)
    }
}
