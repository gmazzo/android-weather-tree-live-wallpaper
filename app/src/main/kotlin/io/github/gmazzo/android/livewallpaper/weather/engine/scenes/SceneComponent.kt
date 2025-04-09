package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@SceneScoped
@Subcomponent(modules = [SceneModule::class])
interface SceneComponent {

    val mode: SceneMode

    // Lazy to delay instantiation until OpenGL is properly switched (at draw phase)
    // we we instead of `dagger.Lazy`, because we want to be able to ask if it was initialized
    val scene: Lazy<Scene>

    @Subcomponent.Factory
    fun interface Factory {
        fun create(
            @BindsInstance mode: SceneMode,
            @BindsInstance @Named("landscape") landscape: Boolean,
        ): SceneComponent
    }

}
