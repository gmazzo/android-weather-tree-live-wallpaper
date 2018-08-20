package gs.weather.sky_manager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import gs.weather.sky_manager.WeatherCondition.ForcastCondition;
import gs.weather.sky_manager.WeatherCondition.WeatherResult;
import gs.weather.sky_manager.WeatherSettingsUtil.OnSettingChangeListener;

public class WeatherInfoManager implements Runnable {
    private static final int ABNORMALDURATION = 300000;
    private static final boolean DEBUG = false;
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
    private boolean mGetInfoSuccess = DEBUG;
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
    private boolean mUseCuLoc = DEBUG;
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
            e.putBoolean(KEY_IS_FIRST_LAUNCH, DEBUG);
            e.putString(WeatherSettingsUtil.KEY_TEMP_UNITS, "0");
            e.commit();
            WeatherSettingsUtil weatherSettingUtil = new WeatherSettingsUtil(this.mContext);
            weatherSettingUtil.refreshGeoLocation(DEBUG);
            weatherSettingUtil.setOnSettingChangeListener(new OnSettingChangeListener() {
                public void onGeoPositionChange(double longi, double lati) {
                }

                public void onLocNameChange(String stateName, String cityName) {
                }
            });
        }
    }

    private synchronized void saveWeather(WeatherResult wr) {
        int i = 0;
        synchronized (this) {
            WeatherSettingsUtil weatherSettingUtil = new WeatherSettingsUtil(this.mContext);
            Editor e = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
            e.putLong(KEY_UPDATED_TIME, System.currentTimeMillis());
            this.mCityCode = weatherSettingUtil.getCityCode();
            e.putString(KEY_CITY_CODE, this.mCityCode);
            e.putString(KEY_CITY_NAME, wr.city);
            this.mUseCuLoc = weatherSettingUtil.useCurGeoLoc();
            e.putBoolean(KEY_USE_LOCATION, this.mUseCuLoc);
            this.mLatitude = weatherSettingUtil.getLatitude();
            e.putFloat(KEY_LATITUDE, this.mLatitude);
            this.mLongitude = weatherSettingUtil.getLongitude();
            e.putFloat(KEY_LONGITUDE, this.mLongitude);
            e.putString(KEY_TIME_ZONE, wr.timeZone == null ? TimeZone.getDefault().getID() : wr.timeZone);
            e.putInt(KEY_CURRENT_TEMP, wr.tempCurrent == null ? 0 : wr.tempCurrent.intValue());
            e.putInt(KEY_HIGH_TEMP, wr.tempHigh == null ? 0 : wr.tempHigh.intValue());
            e.putInt(KEY_LOW_TEMP, wr.tempLow == null ? 0 : wr.tempLow.intValue());
            e.putInt(KEY_WEATHER_TYPE, wr.type == null ? 0 : wr.type.intValue());
            this.mTempUnit = weatherSettingUtil.getTempUnit();
            e.putInt(KEY_TEMP_UNIT, this.mTempUnit.intValue());
            e.putString(KEY_WEATHERCONDITION, wr.conditionTxt == null ? WeatherSettingsUtil.INVALID_STR : wr.conditionTxt);
            String date = String.valueOf(Calendar.getInstance().get(3)) + "/" + String.valueOf(Calendar.getInstance().get(5)) + "/" + String.valueOf(Calendar.getInstance().get(1));
            ArrayList<ForcastCondition> forcastList = wr.forcastList;
            if (forcastList != null) {
                WeatherCondition weatherCondition;
                ForcastCondition f0 = (ForcastCondition) forcastList.get(0);
                if (f0 == null) {
                    weatherCondition = new WeatherCondition();
                    weatherCondition.getClass();
                    f0 = new ForcastCondition();
                }
                e.putInt(KEY_WEATHER_TYPE_0, f0.type == null ? 0 : f0.type.intValue());
                e.putString(KEY_DATE_0, f0.date == null ? date : f0.date);
                e.putString(KEY_DAYOFWEEK_0, f0.week == null ? WeatherSettingsUtil.INVALID_STR : f0.week);
                e.putString(KEY_SUNRISE_0, f0.sunrise == null ? "6:00 AM" : f0.sunrise);
                e.putString(KEY_SUNSET_0, f0.sunset == null ? "6:00 PM" : f0.sunset);
                e.putString(KEY_CONDITION_TEXT_0, f0.conditionTxt == null ? WeatherSettingsUtil.INVALID_STR : f0.conditionTxt);
                e.putInt(KEY_MAXTEMP_0, f0.tempHigh == null ? 0 : f0.tempHigh.intValue());
                e.putInt(KEY_MINTEMP_0, f0.tempLow == null ? 0 : f0.tempLow.intValue());
                ForcastCondition f1 = (ForcastCondition) forcastList.get(1);
                if (f1 == null) {
                    weatherCondition = new WeatherCondition();
                    weatherCondition.getClass();
                    f1 = new ForcastCondition();
                }
                e.putInt(KEY_WEATHER_TYPE_1, f1.type == null ? 0 : f1.type.intValue());
                e.putString(KEY_DATE_1, f1.date == null ? date : f1.date);
                e.putString(KEY_DAYOFWEEK_1, f1.week == null ? WeatherSettingsUtil.INVALID_STR : f1.week);
                e.putString(KEY_SUNRISE_1, f1.sunrise == null ? "6:00 AM" : f1.sunrise);
                e.putString(KEY_SUNSET_1, f1.sunset == null ? "6:00 PM" : f1.sunset);
                e.putString(KEY_CONDITION_TEXT_1, f1.conditionTxt == null ? WeatherSettingsUtil.INVALID_STR : f1.conditionTxt);
                e.putInt(KEY_MAXTEMP_1, f1.tempHigh == null ? 0 : f1.tempHigh.intValue());
                e.putInt(KEY_MINTEMP_1, f1.tempLow == null ? 0 : f1.tempLow.intValue());
                ForcastCondition f2 = (ForcastCondition) forcastList.get(2);
                if (f2 == null) {
                    weatherCondition = new WeatherCondition();
                    weatherCondition.getClass();
                    f2 = new ForcastCondition();
                }
                e.putInt(KEY_WEATHER_TYPE_2, f2.type == null ? 0 : f2.type.intValue());
                String str = KEY_DATE_2;
                if (f2.date != null) {
                    date = f2.date;
                }
                e.putString(str, date);
                e.putString(KEY_DAYOFWEEK_2, f2.week == null ? WeatherSettingsUtil.INVALID_STR : f2.week);
                e.putString(KEY_SUNRISE_2, f2.sunrise == null ? "6:00 AM" : f2.sunrise);
                e.putString(KEY_SUNSET_2, f2.sunset == null ? "6:00 PM" : f2.sunset);
                e.putString(KEY_CONDITION_TEXT_2, f2.conditionTxt == null ? WeatherSettingsUtil.INVALID_STR : f2.conditionTxt);
                e.putInt(KEY_MAXTEMP_2, f2.tempHigh == null ? 0 : f2.tempHigh.intValue());
                str = KEY_MINTEMP_2;
                if (f2.tempLow != null) {
                    i = f2.tempLow.intValue();
                }
                e.putInt(str, i);
            }
            e.commit();
        }
    }

    public synchronized WeatherResult getWeather() {
        WeatherResult wr;
        Integer num = null;
        int i = 0;
        synchronized (this) {
            String string;
            Integer valueOf;
            String string2;
            Integer valueOf2;
            WeatherCondition wc = new WeatherCondition();
            wc.getClass();
            wr = new WeatherResult();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
            wr.city = this.mGetInfoSuccess ? prefs.getString(KEY_CITY_NAME, "Chicago") : "  ";
            if (this.mGetInfoSuccess) {
                string = prefs.getString(KEY_TIME_ZONE, TimeZone.getDefault().getID());
            } else {
                string = null;
            }
            wr.timeZone = string;
            if (this.mGetInfoSuccess) {
                valueOf = Integer.valueOf(prefs.getInt(KEY_CURRENT_TEMP, 0));
            } else {
                valueOf = null;
            }
            wr.tempCurrent = valueOf;
            if (this.mGetInfoSuccess) {
                valueOf = Integer.valueOf(prefs.getInt(KEY_HIGH_TEMP, 0));
            } else {
                valueOf = null;
            }
            wr.tempHigh = valueOf;
            if (this.mGetInfoSuccess) {
                valueOf = Integer.valueOf(prefs.getInt(KEY_LOW_TEMP, 0));
            } else {
                valueOf = null;
            }
            wr.tempLow = valueOf;
            if (this.mGetInfoSuccess) {
                i = prefs.getInt(KEY_WEATHER_TYPE, 0);
            }
            wr.type = Integer.valueOf(i);
            if (this.mGetInfoSuccess) {
                string2 = prefs.getString(KEY_WEATHERCONDITION, WeatherSettingsUtil.INVALID_STR);
            } else {
                string2 = null;
            }
            wr.conditionTxt = string2;
            String date = String.valueOf(Calendar.getInstance().get(3)) + "/" + String.valueOf(Calendar.getInstance().get(5)) + "/" + String.valueOf(Calendar.getInstance().get(1));
            wr.forcastList = new ArrayList();
            wc.getClass();
            ForcastCondition f0 = new ForcastCondition();
            f0.type = Integer.valueOf(prefs.getInt(KEY_WEATHER_TYPE_0, 0));
            f0.date = prefs.getString(KEY_DATE_0, date);
            f0.week = prefs.getString(KEY_DAYOFWEEK_0, WeatherSettingsUtil.INVALID_STR);
            f0.sunrise = prefs.getString(KEY_SUNRISE_0, "6:00 AM");
            f0.sunset = prefs.getString(KEY_SUNSET_0, "6:00 PM");
            if (this.mGetInfoSuccess) {
                string2 = prefs.getString(KEY_CONDITION_TEXT_0, WeatherSettingsUtil.INVALID_STR);
            } else {
                string2 = null;
            }
            f0.conditionTxt = string2;
            if (this.mGetInfoSuccess) {
                valueOf2 = Integer.valueOf(prefs.getInt(KEY_MAXTEMP_0, 0));
            } else {
                valueOf2 = null;
            }
            f0.tempHigh = valueOf2;
            if (this.mGetInfoSuccess) {
                valueOf2 = Integer.valueOf(prefs.getInt(KEY_MINTEMP_0, 0));
            } else {
                valueOf2 = null;
            }
            f0.tempLow = valueOf2;
            wr.forcastList.add(f0);
            wc.getClass();
            ForcastCondition f1 = new ForcastCondition();
            f1.type = Integer.valueOf(prefs.getInt(KEY_WEATHER_TYPE_1, 0));
            f1.date = prefs.getString(KEY_DATE_1, date);
            f1.week = prefs.getString(KEY_DAYOFWEEK_1, WeatherSettingsUtil.INVALID_STR);
            f1.sunrise = prefs.getString(KEY_SUNRISE_1, "6:00 AM");
            f1.sunset = prefs.getString(KEY_SUNSET_1, "6:00 PM");
            if (this.mGetInfoSuccess) {
                string2 = prefs.getString(KEY_CONDITION_TEXT_1, WeatherSettingsUtil.INVALID_STR);
            } else {
                string2 = null;
            }
            f1.conditionTxt = string2;
            if (this.mGetInfoSuccess) {
                valueOf2 = Integer.valueOf(prefs.getInt(KEY_MAXTEMP_1, 0));
            } else {
                valueOf2 = null;
            }
            f1.tempHigh = valueOf2;
            if (this.mGetInfoSuccess) {
                valueOf2 = Integer.valueOf(prefs.getInt(KEY_MINTEMP_1, 0));
            } else {
                valueOf2 = null;
            }
            f1.tempLow = valueOf2;
            wr.forcastList.add(f1);
            wc.getClass();
            ForcastCondition f2 = new ForcastCondition();
            f2.type = Integer.valueOf(prefs.getInt(KEY_WEATHER_TYPE_2, 0));
            f2.date = prefs.getString(KEY_DATE_2, date);
            f2.week = prefs.getString(KEY_DAYOFWEEK_2, WeatherSettingsUtil.INVALID_STR);
            f2.sunrise = prefs.getString(KEY_SUNRISE_2, "6:00 AM");
            f2.sunset = prefs.getString(KEY_SUNSET_2, "6:00 PM");
            if (this.mGetInfoSuccess) {
                string2 = prefs.getString(KEY_CONDITION_TEXT_2, WeatherSettingsUtil.INVALID_STR);
            } else {
                string2 = null;
            }
            f2.conditionTxt = string2;
            if (this.mGetInfoSuccess) {
                valueOf2 = Integer.valueOf(prefs.getInt(KEY_MAXTEMP_2, 0));
            } else {
                valueOf2 = null;
            }
            f2.tempHigh = valueOf2;
            if (this.mGetInfoSuccess) {
                num = Integer.valueOf(prefs.getInt(KEY_MINTEMP_2, 0));
            }
            f2.tempLow = num;
            wr.forcastList.add(f2);
        }
        return wr;
    }

    public long getUpdateTime() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(KEY_UPDATED_TIME, 0);
    }

    public String getCurrentDate() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(KEY_DATE_0, String.valueOf(Calendar.getInstance().get(2) + 1) + "/" + String.valueOf(Calendar.getInstance().get(5)) + "/" + String.valueOf(Calendar.getInstance().get(1)));
    }

    public String getTimeZone() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(KEY_TIME_ZONE, TimeZone.getDefault().getID());
    }

    public String getSunrise() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(KEY_SUNRISE_0, "6:00 AM");
    }

    public String getSunset() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(KEY_SUNSET_0, "6:00 PM");
    }

    public boolean isExpired() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        WeatherSettingsUtil weatherSettingUtil = new WeatherSettingsUtil(this.mContext);
        long updateTime = prefs.getLong(KEY_UPDATED_TIME, 0);
        long life = System.currentTimeMillis() - updateTime;
        boolean rv = (life <= 0 || life > 1800000 || updateTime == 0) ? true : DEBUG;
        if (!rv) {
            boolean useCuLoc = weatherSettingUtil.useCurGeoLoc();
            if (this.mUseCuLoc != useCuLoc) {
                rv = true;
                this.mUseCuLoc = useCuLoc;
            } else {
                int tempUnit = weatherSettingUtil.getTempUnit().intValue();
                if (this.mTempUnit.intValue() != tempUnit) {
                    rv = true;
                    this.mTempUnit = Integer.valueOf(tempUnit);
                } else if (this.mUseCuLoc) {
                    float latitude = weatherSettingUtil.getLatitude();
                    float longitude = weatherSettingUtil.getLongitude();
                    if (latitude == 360.0f || longitude == 360.0f) {
                        rv = true;
                    } else if (!(latitude == this.mLatitude && longitude == this.mLongitude)) {
                        rv = true;
                    }
                } else {
                    String cityCode = weatherSettingUtil.getCityCode();
                    if (!cityCode.equals(this.mCityCode)) {
                        rv = true;
                        this.mCityCode = cityCode;
                    }
                }
            }
        }
        setResult(!rv ? true : DEBUG);
        if (rv) {
            Editor e = prefs.edit();
            e.putLong(KEY_UPDATED_TIME, 0);
            e.commit();
        }
        return rv;
    }

    public String getURL() {
        if (this.mUseCuLoc) {
            return "http://www.accuweather.com/m/%s.aspx?p=motosht&lat=" + this.mLatitude + "&lon=" + this.mLongitude;
        }
        return "http://www.accuweather.com/m/%s.aspx?p=motosht&loc=" + this.mCityCode;
    }

    public String getURLs() {
        if (this.mUseCuLoc) {
            return "http://www.accuweather.com/m/%s.aspx?p=motosht&slat=" + this.mLatitude + "&slon=" + this.mLongitude;
        }
        return "http://www.accuweather.com/m/%s.aspx?p=motosht&loc=" + this.mCityCode;
    }

    public void run() {
        if (isConnected() && this.mRunning) {
            int delay = ABNORMALDURATION;
            if (-1 == getWeatherInformation()) {
                setResult(DEBUG);
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
        setResult(DEBUG);
        this.mWeatherStateReceiver.updateWeatherState();
    }

    private synchronized void setResult(boolean result) {
        this.mGetInfoSuccess = result;
    }

    private synchronized boolean getResult() {
        return this.mGetInfoSuccess;
    }

    private int getWeatherInformation() {
        int rv = -1;
        WeatherCondition wc = new WeatherCondition();
        wc.getClass();
        WeatherResult w = new WeatherResult();
        WeatherSettingsUtil weatherSettingUtil = new WeatherSettingsUtil(this.mContext);
        float latitude = weatherSettingUtil.getLatitude();
        float longitude = weatherSettingUtil.getLongitude();
        String cityCode = weatherSettingUtil.getCityCode();
        Integer tempUnit = weatherSettingUtil.getTempUnit();
        boolean useCuLoc = weatherSettingUtil.useCurGeoLoc();
        if (useCuLoc && latitude != 360.0f && longitude != 360.0f) {
            rv = wc.getWeather((double) latitude, (double) longitude, tempUnit, w).intValue();
        } else if (!(useCuLoc || cityCode == null)) {
            rv = wc.getWeather(cityCode, tempUnit, w).intValue();
        }
        if (rv != -1) {
            saveWeather(w);
        }
        return rv;
    }

    private boolean isConnected() {
        NetworkInfo info = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        }
        return DEBUG;
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
                this.mRunning = DEBUG;
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
