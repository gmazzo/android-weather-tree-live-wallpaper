package io.github.gmazzo.android.livewallpaper.weather.wallpaper

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Texture(
    @DrawableRes @RawRes val resId: Int,
    val glId: Int
)
