package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import javax.inject.Inject
import javax.inject.Provider
import kotlin.random.Random
import kotlin.reflect.KClass

class Things @Inject constructor(
    private val sunProvider: Provider<ThingSun>,
    private val moonProvider: Provider<ThingMoon>,
    private val cloudFactory: ThingCloud.Factory,
    private val darkCloudFactory: ThingDarkCloud.Factory,
    private val wispyFactory: ThingWispy.Factory,
) {

    private val items = mutableListOf<Thing>()

    fun clear() =
        items.clear()

    fun add(thing: Thing) {
        items.add(thing)
        items.sortWith(Sorter)
    }

    private inline fun <reified Type : Thing> addIfMissing(
        provider: () -> Type,
        noinline init: ((Type) -> Unit)? = null
    ) = items.firstOrNull { it is Type } ?: provider().also { init?.invoke(it); add(it) }

    fun render() =
        items.forEach(Thing::render)

    fun update() = with(items.iterator()) {
        while (hasNext()) {
            val thing = next()

            if (thing.isDeleted) {
                remove()

            } else {
                thing.update()
            }
        }
    }

    fun spawnMoon() = addIfMissing(moonProvider::get) { moon ->
        moon.origin.set(-30f, 100f, -100f)
    }

    fun spawnSun() = addIfMissing(sunProvider::get) { sun ->
        sun.origin.set(30f, 100f, 0f)
    }

    fun spawnClouds(numClouds: Int, dark: Boolean = false) {
        val cloudDepthStep = 131.25f / numClouds

        val changed = syncInstances(
            if (dark) ThingDarkCloud::class else ThingCloud::class,
            numClouds
        ) { which ->
            val cloud =
                if (dark) darkCloudFactory.create(which)
                else cloudFactory.create(which)

            cloud.randomizeScale()
            if (Random.nextInt(2) == 0) {
                cloud.scale.x *= -1.0f
            }
            cloud.origin.x = ((which.toFloat()) * (90.0f / (numClouds.toFloat()))) - 0.099609375f
            cloud.origin.z = Random.nextFloat(-20.0f, -10.0f)
            cloud.velocity = Vector(WIND_SPEED * 1.5f, 0.0f, 0.0f)
            return@syncInstances cloud
        }
        if (changed) {
            // positions the clouds randomly uniformly in the height
            items
                .filter { if (dark) it is ThingDarkCloud else it is ThingCloud }
                .shuffled()
                .forEachIndexed { which, cloud ->
                    cloud.origin.y = ((which.toFloat()) * cloudDepthStep) + 43.75f
                }
        }
    }

    fun spawnWisps(numWisps: Int) = syncInstances(ThingWispy::class, numWisps) { which ->
        val wispy = wispyFactory.create(which)

        wispy.velocity = Vector(WIND_SPEED * 1.5f, 0.0f, 0.0f)
        wispy.scale.set(
            Random.nextFloat(1.0f, 3.0f),
            1.0f,
            Random.nextFloat(1.0f, 1.5f)
        )
        wispy.origin.x = ((which.toFloat()) * (120.0f / (numWisps.toFloat()))) - 0.0703125f
        wispy.origin.y = Random.nextFloat(87.5f, CLOUD_START_DISTANCE)
        wispy.origin.z = Random.nextFloat(-40.0f, -20.0f)
        return@syncInstances wispy
    }

    private fun <Type : Thing> syncInstances(
        type: KClass<out Type>,
        desiredCount: Int,
        onCreate: (Int) -> Type
    ): Boolean {
        var changed = false
        var count = 0
        val it = items.iterator()

        while (it.hasNext()) {
            if (type.isInstance(it.next())) {
                count++

                if (count > desiredCount) {
                    it.remove()
                    changed = true
                }
            }
        }
        (count until desiredCount).forEach {
            add(onCreate(it))
            changed = true
        }
        return changed
    }

    private object Sorter : Comparator<Thing> by (compareBy<Thing> {
        when (it) {
            is ThingSun, is ThingMoon -> 0
            else -> 1
        }
    }.thenBy { it.origin.y })

    companion object {
        const val WIND_SPEED = 3 * .5f
        private const val CLOUD_START_DISTANCE = 175.0f
    }

}
