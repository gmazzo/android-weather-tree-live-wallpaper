package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SceneScoped

@SceneScoped
@Subcomponent(modules = [ScenesModule::class])
interface SceneComponent {

    val mode: SceneMode

    // Lazy to delay instantiation until OpenGL is properly switched (at draw phase)
    val scene: Lazy<Scene>

    @Subcomponent.Factory
    fun interface Factory {
        fun create(
            @BindsInstance mode: SceneMode,
            @BindsInstance @Named("landscape") landscape: Boolean,
        ): SceneComponent
    }

}
