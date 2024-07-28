package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.random.Random

fun Random.nextFloat(min: Float, max: Float) = (nextFloat() * (max - min)) + min

fun EngineColor.normalizedRandom() = set(
    Vector(
        Random.nextFloat(-1.0f, 1.0f),
        Random.nextFloat(-1.0f, 1.0f),
        Random.nextFloat(-1.0f, 1.0f),
    ).normalize(), a
)
