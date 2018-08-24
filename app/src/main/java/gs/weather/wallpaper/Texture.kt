package gs.weather.wallpaper

import android.support.annotation.AnyRes

data class Texture(
        @AnyRes val resId: Int,
        val glId: Int)
