package com.hm.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.hm.weather.sky_manager.WeatherCondition.CityInfo;
import com.hm.weather.sky_manager.WeatherSettingsUtil;

public class WallpaperSettings extends BaseWallpaperSettings implements OnPreferenceChangeListener, OnPreferenceClickListener, OnSharedPreferenceChangeListener {
    private static final boolean DBG = false;
    static final String DEFAULT_BALLOON_COUNT = "5";
    static final String DEFAULT_CLOUD_COUNT = "20";
    static final String DEFAULT_TOD1_COLOR = "0.5 0.5 0.75 1";
    static final String DEFAULT_TOD2_COLOR = "1 0.73 0.58 1";
    static final String DEFAULT_TOD3_COLOR = "1 1 1 1";
    static final String DEFAULT_TOD4_COLOR = "1 0.85 0.75 1";
    static final String DEFAULT_WISP_COUNT = "5";
    public static final String PICK_CITY_ACTION = "com.hm.weather.action.PICK_CITY_ACTION";
    private static final String TAG = "WeatherSettings";
    private int MENU_CLICK_COUNT = 0;
    PrefButtonSliderListener balloonSliderListener;
    PrefButtonSliderListener cloudsSliderListener;
    PrefDefaultColorsListener defaultColorsListener;
    PrefButtonSliderListener frequencySliderListener;
    private CheckBoxPreference mAutoOrManualLocation;
    private SharedPreferences mSharedPreferences;
    private ListPreference mTempUnitsPreference;
    private PreferenceScreen mWeatherCityPreference;
    private WeatherSettingsUtil mWeatherSettingsUtil;
    PrefButtonColorListener tod1ColorListener;
    PrefButtonColorListener tod2ColorListener;
    PrefButtonColorListener tod3ColorListener;
    PrefButtonColorListener tod4ColorListener;
    PrefButtonSliderListener wispsSliderListener;

    private class PrefDefaultColorsListener implements OnPreferenceClickListener {
        private PrefDefaultColorsListener() {
        }

