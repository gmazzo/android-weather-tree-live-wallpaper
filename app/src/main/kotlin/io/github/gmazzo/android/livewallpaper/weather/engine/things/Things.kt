package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import javax.inject.Inject
import javax.inject.Provider
import kotlin.random.Random

class Things @Inject constructor(
    private val sunProvider: Provider<ThingSun>,
    private val moonProvider: Provider<ThingMoon>,
    private val cloudFactory: ThingCloud.Factory,
    private val darkCloudFactory: ThingDarkCloud.Factory,
    private val wispyFactory: ThingWispy.Factory,
) : MutableList<Thing> by mutableListOf() {

    private inline fun <reified Type : Thing> addIfMissing(
        provider: () -> Type,
        noinline init: ((Type) -> Unit)? = null
    ) = firstOrNull { it is Type } ?: provider().also { init?.invoke(it); add(it) }

    private inline fun <reified Type> removeOfType() =
        removeAll { it is Type }

    fun render() =
        forEach(Thing::renderIfVisible)

    fun sortByY() {
        sortBy { it.origin.y }
    }

    fun update(timeDelta: Float) {
        update(timeDelta, false)
    }

    fun update(timeDelta: Float, onlyVisible: Boolean) {
        val it = iterator()
        while (it.hasNext()) {
            val thing = it.next()

            if (thing.isDeleted) {
                it.remove()

            } else if (onlyVisible) {
                thing.updateIfVisible(timeDelta)

            } else {
                thing.update(timeDelta)
            }
        }
    }

    fun spawnMoon() = addIfMissing(moonProvider::get) { moon ->
        moon.origin.set(-30f, 100f, -100f)
    }

    fun spawnSun() = addIfMissing(sunProvider::get) { sun ->
        sun.origin.set(30f, 100f, 0f)
    }

    fun spawnClouds(numClouds: Int, numWisps: Int, dark: Boolean = false) {
        removeOfType<ThingCloud>()
        removeOfType<ThingDarkCloud>()
        removeOfType<ThingWispy>()

        val cloudDepthList = FloatArray(numClouds)
        val cloudDepthStep = 131.25f / (numClouds.toFloat())

        (0 until numClouds).forEach { i ->
            cloudDepthList[i] = ((i.toFloat()) * cloudDepthStep) + 43.75f
        }

        (0 until numClouds).forEach { i ->
            val current = cloudDepthList[i]
            val randomPos = Random.nextInt(cloudDepthList.size)
            cloudDepthList[i] = cloudDepthList[randomPos]
            cloudDepthList[randomPos] = current
        }

        (0 until numClouds).forEach { i ->
            val cloud =
                if (dark) darkCloudFactory.create(i)
                else cloudFactory.create(i)

            cloud.randomizeScale()
            if (Random.nextInt(2) == 0) {
                cloud.scale.x *= -1.0f
            }
            cloud.origin.x = ((i.toFloat()) * (90.0f / (numClouds.toFloat()))) - 0.099609375f
            cloud.origin.y = cloudDepthList[i]
            cloud.origin.z = Random.nextFloat(-20.0f, -10.0f)
            cloud.velocity = Vector(WIND_SPEED * 1.5f, 0.0f, 0.0f)
            add(cloud)
        }

        (0 until numWisps).forEach { i ->
            val wispy = wispyFactory.create(i)
            wispy.velocity = Vector(WIND_SPEED * 1.5f, 0.0f, 0.0f)
            wispy.scale.set(
                Random.nextFloat(1.0f, 3.0f),
                1.0f,
                Random.nextFloat(1.0f, 1.5f)
            )
            wispy.origin.x = ((i.toFloat()) * (120.0f / (numWisps.toFloat()))) - 0.0703125f
            wispy.origin.y = Random.nextFloat(87.5f, CLOUD_START_DISTANCE)
            wispy.origin.z = Random.nextFloat(-40.0f, -20.0f)
            add(wispy)
        }
    }

    companion object {
        const val WIND_SPEED = 3 * .5f
        private const val CLOUD_START_DISTANCE = 175.0f
    }

}
