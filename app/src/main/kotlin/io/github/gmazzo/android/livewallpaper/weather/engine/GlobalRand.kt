package io.github.gmazzo.android.livewallpaper.weather.engine

import java.util.Random

object GlobalRand {
    var rand: Random = Random()

    fun flipCoin(): Boolean {
        return rand.nextFloat() < 0.5f
    }

    fun floatRange(min: Float, max: Float): Float {
        return (rand.nextFloat() * (max - min)) + min
    }

    fun intRange(min: Int, max: Int): Int {
        return rand.nextInt(max - min) + min
    }

    fun randomNormalizedVector(dest: EngineColor) {
        dest.set(
            Vector(
                floatRange(-1.0f, 1.0f),
                floatRange(-1.0f, 1.0f),
                floatRange(-1.0f, 1.0f)
            )
                .normalize(),
            dest.a
        )
    }
}