        public boolean onPreferenceClick(Preference preference) {
            Editor editor = preference.getSharedPreferences().edit();
            editor.putString(BaseWallpaperSettings.PREF_LIGHT_COLOR1, WallpaperSettings.DEFAULT_TOD1_COLOR);
            editor.putString(BaseWallpaperSettings.PREF_LIGHT_COLOR2, WallpaperSettings.DEFAULT_TOD2_COLOR);
            editor.putString(BaseWallpaperSettings.PREF_LIGHT_COLOR3, WallpaperSettings.DEFAULT_TOD3_COLOR);
            editor.putString(BaseWallpaperSettings.PREF_LIGHT_COLOR4, WallpaperSettings.DEFAULT_TOD4_COLOR);
            editor.commit();
            return true;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.settings);
        this.context = this;
        try {
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
                    } else if (!(geoLati == 360.0f || geoLongi == 360.0f)) {
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
            Resources res = getResources();
            PreferenceScreen ps = getPreferenceScreen();
            Preference pref_numclouds = ps.findPreference(BaseWallpaperSettings.PREF_NUM_CLOUDS);
            this.cloudsSliderListener = new PrefButtonSliderListener(res.getString(R.string.pref_numclouds), DEFAULT_CLOUD_COUNT, 8, 32, "8", "32");
            pref_numclouds.setOnPreferenceClickListener(this.cloudsSliderListener);
            Preference pref_numwisps = ps.findPreference(BaseWallpaperSettings.PREF_NUM_WISPS);
            this.wispsSliderListener = new PrefButtonSliderListener(res.getString(R.string.pref_numwisps), "5", 0, 12, "0", "12");
            pref_numwisps.setOnPreferenceClickListener(this.wispsSliderListener);
            Preference pref_balloon = ps.findPreference("pref_ballooncounttarget");
            this.balloonSliderListener = new PrefButtonSliderListener(res.getString(R.string.pref_ballooncounttarget), "5", 1, 10, "1", "10");
            pref_balloon.setOnPreferenceClickListener(this.balloonSliderListener);
            Preference pref_lightcolor1 = ps.findPreference(BaseWallpaperSettings.PREF_LIGHT_COLOR1);
            this.tod1ColorListener = new PrefButtonColorListener(DEFAULT_TOD1_COLOR);
            pref_lightcolor1.setOnPreferenceClickListener(this.tod1ColorListener);
            Preference pref_lightcolor2 = ps.findPreference(BaseWallpaperSettings.PREF_LIGHT_COLOR2);
            this.tod2ColorListener = new PrefButtonColorListener(DEFAULT_TOD2_COLOR);
            pref_lightcolor2.setOnPreferenceClickListener(this.tod2ColorListener);
            Preference pref_lightcolor3 = ps.findPreference(BaseWallpaperSettings.PREF_LIGHT_COLOR3);
            this.tod3ColorListener = new PrefButtonColorListener(DEFAULT_TOD3_COLOR);
            pref_lightcolor3.setOnPreferenceClickListener(this.tod3ColorListener);
            Preference pref_lightcolor4 = ps.findPreference(BaseWallpaperSettings.PREF_LIGHT_COLOR4);
            this.tod4ColorListener = new PrefButtonColorListener(DEFAULT_TOD4_COLOR);
            pref_lightcolor4.setOnPreferenceClickListener(this.tod4ColorListener);
            Preference pref_defaultcolors = ps.findPreference(BaseWallpaperSettings.PREF_DEF_COLORS);
            WallpaperSettings wallpaperSettings = this;
            this.defaultColorsListener = new PrefDefaultColorsListener();
            pref_defaultcolors.setOnPreferenceClickListener(this.defaultColorsListener);
        } catch (NullPointerException npe) {
            Log.w(TAG, "NullPointerException");
            npe.printStackTrace();
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
                    CityInfo ci = (CityInfo) resultExtras.getParcelable(SearchCity.CITY_INFO);
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
                if (((Boolean) newValue).booleanValue()) {
                    if (this.mWeatherCityPreference != null) {
                        this.mWeatherCityPreference.setEnabled(DBG);
                    }
                    if (this.mWeatherSettingsUtil.isNetworkLocServOnBySetting().booleanValue()) {
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
                if (longiStr == 360.0f || latiStr == 360.0f) {
                    this.mAutoOrManualLocation.setTitle(getText(R.string.empty_replacer));
                } else {
                    this.mAutoOrManualLocation.setTitle(latiStr + ", " + longiStr);
                }
            } else if (WeatherSettingsUtil.KEY_GEO_LATITUDE.equals(key)) {
                longiStr = this.mWeatherSettingsUtil.getLongitude();
                latiStr = this.mWeatherSettingsUtil.getLatitude();
                if (longiStr == 360.0f || latiStr == 360.0f) {
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

    private boolean setCity(String cityName, String stateName, String cityCode) {
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
            editor.commit();
        }
        return true;
    }

    private boolean setCity(CityInfo city) {
        String cityName = city.mCity;
        String stateName = city.mState;
        String cityCode = city.mcityCode;
        float lat = (float) city.mLatitude;
        float lng = (float) city.mLongitude;
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

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        this.MENU_CLICK_COUNT = 0;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        this.MENU_CLICK_COUNT++;
        if (this.MENU_CLICK_COUNT > 5) {
            openTestActivity();
        }
        return super.onMenuOpened(featureId, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case BaseWallpaperSettings.REQUESTCODE_PREF_IMAGE /*1*/:
                openTestActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 82) {
            this.MENU_CLICK_COUNT++;
            if (this.MENU_CLICK_COUNT > 5) {
                openTestActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openTestActivity() {
        startActivity(new Intent(this, TestActivity.class));
    }
}
