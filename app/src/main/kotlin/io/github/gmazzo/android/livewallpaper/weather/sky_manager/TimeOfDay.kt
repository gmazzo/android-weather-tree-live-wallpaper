package io.github.gmazzo.android.livewallpaper.weather.sky_manager

import android.util.Log

class TimeOfDay {
    var blendAmount: Float = 0.0f
        private set
    var blendIndex: Int = 1
        private set
    private val _fakeSunArray = floatArrayOf(-1.0f, 0.0f, 1.0f, 0.0f)
    private var _fakeSunPosition = true
    private var _latitude = 0.0f
    private var _longitude = 0.0f
    var mainIndex: Int = 0
        private set
    var sunPosition: Float = 0.0f
        private set
    private val _todTime = IntArray(4)

    private fun deriveMidpoint(a: Int, b: Int): Int {
        var l = if (a < b) {
            a + ((b - a) / 2)
        } else {
            a + (((1440 - a) + b) / 2)
        }
        if (l < 0) {
            l += MINUTES_IN_DAY
        }
        if (l > MINUTES_IN_DAY) {
            return l - 1440
        }
        return l
    }

    private fun timeSince(from: Int, to: Int): Int {
        if (from > to) {
            return from - to
        }
        return (1440 - to) + from
    }

    private fun timeUntil(from: Int, to: Int): Int {
        if (from <= to) {
            return to - from
        }
        return (1440 - from) + to
    }

    fun calculateTimeTable(latitude: Float, longitude: Float) {
        var minOfSunrise = 360
        var minOfSunset = 1080
        if (latitude == 0.0f || longitude == 0.0f) {
            this._fakeSunPosition = true
        } else {
            val sunrise_time = SkyManager.GetSunrise(latitude.toDouble(), longitude.toDouble())
            val sunset_time = SkyManager.GetSunset(latitude.toDouble(), longitude.toDouble())
            if (sunrise_time != null) {
                minOfSunrise = (sunrise_time[11] * 60) + sunrise_time[12]
                Log.v(TAG, "sunrise minutes of day is $minOfSunrise")
            }
            if (sunset_time != null) {
                minOfSunset = (sunset_time[11] * 60) + sunset_time[12]
                Log.v(TAG, "sunset minutes of day is $minOfSunset")
            }
            this._fakeSunPosition = false
        }
        val minOfNoon = deriveMidpoint(minOfSunrise, minOfSunset)
        val minOfMidnight = deriveMidpoint(minOfSunset, minOfSunrise)
        _todTime[0] = minOfMidnight
        _todTime[1] = minOfSunrise
        _todTime[2] = minOfNoon
        _todTime[3] = minOfSunset
        this._latitude = latitude
        this._longitude = longitude
        Log.v(
            TAG,
            "calculateTimeTable @ " + latitude + "r" + longitude + ": " + minOfMidnight + "   " + minOfSunrise + "   " + minOfNoon + "   " + minOfSunset
        )
    }

    fun update(minutes: Int, useSunriseSunsetWeighting: Boolean) {
        var sinceDelta = 999999
        var sinceIndex = -1
        var i = 0
        while (i < _todTime.size) {
            val since = timeSince(minutes, _todTime[i])
            if (since < sinceDelta) {
                sinceDelta = since
                sinceIndex = i
            }
            i++
        }
        this.mainIndex = sinceIndex
        var nextDelta = 999999
        var nextIndex = -1
        i = 0
        while (i < _todTime.size) {
            val until = timeUntil(minutes, _todTime[i])
            if (until < nextDelta) {
                nextIndex = i
                nextDelta = until
            }
            i++
        }
        this.blendIndex = nextIndex
        this.blendAmount = (sinceDelta.toFloat()) / ((sinceDelta + nextDelta).toFloat())
        this.sunPosition =
            (_fakeSunArray[mainIndex] * (1.0f - this.blendAmount)) + (_fakeSunArray[blendIndex] * this.blendAmount)
        if (!useSunriseSunsetWeighting) {
            return
        }
        if (this.blendIndex == 1 || this.blendIndex == 3) {
            this.blendAmount -= 0.5f
            if (this.blendAmount < 0.0f) {
                this.blendAmount = 0.0f
            }
            this.blendAmount *= 2.0f
        } else if (this.blendIndex == 0 || this.blendIndex == 2) {
            this.blendAmount *= 2.0f
            if (this.blendAmount > 1.0f) {
                this.blendAmount = 1.0f
            }
        }
    }

    companion object {
        const val MINUTES_IN_DAY: Int = 1440
        const val MS_IN_DAY: Int = 86400000
        private const val TAG = "GL Engine"
    }
}
