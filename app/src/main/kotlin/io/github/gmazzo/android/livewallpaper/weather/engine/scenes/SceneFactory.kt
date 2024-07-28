package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

fun interface SceneFactory {
    fun create(mode: SceneMode, init: (Scene) -> Unit): Scene
}
