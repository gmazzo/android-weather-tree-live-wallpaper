package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.math.cos
import kotlin.math.sin

class GlobalTime {
    val WORST_FRAMERATE: Float = 3.0f
    val WORST_FRAME_TIME: Float = 0.3333333f
    var msTimeCurrent: Long = 0
    private var msTimeDelta = 0
    private var msTimePrev: Long = 0
    var sTimeDelta: Float = 0f
    var sTimeElapsed: Float = 0f

    init {
        setInitialValues()
        instance = this
    }

    private fun setInitialValues() {
        this.msTimeCurrent = System.currentTimeMillis()
        this.sTimeElapsed = 0.0f
        this.msTimePrev = this.msTimeCurrent - 16
        this.sTimeDelta = ((this.msTimeCurrent - this.msTimePrev).toFloat()) / 1000.0f
        this.msTimeDelta = (this.msTimeCurrent - this.msTimePrev).toInt()
        static_sTimeElapsed = 0.0f
    }

    fun reset() {
        setInitialValues()
    }

    fun updateTime() {
        this.msTimePrev = this.msTimeCurrent
        this.msTimeCurrent = System.currentTimeMillis()
        this.sTimeDelta = ((this.msTimeCurrent - this.msTimePrev).toFloat()) / 1000.0f
        this.msTimeDelta = (this.msTimeCurrent - this.msTimePrev).toInt()
        if (this.sTimeDelta > 0.3333333f) {
            this.sTimeDelta = 0.3333333f
        }
        if (this.sTimeDelta < 0.0f) {
            this.sTimeDelta = 0.0f
        }
        this.sTimeElapsed += this.sTimeDelta
        static_sTimeElapsed += this.sTimeDelta
    }

    companion object {
        private var instance: GlobalTime? = null
        private var static_sTimeElapsed = 0.0f
        fun getInstance(): GlobalTime? {
            if (instance == null) {
                instance = GlobalTime()
            }
            return instance
        }

        fun waveCos(base: Float, amplitude: Float, phase: Float, frequency: Float): Float {
            return ((base.toDouble()) + ((amplitude.toDouble()) * cos(((static_sTimeElapsed * frequency) + phase).toDouble()))).toFloat()
        }

        fun waveSin(base: Float, amplitude: Float, phase: Float, frequency: Float): Float {
            return waveSin(static_sTimeElapsed, base, amplitude, phase, frequency)
        }

        fun waveSin(
            inputTime: Float,
            base: Float,
            amplitude: Float,
            phase: Float,
            frequency: Float
        ): Float {
            return ((base.toDouble()) + ((amplitude.toDouble()) * sin(((inputTime * frequency) + phase).toDouble()))).toFloat()
        }
    }
}
