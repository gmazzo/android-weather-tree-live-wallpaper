package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import javax.inject.Inject
import javax.inject.Provider
import kotlin.random.Random

class Things @Inject constructor(
    private val sunProvider: Provider<ThingSun>,
    private val moonProvider: Provider<ThingMoon>,
    private val cloudLightFactory: ThingLightCloud.Factory,
    private val cloudDarkFactory: ThingDarkCloud.Factory,
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

            if (thing.deleted) {
                remove()

            } else {
                thing.update()
            }
        }
    }

    fun spawnMoon() = addIfMissing(moonProvider::get) { moon ->
        moon.origin = Vector(-30f, 100f, -100f)
    }

    fun spawnSun() = addIfMissing(sunProvider::get) { sun ->
        sun.origin = Vector(30f, 100f, 0f)
    }

    fun spawnClouds(numClouds: Int, dark: Boolean = false) {
        val factory: (Int) -> ThingCloud =
            if (dark) cloudDarkFactory::create
            else cloudLightFactory::create

        // positions the clouds randomly uniformly in the height
        val yPositions = (0 until numClouds).shuffled().map { it * 130f / numClouds + 45f }

        syncInstances(numClouds, factory) { which ->
            origin = Vector(
                x = which * 90f / numClouds - 45,
                y = yPositions[which],
                z = Random.nextFloat(-20f, -10f)
            )
        }
    }

    fun spawnWisps(numWisps: Int) =
        syncInstances(numWisps, wispyFactory::create) { which ->
            origin = origin.copy(
                x = which * 120f / numWisps - .0703125f,
            )
        }

    private inline fun <reified Type : Thing> syncInstances(
        desiredCount: Int,
        onCreate: (Int) -> Type,
        noinline onEach: (Type.(Int) -> Unit)? = null,
    ): Boolean {
        var changed = false
        var count = 0
        val it = items.iterator()

        while (it.hasNext()) {
            val thing = it.next()

            if (thing is Type) {
                if (count >= desiredCount) {
                    it.remove()
                    changed = true

                } else if (onEach != null) {
                    onEach(thing, count)
                }

                count++
            }
        }
        (count until desiredCount).forEach {
            val thing = onCreate(it)

            if (onEach != null) {
                thing.onEach(it)
            }
            add(thing)
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
    }

}
