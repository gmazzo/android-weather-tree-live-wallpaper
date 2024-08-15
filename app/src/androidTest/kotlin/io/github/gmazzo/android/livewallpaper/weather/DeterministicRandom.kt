package io.github.gmazzo.android.livewallpaper.weather

import android.util.Log
import kotlin.random.Random

class DeterministicRandom : Random() {

    private val threadLocal = ThreadLocalRandom()

    override fun nextBits(bitCount: Int) =
        threadLocal.get()!!.nextBits(bitCount)

    private class ThreadLocalRandom : ThreadLocal<Random>() {
        override fun initialValue() = Random(seed = 0).also {
            Log.d("DeterministicRandom", "Providing $it for ${Thread.currentThread()}")
        }
    }

}
