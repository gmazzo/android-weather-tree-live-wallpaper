package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.AnyRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.microedition.khronos.opengles.GL11

abstract class SimpleThing(
    gl: GL11,
    models: Models,
    textures: Textures,
    @RawRes modelId: Int,
    @AnyRes @DrawableRes textureId: Int,
) : Thing(gl, models, modelId) {

    override val texture = textures[textureId]

}
