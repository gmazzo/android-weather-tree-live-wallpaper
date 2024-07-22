package io.github.gmazzo.android.livewallpaper.weather.engine

import android.util.Log

object Utility {
    var DEBUG: Boolean = true

    fun adjustScreenPosForDepth(
        destVector: Vector,
        cameraFOV: Float,
        screenWidth: Float,
        screenHeight: Float,
        touchX: Float,
        touchY: Float,
        depth: Float
    ) {
        val f10 = (cameraFOV * (screenWidth / screenHeight)) * 0.01111111f
        val f14 =
            (cameraFOV * 0.01111111f) * ((((1.0f - (touchY / screenHeight)) - 0.5f) * 2.0f) * depth)
        destVector.x = ((((touchX / screenWidth) - 0.5f) * 2.0f) * depth) * f10
        destVector.y = depth
        destVector.z = f14
    }

    object Logger {
        fun v(tag: String?, info: String?) {
            if (DEBUG) {
                Log.v(tag, info!!)
            }
        }

        fun d(tag: String?, info: String?) {
            if (DEBUG) {
                Log.d(tag, info!!)
            }
        }

        fun i(tag: String?, info: String?) {
            if (DEBUG) {
                Log.i(tag, info!!)
            }
        }

        fun w(tag: String?, info: String?) {
            if (DEBUG) {
                Log.w(tag, info!!)
            }
        }

        fun e(tag: String?, info: String?) {
            if (DEBUG) {
                Log.e(tag, info!!)
            }
        }
    }
}
