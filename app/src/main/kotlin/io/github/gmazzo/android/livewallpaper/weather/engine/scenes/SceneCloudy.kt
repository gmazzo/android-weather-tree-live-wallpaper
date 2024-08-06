package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import javax.inject.Inject

class SceneCloudy @Inject constructor(
    dependencies: SceneDependencies,
) : Scene(
    dependencies,
    background = R.drawable.bg1,
    withSunAndMoon = true,
    withStars = true
)
