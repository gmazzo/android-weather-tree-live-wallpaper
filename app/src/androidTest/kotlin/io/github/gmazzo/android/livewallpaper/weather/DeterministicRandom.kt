package io.github.gmazzo.android.livewallpaper.weather

import android.util.Log
import kotlin.random.Random

object DeterministicRandom : Random() {

    private var threadLocal = ThreadLocalRandom()

    override fun nextBits(bitCount: Int) =
        threadLocal.get()!!.nextBits(bitCount)

    fun reset() = apply {
        threadLocal = ThreadLocalRandom()
    }

    private class ThreadLocalRandom : ThreadLocal<Random>() {
        override fun initialValue() = Random(seed = 0).also {
            Log.d("DeterministicRandom", "Providing $it for ${Thread.currentThread()}")
        }
    }

}
