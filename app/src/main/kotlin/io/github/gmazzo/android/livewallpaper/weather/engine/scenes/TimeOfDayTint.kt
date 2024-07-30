package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.TimeOfDay
import javax.inject.Inject

@SceneScoped
class TimeOfDayTint @Inject constructor(
    sceneMode: SceneMode,
    private val timeOfDay: TimeOfDay,
) {

    val color = EngineColor(1f)

    private val timeOfDayColors = when (sceneMode) {
        SceneMode.RAIN, SceneMode.STORM -> arrayOf(
            EngineColor(.25f, .2f, .2f, 1f),
            EngineColor(.6f, .6f, .6f, 1f),
            EngineColor(.9f, .9f, .9f, 1f),
            EngineColor(.65f, .6f, .6f, 1f),
        )

        else -> arrayOf(
            EngineColor(.5f, .5f, .75f, 1f),
            EngineColor(1f, .73f, .58f, 1f),
            EngineColor(1f, 1f, 1f, 1f),
            EngineColor(1f, .85f, .75f, 1f),
        )
    }

    fun update(
        into: EngineColor = color,
        blendColors: Array<EngineColor> = timeOfDayColors,
    ) {
        into.blend(
            blendColors[timeOfDay.mainIndex],
            blendColors[timeOfDay.blendIndex],
            timeOfDay.blendAmount
        )
    }

    @Inject
    fun reset() {
        color.set(1f, 1f, 1f, 1f)
    }

}
