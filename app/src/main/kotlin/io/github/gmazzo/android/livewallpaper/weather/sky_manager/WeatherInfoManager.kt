package io.github.gmazzo.android.livewallpaper.weather.sky_manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.HandlerThread
import android.preference.PreferenceManager
import android.util.Log
import io.github.gmazzo.android.livewallpaper.weather.LocationProvider.lastKnownLocation
import io.github.gmazzo.android.livewallpaper.weather.api.forecast.LocationForecast
import io.github.gmazzo.android.livewallpaper.weather.latitude
import io.github.gmazzo.android.livewallpaper.weather.longitude
import io.github.gmazzo.android.livewallpaper.weather.weatherConditions

class WeatherInfoManager private constructor(
    private val mContext: Context,
    weatherStateReceiver: WeatherStateReceiver
) : Runnable {
    @get:Synchronized
    @set:Synchronized
    private var result = false
    private var mHandler: Handler? = null
    private val mNetworkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ((!this@WeatherInfoManager.result && this@WeatherInfoManager.isConnected) && this@WeatherInfoManager.mHandler != null) {
                mHandler!!.post(instance!!)
            }
        }
    }
    private var mRunning = true
    private var mWeatherStateReceiver: WeatherStateReceiver? = null

    interface WeatherStateReceiver {
        fun updateWeatherState()
    }

    init {
        this.mWeatherStateReceiver = weatherStateReceiver
        mHandlerThread = object : HandlerThread("InfoHandlerThread") {
            public override fun onLooperPrepared() {
                synchronized(mHandlerThread!!) {
                    this@WeatherInfoManager.mHandler = Handler()
                }
            }
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext)
        Log.i(TAG, "register network Receiver: context is " + this.mContext)
        mContext.registerReceiver(this.mNetworkReceiver, Network_Event_Filter)
    }

    override fun run() {
        if (isConnected && this.mRunning) {
            var delay = ABNORMALDURATION
            if (-1 == weatherInformation) {
                result = false
            } else {
                result = true
                delay = NORMALDURATION
            }
            synchronized(this) {
                if (this.mHandler != null) {
                    mWeatherStateReceiver!!.updateWeatherState()
                    mHandler!!.postDelayed(this, delay.toLong())
                }
            }
            return
        }
        result = false
        mWeatherStateReceiver!!.updateWeatherState()
    }

    private val weatherInformation: Int
        get() {
            val location = mContext.lastKnownLocation

            mContext.latitude = location?.latitude?.toFloat()
            mContext.longitude = location?.longitude?.toFloat()
            if (location == null) {
                return -1
            }

            try {
                val response = LocationForecast
                    .getForecast(location.latitude, location.longitude, null)
                    .execute()
                    .body()

                val series = response!!.properties.timeSeries
                if (!series.isEmpty()) {
                    mContext.weatherConditions = series[0].data.nextHour.weatherType
                    return 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return -1
        }

    private val isConnected: Boolean
        get() {
            val info =
                (mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (info != null) {
                return info.isConnected
            }
            return false
        }

    @Synchronized
    fun onStop() {
        Log.i(TAG, "unregister network Receiver context is " + this.mContext)
        try {
            mContext.unregisterReceiver(this.mNetworkReceiver)
        } catch (iae: IllegalArgumentException) {
            Log.d(TAG, "receier never been registered. $iae")
        }
        if (mHandlerThread != null) {
            mHandlerThread!!.quit()
            mHandlerThread = null
        }
        this.mHandler = null
        instance = null
    }

    fun update(delay: Long) {
        if (this.mHandler != null) {
            mHandler!!.removeCallbacks(this)
            if (delay > 0) {
                this.mRunning = true
                mHandler!!.postDelayed(this, delay)
                Log.d(TAG, "postDelayed: $delay")
                return
            } else if (delay == 0L) {
                this.mRunning = true
                mHandler!!.post(this)
                return
            } else {
                this.mRunning = false
                return
            }
        }
        synchronized(mHandlerThread!!) {
            while (this.mHandler == null) {
                try {
                    (mHandlerThread as Object?)!!.wait(100)
                } catch (e: InterruptedException) {
                    Log.w(TAG, e.toString())
                }
            }
        }
    }

    companion object {
        private const val ABNORMALDURATION = 300000
        const val INVALID_COORD: Float = 360.0f
        private const val NORMALDURATION = 1800000
        private val Network_Event_Filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        const val STOP: Int = -1
        private const val TAG = "WeatherInfoManager"
        var instance: WeatherInfoManager? = null
        private var mHandlerThread: HandlerThread? = null
        fun getWeatherInfo(
            context: Context,
            weatherStateReceiver: WeatherStateReceiver
        ): WeatherInfoManager? {
            if (!(instance == null || context == instance!!.mContext)) {
                instance!!.onStop()
            }
            if (instance == null) {
                instance = WeatherInfoManager(context, weatherStateReceiver)
                mHandlerThread!!.start()
            } else {
                instance!!.mWeatherStateReceiver = weatherStateReceiver
            }
            return instance
        }
    }
}
