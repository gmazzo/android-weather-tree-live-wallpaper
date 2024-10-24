package io.github.gmazzo.android.livewallpaper.weather

import androidx.test.filters.SmallTest
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors

@SmallTest
class DeterministicRandomTest {

    private val random = DeterministicRandom()

    /**
     * Validates random is deterministic when invoked from multiple threads
     */
    @Test
    fun isDeterministic() = runTest {
        val seenThreads = CopyOnWriteArraySet<Thread>()
        val values = (0 until THREADS).map {
            async(executor.asCoroutineDispatcher()) {
                Thread.sleep(100)
                seenThreads.add(Thread.currentThread())
                expectedValues.indices.map { random.nextInt(10) }
            }
        }.map { it.await() }

        assertEquals(THREADS, seenThreads.size)
        assertEquals((0 until THREADS).map { expectedValues }, values)
    }

    companion object {

        private const val THREADS = 3

        private val executor by lazy { Executors.newFixedThreadPool(THREADS) }

        private val expectedValues = listOf(4, 8, 7, 7, 2, 7, 1, 7, 4, 9)

        @JvmStatic
        @AfterClass
        fun tearDown() {
            executor.shutdown()
        }

    }

}
