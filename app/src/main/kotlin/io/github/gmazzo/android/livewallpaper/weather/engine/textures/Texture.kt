package io.github.gmazzo.android.livewallpaper.weather.engine.textures

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

class Texture(
    val name: String,
    @DrawableRes @RawRes val resId: Int,
    val glId: Int
) {
    override fun toString() = "$name (resId=$resId, glId=$glId)"
}
