package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.GLDispatcher
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL11

/**
 * Helper class to reduce the boilerplate of passing dependencies to the scene hierarchy.
 */
interface SceneDependencies {
    val landscape: Boolean
    val time: GlobalTime
    val gl: GL11
    val sceneScope: CoroutineScope
    val weather: MutableStateFlow<WeatherType>
    val models: Models
    val textures: Textures
    val things: Things
    val timeOfDay: TimeOfDay
    val timeOfDayTint: TimeOfDayTint

    class Impl @Inject constructor(
        @Named("landscape") override val landscape: Boolean,
        override val time: GlobalTime,
        override val gl: GL11,
        dispatcher: GLDispatcher,
        override val weather: MutableStateFlow<WeatherType>,
        override val models: Models,
        override val textures: Textures,
        override val things: Things,
        override val timeOfDay: TimeOfDay,
        override val timeOfDayTint: TimeOfDayTint
    ) : SceneDependencies {
        override val sceneScope = CoroutineScope(dispatcher)
    }

}