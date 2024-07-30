package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.AnyRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.microedition.khronos.opengles.GL11

sealed class ThingSimple(
    time: GlobalTime,
    gl: GL11,
    models: Models,
    textures: Textures,
    @RawRes modelId: Int,
    @AnyRes @DrawableRes textureId: Int,
) : Thing(time, gl) {

    override val model = models[modelId]

    override val texture = textures[textureId]

}
