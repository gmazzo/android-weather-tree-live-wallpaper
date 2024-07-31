package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.random.Random

fun Random.nextFloat(min: Float, max: Float) = (nextFloat() * (max - min)) + min
