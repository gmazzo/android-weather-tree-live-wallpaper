@file:Suppress("UnusedReceiverParameter")

package io.github.gmazzo.android.livewallpaper.weather.theme

import androidx.compose.material.icons.Icons
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Sun

val Icons.TimeOfDay get() = TimeOfDayIcons

object TimeOfDayIcons {
    val Day by Lucide::Sun
    val Night by Lucide::Moon
}
