package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

fun interface SceneFactory {
    fun createScene(mode: SceneMode): Scene
}
