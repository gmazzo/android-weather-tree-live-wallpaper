package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.AnyRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures

abstract class SimpleThing(
    models: Models,
    textures: Textures,
    @RawRes modelId: Int,
    @AnyRes @DrawableRes textureId: Int,
) : Thing(models, modelId) {

    override val texture by lazy { textures[textureId] }

}
