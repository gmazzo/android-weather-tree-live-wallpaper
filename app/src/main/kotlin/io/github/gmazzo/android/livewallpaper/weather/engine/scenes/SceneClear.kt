package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import javax.inject.Inject

class SceneClear @Inject constructor(
    dependencies: SceneDependencies,
) : Scene(
    dependencies,
    background = R.drawable.bg3,
    withSunAndMoon = true,
    withStars = true,
)
