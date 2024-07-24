package io.github.gmazzo.android.livewallpaper.weather.engine

import android.text.TextUtils
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Thing
import javax.microedition.khronos.opengles.GL10

class ThingManager {
    private val thingList: MutableList<Thing> = ArrayList()

    @Synchronized
    fun add(thing: Thing): Boolean {
        return thingList.add(thing)
    }

    @Synchronized
    fun clear(): Boolean {
        thingList.clear()
        return false
    }

    @Synchronized
    fun clearByTargetname(name: String?): Boolean {
        val it = thingList.iterator()
        while (it.hasNext()) {
            val thing = it.next()

            if (TextUtils.equals(thing.targetName, name)) {
                it.remove()
            }
        }
        return false
    }

    @Synchronized
    fun countByTargetname(name: String?): Int {
        var count = 0
        for (thing in thingList) {
            if (TextUtils.equals(thing.targetName, name)) {
                count++
            }
        }
        return count
    }

    @Synchronized
    fun render(gl10: GL10) {
        for (thing in thingList) {
            thing.renderIfVisible(gl10)
        }
    }

    @Synchronized
    fun sortByY() {
        thingList.sortBy { it.origin.y }
    }

    @Synchronized
    fun update(timeDelta: Float) {
        update(timeDelta, false)
    }

    @Synchronized
    fun update(timeDelta: Float, onlyVisible: Boolean) {
        val it = thingList.iterator()
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

    companion object {
        private const val MAX_THINGS = 64
        private const val TAG = "GL Engine"
    }
}
