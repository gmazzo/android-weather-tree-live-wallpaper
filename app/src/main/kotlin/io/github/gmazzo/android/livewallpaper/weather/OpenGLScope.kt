package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import dagger.BindsInstance
import dagger.Subcomponent
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import javax.inject.Scope
import javax.microedition.khronos.opengles.GL11

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class OpenGLScoped

@OpenGLScoped
@Subcomponent(modules = [WeatherRendererModule::class])
interface OpenGLComponent {

    val time: GlobalTime

    val timeOfDay: TimeOfDay

    val textures: Textures

    val models: Models

    val dispatcher: OpenGLDispatcher

    val sceneFactory: SceneComponent.Factory

    @Subcomponent.Factory
    fun interface Factory {
        fun create(
            @BindsInstance view: GLSurfaceView,
            @BindsInstance gl: GL11,
            @BindsInstance @Named("fastTime") fastTime: Boolean,
            @BindsInstance @Named("homeOffset") homeOffset: MutableStateFlow<Float>,
        ): OpenGLComponent
    }

}
