package io.github.gmazzo.android.livewallpaper.weather.engine

import android.util.Log
import io.github.gmazzo.android.livewallpaper.weather.OpenGLDispatcher
import io.github.gmazzo.android.livewallpaper.weather.OpenGLScoped
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Named

@OpenGLScoped
class TimeOfDay @Inject constructor(
    private val time: GlobalTime,
    private val dispatcher: OpenGLDispatcher,
    private val weather: MutableStateFlow<WeatherConditions>,
    @Named("fastTime") private val fastTime: Boolean,
) {
    var blendAmount: Float = 0.0f
        private set
    var blendIndex: Int = 1
        private set
    private val _fakeSunArray = floatArrayOf(-1.0f, 0.0f, 1.0f, 0.0f)
    private var fakeSunPosition = true
    private var _latitude = 0.0f
    private var _longitude = 0.0f
    var mainIndex = 0
        private set
    private val todTime = IntArray(4)

    private fun deriveMidpoint(a: Int, b: Int): Int {
        var l = if (a < b) {
            a + ((b - a) / 2)
        } else {
            a + (((MINUTES_IN_DAY - a) + b) / 2)
        }
        if (l < 0) {
            l += MINUTES_IN_DAY
        }
        if (l > MINUTES_IN_DAY) {
            return l - MINUTES_IN_DAY
        }
        return l
    }

    private fun timeSince(from: Int, to: Int): Int {
        if (from > to) {
            return from - to
        }
        return (MINUTES_IN_DAY - to) + from
    }

    private fun timeUntil(from: Int, to: Int): Int {
        if (from <= to) {
            return to - from
        }
        return (MINUTES_IN_DAY - from) + to
    }

    @Inject
    fun watchLocation(weather: MutableStateFlow<WeatherConditions>) {
        MainScope().launch(dispatcher) {
            weather.collectLatest(::calculateTimeTable)
        }
    }

    private fun calculateTimeTable(weather: WeatherConditions) {
        val (latitude, longitude) = weather

        var minOfSunrise = 360
        var minOfSunset = 1080
        if (!latitude.isFinite() || !longitude.isFinite()) {
            fakeSunPosition = true
        } else {
            val sunriseTime = SkyManager.GetSunrise(latitude.toDouble(), longitude.toDouble())
            val sunsetTime = SkyManager.GetSunset(latitude.toDouble(), longitude.toDouble())
            if (sunriseTime != null) {
                minOfSunrise = (sunriseTime[11] * 60) + sunriseTime[12]
                Log.v(TAG, "sunrise minutes of day is $minOfSunrise")
            }
            if (sunsetTime != null) {
                minOfSunset = (sunsetTime[11] * 60) + sunsetTime[12]
                Log.v(TAG, "sunset minutes of day is $minOfSunset")
            }
            fakeSunPosition = false
        }
        val minOfNoon = deriveMidpoint(minOfSunrise, minOfSunset)
        val minOfMidnight = deriveMidpoint(minOfSunset, minOfSunrise)
        todTime[0] = minOfMidnight
        todTime[1] = minOfSunrise
        todTime[2] = minOfNoon
        todTime[3] = minOfSunset
        _latitude = latitude
        _longitude = longitude
        Log.v(
            TAG,
            "calculateTimeTable @ " + latitude + "r" + longitude + ": " + minOfMidnight + "   " + minOfSunrise + "   " + minOfNoon + "   " + minOfSunset
        )
    }

    fun update(useSunriseSunsetWeighting: Boolean = true) {
        val minutes =
            if (fastTime) ((time.currentMillis / 10) % 1440).toInt()
            else Calendar.getInstance().let { (it[Calendar.HOUR_OF_DAY] * 60) + it[Calendar.MINUTE] }

        var sinceDelta = 999999
        var sinceIndex = -1
        var i = 0
        while (i < todTime.size) {
            val since = timeSince(minutes, todTime[i])
            if (since < sinceDelta) {
                sinceDelta = since
                sinceIndex = i
            }
            i++
        }
        mainIndex = sinceIndex
        var nextDelta = 999999
        var nextIndex = -1
        i = 0
        while (i < todTime.size) {
            val until = timeUntil(minutes, todTime[i])
            if (until < nextDelta) {
                nextIndex = i
                nextDelta = until
            }
            i++
        }
        blendIndex = nextIndex
        blendAmount = (sinceDelta.toFloat()) / ((sinceDelta + nextDelta).toFloat())

        val sunPosition = (_fakeSunArray[mainIndex] * (1.0f - blendAmount)) + (_fakeSunArray[blendIndex] * blendAmount)
        weather.update { it.copy(sunPosition = sunPosition) }

        if (!useSunriseSunsetWeighting) {
            return
        }
        if (blendIndex == 1 || blendIndex == 3) {
            blendAmount -= 0.5f
            if (blendAmount < 0.0f) {
                blendAmount = 0.0f
            }
            blendAmount *= 2.0f
        } else if (blendIndex == 0 || blendIndex == 2) {
            blendAmount *= 2.0f
            if (blendAmount > 1.0f) {
                blendAmount = 1.0f
            }
        }
    }

    companion object {
        const val MINUTES_IN_DAY = 1440
        private const val TAG = "GL Engine"
    }
}
