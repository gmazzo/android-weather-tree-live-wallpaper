package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import dagger.BindsInstance
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope
import javax.microedition.khronos.opengles.GL11

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class OpenGLScoped

@OpenGLScoped
@DefineComponent(parent = SingletonComponent::class)
interface OpenGLComponent {

    @DefineComponent.Builder
    interface Builder {
        fun view(@BindsInstance view: GLSurfaceView): Builder
        fun gl(@BindsInstance gl: GL11): Builder
        fun build(): OpenGLComponent
    }

}
