package io.github.gmazzo.android.livewallpaper.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityInfo(
    var city: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var state: String? = null,
    var cityCode: String? = null
) : Parcelable
