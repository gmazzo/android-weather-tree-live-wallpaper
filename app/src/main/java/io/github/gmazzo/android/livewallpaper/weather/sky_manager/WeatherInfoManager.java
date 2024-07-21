package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import io.github.gmazzo.android.livewallpaper.weather.LocationProvider;
import io.github.gmazzo.android.livewallpaper.weather.SettingsUtils;
import io.github.gmazzo.android.livewallpaper.weather.api.forecast.LocationForecast;
import io.github.gmazzo.android.livewallpaper.weather.api.forecast.LocationForecastAPI;

public class WeatherInfoManager implements Runnable {
    private static final int ABNORMALDURATION = 300000;
    public static final float INVALID_COORD = 360.0f;
    private static final int NORMALDURATION = 1800000;
    private static final IntentFilter Network_Event_Filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    public static final int STOP = -1;
    private static final String TAG = "WeatherInfoManager";
    public static WeatherInfoManager instance = null;
    private static HandlerThread mHandlerThread;
    private final Context mContext;
    private boolean mGetInfoSuccess = false;
    private Handler mHandler = null;
    private final BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!WeatherInfoManager.this.getResult() && WeatherInfoManager.this.isConnected() && WeatherInfoManager.this.mHandler != null) {
                WeatherInfoManager.this.mHandler.post(WeatherInfoManager.instance);
            }
        }
    };
    private boolean mRunning = true;
    private WeatherStateReceiver mWeatherStateReceiver = null;

    public interface WeatherStateReceiver {
        void updateWeatherState();
    }

    public static WeatherInfoManager getWeatherInfo(Context context, WeatherStateReceiver weatherStateReceiver) {
        if (!(instance == null || context.equals(instance.mContext))) {
            instance.onStop();
        }
        if (instance == null) {
            instance = new WeatherInfoManager(context, weatherStateReceiver);
            mHandlerThread.start();
        } else {
            instance.mWeatherStateReceiver = weatherStateReceiver;
        }
        return instance;
    }

    private WeatherInfoManager(Context context, WeatherStateReceiver weatherStateReceiver) {
        this.mContext = context;
        this.mWeatherStateReceiver = weatherStateReceiver;
        mHandlerThread = new HandlerThread("InfoHandlerThread") {
            public void onLooperPrepared() {
                synchronized (WeatherInfoManager.mHandlerThread) {
                    WeatherInfoManager.this.mHandler = new Handler();
                }
            }
        };
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        Log.i(TAG, "register network Receiver: context is " + this.mContext);
        this.mContext.registerReceiver(this.mNetworkReceiver, Network_Event_Filter);
    }

    public void run() {
        if (isConnected() && this.mRunning) {
            int delay = ABNORMALDURATION;
            if (-1 == getWeatherInformation()) {
                setResult(false);
            } else {
                setResult(true);
                delay = NORMALDURATION;
            }
            synchronized (this) {
                if (this.mHandler != null) {
                    this.mWeatherStateReceiver.updateWeatherState();
                    this.mHandler.postDelayed(this, delay);
                }
            }
            return;
        }
        setResult(false);
        this.mWeatherStateReceiver.updateWeatherState();
    }

    private synchronized void setResult(boolean result) {
        this.mGetInfoSuccess = result;
    }

    private synchronized boolean getResult() {
        return this.mGetInfoSuccess;
    }

    private int getWeatherInformation() {
        Location location = LocationProvider.INSTANCE.getLastKnownLocation(this.mContext);

        if (location == null) {
            SettingsUtils.setLatitude(mContext, null);
            SettingsUtils.setLongitude(mContext, null);
            return -1;
        }

        SettingsUtils.setLatitude(mContext, (float) location.getLatitude());
        SettingsUtils.setLongitude(mContext, (float) location.getLongitude());
        try {
            LocationForecastAPI.Forecast response = LocationForecast.INSTANCE
                    .getForecast(location.getLatitude(), location.getLongitude(), null)
                    .execute()
                    .body();

            List<LocationForecastAPI.Time> series = response.getProperties().getTimeSeries();
            if (!series.isEmpty()) {
                SettingsUtils.setWeatherConditions(mContext, series.get(0).getData().getNextHour().getWeatherType());
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean isConnected() {
        NetworkInfo info = ((ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        }
        return false;
    }

    public synchronized void onStop() {
        Log.i(TAG, "unregister network Receiver context is " + this.mContext);
        try {
            this.mContext.unregisterReceiver(this.mNetworkReceiver);
        } catch (IllegalArgumentException iae) {
            Log.d(TAG, "receier never been registered. " + iae);
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        this.mHandler = null;
        instance = null;
    }

    public void update(long delay) {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this);
            if (delay > 0) {
                this.mRunning = true;
                this.mHandler.postDelayed(this, delay);
                Log.d(TAG, "postDelayed: " + delay);
                return;
            } else if (delay == 0) {
                this.mRunning = true;
                this.mHandler.post(this);
                return;
            } else {
                this.mRunning = false;
                return;
            }
        }
        synchronized (mHandlerThread) {
            while (this.mHandler == null) {
                try {
                    mHandlerThread.wait(100);
                } catch (InterruptedException e) {
                    Log.w(TAG, e.toString());
                }
            }
        }
    }
}
