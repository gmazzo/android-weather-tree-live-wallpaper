package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import io.github.gmazzo.android.livewallpaper.weather.WeatherType;
import io.github.gmazzo.android.livewallpaper.weather.forecast.LocationForecast;
import io.github.gmazzo.android.livewallpaper.weather.forecast.LocationForecastAPI;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherSettingsUtil.OnSettingChangeListener;

public class WeatherInfoManager implements Runnable {
    private static final int ABNORMALDURATION = 300000;
    public static final float INVALID_COORD = 360.0f;
    private static final String KEY_CITY_CODE = "WeatherInfoManager_CityCode";
    private static final String KEY_CITY_NAME = "WeatherInfoManager_CityName";
    private static final String KEY_CONDITION_TEXT_0 = "WeatherInfoManager_ConditionText_0";
    private static final String KEY_CONDITION_TEXT_1 = "WeatherInfoManager_ConditionText_1";
    private static final String KEY_CONDITION_TEXT_2 = "WeatherInfoManager_ConditionText_2";
    private static final String KEY_CURRENT_TEMP = "WeatherInfoManager_CurTemp";
    private static final String KEY_DATE_0 = "WeatherInfoManager_Date_0";
    private static final String KEY_DATE_1 = "WeatherInfoManager_Date_1";
    private static final String KEY_DATE_2 = "WeatherInfoManager_Date_2";
    private static final String KEY_DAYOFWEEK_0 = "WeatherInfoManager_DayOfWeek_0";
    private static final String KEY_DAYOFWEEK_1 = "WeatherInfoManager_DayOfWeek_1";
    private static final String KEY_DAYOFWEEK_2 = "WeatherInfoManager_DayOfWeek_2";
    private static final String KEY_HIGH_TEMP = "WeatherInfoManager_HighTemp";
    private static final String KEY_IS_FIRST_LAUNCH = "WeatherInfoManager_IsFirstLaunch";
    private static final String KEY_LATITUDE = "WeatherInfoManager_LatitudeCache";
    private static final String KEY_LONGITUDE = "WeatherInfoManager_LongitudeCache";
    private static final String KEY_LOW_TEMP = "WeatherInfoManager_LowTemp";
    private static final String KEY_MAXTEMP_0 = "WeatherInfoManager_MAXTEMP_0";
    private static final String KEY_MAXTEMP_1 = "WeatherInfoManager_MAXTEMP_1";
    private static final String KEY_MAXTEMP_2 = "WeatherInfoManager_MAXTEMP_2";
    private static final String KEY_MINTEMP_0 = "WeatherInfoManager_MINTEMP_0";
    private static final String KEY_MINTEMP_1 = "WeatherInfoManager_MINTEMP_1";
    private static final String KEY_MINTEMP_2 = "WeatherInfoManager_MINTEMP_2";
    private static final String KEY_SUNRISE_0 = "WeatherInfoManager_Sunrise_0";
    private static final String KEY_SUNRISE_1 = "WeatherInfoManager_Sunrise_1";
    private static final String KEY_SUNRISE_2 = "WeatherInfoManager_Sunrise_2";
    private static final String KEY_SUNSET_0 = "WeatherInfoManager_Sunset_0";
    private static final String KEY_SUNSET_1 = "WeatherInfoManager_Sunset_1";
    private static final String KEY_SUNSET_2 = "WeatherInfoManager_Sunset_2";
    private static final String KEY_TEMP_UNIT = "WeatherInfoManager_TempUnit";
    private static final String KEY_TIME_ZONE = "WeatherInfoManager_TimeZone";
    private static final String KEY_UPDATED_TIME = "WeatherInfoManager_updateTime";
    private static final String KEY_USE_LOCATION = "WeatherInfoManager_UseLocation";
    private static final String KEY_WEATHERCONDITION = "WeatherInfoManager_WeatherCondition";
    private static final String KEY_WEATHER_TYPE = "WeatherInfoManager_WeatherType";
    private static final String KEY_WEATHER_TYPE_0 = "WeatherInfoManager_WeatherType_0";
    private static final String KEY_WEATHER_TYPE_1 = "WeatherInfoManager_WeatherType_1";
    private static final String KEY_WEATHER_TYPE_2 = "WeatherInfoManager_WeatherType_2";
    private static final int NORMALDURATION = 1800000;
    private static final IntentFilter Network_Event_Filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    public static final int STOP = -1;
    private static final String TAG = "WeatherInfoManager";
    public static WeatherInfoManager instance = null;
    private static HandlerThread mHandlerThread;
    private String mCityCode = null;
    private Context mContext;
    private boolean mGetInfoSuccess = false;
    private Handler mHandler = null;
    private float mLatitude = 360.0f;
    private float mLongitude = 360.0f;
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!WeatherInfoManager.this.getResult() && WeatherInfoManager.this.isConnected() && WeatherInfoManager.this.mHandler != null) {
                WeatherInfoManager.this.mHandler.post(WeatherInfoManager.instance);
            }
        }
    };
    private ProgressDialog mProgressDialog = null;
    private boolean mRunning = true;
    public Integer mTempUnit = null;
    private boolean mUseCuLoc = false;
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
        this.mCityCode = prefs.getString(KEY_CITY_CODE, "600290");
        this.mUseCuLoc = prefs.getBoolean(KEY_USE_LOCATION, true);
        this.mLatitude = prefs.getFloat(KEY_LATITUDE, 360.0f);
        this.mLongitude = prefs.getFloat(KEY_LONGITUDE, 360.0f);
        this.mTempUnit = Integer.valueOf(prefs.getInt(KEY_TEMP_UNIT, 0));
        Log.i(TAG, "register network Receiver: context is " + this.mContext);
        this.mContext.registerReceiver(this.mNetworkReceiver, Network_Event_Filter);
        if (prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)) {
            Editor e = prefs.edit();
            e.putBoolean(KEY_IS_FIRST_LAUNCH, false);
            e.putString(WeatherSettingsUtil.KEY_TEMP_UNITS, "0");
            e.apply();
            WeatherSettingsUtil weatherSettingUtil = new WeatherSettingsUtil(this.mContext);
            weatherSettingUtil.refreshGeoLocation(false);
            weatherSettingUtil.setOnSettingChangeListener(new OnSettingChangeListener() {
                public void onGeoPositionChange(double longi, double lati) {
                }

                public void onLocNameChange(String stateName, String cityName) {
                }
            });
        }
    }

    private synchronized void saveWeather(WeatherType weatherType) {
        PreferenceManager.getDefaultSharedPreferences(this.mContext).edit()
                .putString(KEY_WEATHER_TYPE, weatherType.name())
                .apply();
    }

    public synchronized WeatherType getWeather() {
        return WeatherType.valueOf(PreferenceManager.getDefaultSharedPreferences(this.mContext)
                .getString(KEY_WEATHER_TYPE, WeatherType.CLOUDY.name()));
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
                    this.mHandler.postDelayed(this, (long) delay);
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
            return -1;
        }

        try {
            LocationForecastAPI.Forecast response = LocationForecast.INSTANCE
                    .getForecast(location.getLatitude(), location.getLongitude(), null)
                    .execute()
                    .body();

            List<LocationForecastAPI.Time> series = response.getProperties().getTimeSeries();
            if (!series.isEmpty()) {
                saveWeather(series.get(0).getData().getNextHour().getWeatherType());
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
