@file:Suppress("UnusedReceiverParameter")

package io.github.gmazzo.android.livewallpaper.weather.theme

import androidx.compose.material.icons.Icons
import com.composables.icons.lucide.CloudFog
import com.composables.icons.lucide.CloudLightning
import com.composables.icons.lucide.CloudRain
import com.composables.icons.lucide.CloudSnow
import com.composables.icons.lucide.Cloudy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sun

val Icons.Weather get() = WeatherIcons

object WeatherIcons {
    val Sunny by Lucide::Sun
    val Cloudy by Lucide::Cloudy
    val Fog by Lucide::CloudFog
    val Rain by Lucide::CloudRain
    val Snow by Lucide::CloudSnow
    val Storm by Lucide::CloudLightning
}
