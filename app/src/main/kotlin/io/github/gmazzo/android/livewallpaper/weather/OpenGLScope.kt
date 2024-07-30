package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import dagger.BindsInstance
import dagger.Subcomponent
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneFactory
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.ScenesModule
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Named
import javax.inject.Scope
import javax.microedition.khronos.opengles.GL11

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class OpenGLScoped

@OpenGLScoped
@Subcomponent(modules = [ScenesModule::class])
interface OpenGLComponent {

    val time: GlobalTime

    val timeOfDay: TimeOfDay

    val textures: Textures

    val models: Models

    val sceneFactory: SceneFactory

    val dispatcher: OpenGLDispatcher

    @Subcomponent.Factory
    fun interface Factory {
        fun create(
            @BindsInstance view: GLSurfaceView,
            @BindsInstance gl: GL11,
            @BindsInstance @Named("fastTime") fastTime: Boolean,
        ): OpenGLComponent
    }

}
