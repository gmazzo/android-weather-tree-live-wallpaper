package io.github.gmazzo.android.livewallpaper.weather;

import static io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager.INVALID_COORD;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherSettingsUtil;

public class WallpaperSettings extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener, OnSharedPreferenceChangeListener {
    public static final String PREFS_NAME = "WallpaperPrefs";
    public static final String PREF_DEF_COLORS = "pref_defaultcolors";
    public static final String PREF_LIGHT_COLOR1 = "pref_lightcolor1";
    public static final String PREF_LIGHT_COLOR2 = "pref_lightcolor2";
    public static final String PREF_LIGHT_COLOR3 = "pref_lightcolor3";
    public static final String PREF_LIGHT_COLOR4 = "pref_lightcolor4";
    public static final String PREF_NUM_CLOUDS = "pref_numclouds";
    public static final String PREF_NUM_WISPS = "pref_numwisps";
    public static final String PREF_RAIN_DENSITY = "pref_raindensity";
    public static final String PREF_USE_TOD = "pref_usetimeofday";
    public static final String PREF_WIND_SPEED = "pref_windspeed";
    private static final boolean DBG = false;
    public static final String PICK_CITY_ACTION = "io.github.gmazzo.android.livewallpaper.weather.action.PICK_CITY_ACTION";
    private static final String TAG = "WeatherSettings";
    private CheckBoxPreference mAutoOrManualLocation;
    private SharedPreferences mSharedPreferences;
    private ListPreference mTempUnitsPreference;
    private PreferenceScreen mWeatherCityPreference;
    private WeatherSettingsUtil mWeatherSettingsUtil;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.settings);

        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mWeatherCityPreference = (PreferenceScreen) findPreference(WeatherSettingsUtil.KEY_WEATHER_CITY);
        this.mTempUnitsPreference = (ListPreference) findPreference(WeatherSettingsUtil.KEY_TEMP_UNITS);
        this.mAutoOrManualLocation = (CheckBoxPreference) findPreference(WeatherSettingsUtil.KEY_LOCATION_SRC);
        this.mWeatherSettingsUtil = new WeatherSettingsUtil(this);
        if (this.mTempUnitsPreference != null) {
            this.mTempUnitsPreference.setTitle(this.mTempUnitsPreference.getEntry());
            this.mTempUnitsPreference.setOnPreferenceChangeListener(this);
        }
        if (this.mAutoOrManualLocation != null) {
            if (this.mWeatherSettingsUtil.useCurGeoLoc()) {
                String locTitle = null;
                String geoCityName = this.mWeatherSettingsUtil.getGeoCityName();
                String geoStateName = this.mWeatherSettingsUtil.getGeoStateName();
                float geoLati = this.mWeatherSettingsUtil.getLatitude();
                float geoLongi = this.mWeatherSettingsUtil.getLongitude();
                if (geoCityName != null && geoStateName != null) {
                    locTitle = geoCityName + ", " + geoStateName;
                } else if (!(geoLati == INVALID_COORD || geoLongi == INVALID_COORD)) {
                    locTitle = getString(R.string.current_location);
                }
                if (locTitle != null) {
                    this.mAutoOrManualLocation.setTitle(locTitle);
                } else {
                    this.mAutoOrManualLocation.setTitle(getText(R.string.empty_replacer));
                }
            } else {
                this.mAutoOrManualLocation.setTitle(this.mSharedPreferences.getString(WeatherSettingsUtil.KEY_CITY_NAME, "Chicago") + ", " + this.mSharedPreferences.getString(WeatherSettingsUtil.KEY_STATE_NAME, "IL"));
            }
            this.mAutoOrManualLocation.setOnPreferenceChangeListener(this);
        }
        if (this.mWeatherCityPreference != null) {
            if (this.mWeatherSettingsUtil.useCurGeoLoc()) {
                this.mWeatherCityPreference.setEnabled(DBG);
            } else {
                this.mWeatherCityPreference.setEnabled(true);
                this.mWeatherCityPreference.setTitle(this.mSharedPreferences.getString(WeatherSettingsUtil.KEY_CITY_NAME, "Chicago") + ", " + this.mSharedPreferences.getString(WeatherSettingsUtil.KEY_STATE_NAME, "IL"));
                this.mWeatherCityPreference.setOnPreferenceChangeListener(this);
                this.mWeatherCityPreference.setOnPreferenceClickListener(this);
            }
        }
        this.mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (BuildConfig.DEBUG && bundle == null) {
            startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    Bundle resultExtras = data.getExtras();
                    if (resultExtras == null) {
                        Log.v(TAG, "the resultExtras == null ");
                        break;
                    }
                    CityInfo ci = resultExtras.getParcelable(SearchCity.CITY_INFO);
                    if (ci != null) {
                        setCity(ci);
                        break;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (WeatherSettingsUtil.KEY_USE_GPS.equals(preference.getKey())) {
            Boolean newValue2 = (Boolean) newValue;
        } else if (!WeatherSettingsUtil.KEY_WEATHER_CITY.equals(preference.getKey())) {
            if (WeatherSettingsUtil.KEY_TEMP_UNITS.endsWith(preference.getKey())) {
                if (this.mTempUnitsPreference != null) {
                    this.mTempUnitsPreference.setTitle(this.mTempUnitsPreference.getEntries()[Integer.parseInt((String) newValue)]);
                }
            } else if (WeatherSettingsUtil.KEY_LOCATION_SRC.equals(preference.getKey())) {
                if ((Boolean) newValue) {
                    if (this.mWeatherCityPreference != null) {
                        this.mWeatherCityPreference.setEnabled(DBG);
                    }
                    if (this.mWeatherSettingsUtil.isNetworkLocServOnBySetting()) {
                        this.mWeatherSettingsUtil.refreshGeoLocation(true);
                    } else {
                        Log.v(TAG, "call mWeatherSettingUtil.handleLocServOff");
                        this.mWeatherSettingsUtil.handleLocServOff();
                    }
                } else {
                    if (this.mWeatherCityPreference != null) {
                        this.mWeatherCityPreference.setEnabled(true);
                    }
                    Intent intent = new Intent();
                    intent.setAction(PICK_CITY_ACTION);
                    startActivityForResult(intent, 0);
                }
            }
        }
        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!WeatherSettingsUtil.KEY_WEATHER_CITY.equals(key)) {
            float longiStr;
            float latiStr;
            String cityStr;
            String stateStr;
            if (WeatherSettingsUtil.KEY_GEO_LONGITUDE.equals(key)) {
                longiStr = this.mWeatherSettingsUtil.getLongitude();
                latiStr = this.mWeatherSettingsUtil.getLatitude();
                if (longiStr == INVALID_COORD || latiStr == INVALID_COORD) {
                    this.mAutoOrManualLocation.setTitle(getText(R.string.empty_replacer));
                } else {
                    this.mAutoOrManualLocation.setTitle(latiStr + ", " + longiStr);
                }
            } else if (WeatherSettingsUtil.KEY_GEO_LATITUDE.equals(key)) {
                longiStr = this.mWeatherSettingsUtil.getLongitude();
                latiStr = this.mWeatherSettingsUtil.getLatitude();
                if (longiStr == INVALID_COORD || latiStr == INVALID_COORD) {
                    this.mAutoOrManualLocation.setTitle(getText(R.string.empty_replacer));
                } else {
                    this.mAutoOrManualLocation.setTitle(latiStr + ", " + longiStr);
                }
            } else if (WeatherSettingsUtil.KEY_GEO_STATE_NAME.equals(key)) {
                stateStr = this.mWeatherSettingsUtil.getGeoStateName();
                cityStr = this.mWeatherSettingsUtil.getGeoCityName();
                if (stateStr == null || cityStr == null) {
                    this.mAutoOrManualLocation.setTitle(getText(R.string.empty_replacer));
                } else {
                    this.mAutoOrManualLocation.setTitle(cityStr + ", " + stateStr);
                }
            } else if (WeatherSettingsUtil.KEY_GEO_CITY_NAME.equals(key)) {
                stateStr = this.mWeatherSettingsUtil.getGeoStateName();
                cityStr = this.mWeatherSettingsUtil.getGeoCityName();
                if (stateStr == null || cityStr == null) {
                    this.mAutoOrManualLocation.setTitle(getText(R.string.empty_replacer));
                } else {
                    this.mAutoOrManualLocation.setTitle(cityStr + ", " + stateStr);
                }
            } else if (WeatherSettingsUtil.KEY_CITY_NAME.equals(key) || WeatherSettingsUtil.KEY_STATE_NAME.equals(key)) {
                this.mAutoOrManualLocation.setTitle(sharedPreferences.getString(WeatherSettingsUtil.KEY_CITY_NAME, "IL") + ", " + sharedPreferences.getString(WeatherSettingsUtil.KEY_STATE_NAME, "Chicago"));
            }
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        if (WeatherSettingsUtil.KEY_WEATHER_CITY.equals(preference.getKey())) {
            Intent intent = new Intent();
            intent.setAction(PICK_CITY_ACTION);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    private boolean setCity(CityInfo city) {
        String cityName = city.getCity();
        String stateName = city.getState();
        String cityCode = city.getCityCode();
        float lat = (float) city.getLatitude();
        float lng = (float) city.getLongitude();
        if (this.mWeatherCityPreference != null) {
            this.mWeatherCityPreference.setTitle(cityName + ", " + stateName);
        } else if (this.mAutoOrManualLocation != null) {
            this.mAutoOrManualLocation.setTitle(cityName + ", " + stateName);
        }
        if (this.mSharedPreferences != null) {
            Editor editor = this.mSharedPreferences.edit();
            editor.putString(WeatherSettingsUtil.KEY_WEATHER_CITY, cityCode);
            editor.putString(WeatherSettingsUtil.KEY_CITY_NAME, cityName);
            editor.putString(WeatherSettingsUtil.KEY_STATE_NAME, stateName);
            editor.putFloat(WeatherSettingsUtil.KEY_GEO_LATITUDE, lat);
            editor.putFloat(WeatherSettingsUtil.KEY_GEO_LONGITUDE, lng);
            editor.commit();
        }
        return true;
    }

}
