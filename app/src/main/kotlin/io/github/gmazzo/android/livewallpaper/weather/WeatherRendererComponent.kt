package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import dagger.BindsInstance
import dagger.Subcomponent
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named
import javax.microedition.khronos.opengles.GL11

@WeatherRendererScoped
@Subcomponent(modules = [WeatherRendererModule::class])
interface WeatherRendererComponent {

    val time: GlobalTime

    val timeOfDay: TimeOfDay

    val textures: Textures

    val models: Models

    val coroutineScope: CoroutineScope

    val sceneFactory: SceneComponent.Factory

    @Subcomponent.Factory
    fun interface Factory {
        fun create(
            @BindsInstance view: GLSurfaceView,
            @BindsInstance gl: GL11,
            @BindsInstance @Named("fastTime") fastTime: Boolean,
        ): WeatherRendererComponent
    }

}
