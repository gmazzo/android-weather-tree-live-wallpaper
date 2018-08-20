package com.hm.weather.sky_manager;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

import com.hm.weather.R;
import com.hm.weather.sky_manager.WeatherCondition.WeatherResult;

import java.util.HashMap;

public class WeatherSettingsUtil implements OnCancelListener {
    private static final boolean DEBUG = false;
    public static final float INVALID_COORD = 360.0f;
    public static final String INVALID_STR = "--";
    public static final String KEY_CITY_NAME = "city_name";
    public static final String KEY_DISPLAY_WEATHER = "display_weather";
    public static final String KEY_GEO_CITY_NAME = "geo_city_name";
    public static final String KEY_GEO_LATITUDE = "geo_latitude";
    public static final String KEY_GEO_LONGITUDE = "geo_longitude";
    public static final String KEY_GEO_STATE_NAME = "geo_state_name";
    public static final String KEY_LOCATION_SRC = "location_src";
    public static final String KEY_STATE_NAME = "state_name";
    public static final String KEY_TEMP_UNITS = "temp_units";
    public static final String KEY_USE_GPS = "use_gps";
    public static final String KEY_WEATHER_CITY = "weather_city";
    private static final int LOC_GEO_FEEDBACK = 0;
    private static final int LOC_NAME_FEEDBACK = 1;
    private static final String TAG = "WeatherSettingsUtil";
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    private LocationListener mGpsListener;
    private OnSettingChangeListener mListener = null;
    private final Handler mLocHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WeatherSettingsUtil.LOC_GEO_FEEDBACK /*0*/:
                    WeatherSettingsUtil.this.stopListenLoc();
                    Location receivedLocation = null;
                    if (msg.arg1 != 0 && msg.arg1 == 1) {
                        receivedLocation = WeatherSettingsUtil.this.mNetworkListener.current();
                    }
                    if (receivedLocation != null) {
                        if (WeatherSettingsUtil.this.mNeedProgressDlg) {
                            if (WeatherSettingsUtil.this.mProgressDialog == null) {
                                WeatherSettingsUtil.this.mProgressDialog = ProgressDialog.show(WeatherSettingsUtil.this.mContext, WeatherSettingsUtil.this.mContext.getText(R.string.search_location_title), WeatherSettingsUtil.this.mContext.getText(R.string.search_geo_location_msg), true, WeatherSettingsUtil.DEBUG);
                            } else {
                                WeatherSettingsUtil.this.mProgressDialog.setTitle(WeatherSettingsUtil.this.mContext.getText(R.string.search_location_title));
                                WeatherSettingsUtil.this.mProgressDialog.setMessage(WeatherSettingsUtil.this.mContext.getText(R.string.search_location_msg));
                                WeatherSettingsUtil.this.mProgressDialog.show();
                            }
                        }
                        final double lati = receivedLocation.getLatitude();
                        final double longi = receivedLocation.getLongitude();
                        WeatherSettingsUtil.this.setLatiAndLongitude(lati, longi);
                        new Thread() {
                            public void run() {
                                StringBuffer cityBuffer = new StringBuffer();
                                StringBuffer stateBuffer = new StringBuffer();
                                WeatherSettingsUtil.this.getLocNameFromGeo(lati, longi, cityBuffer, stateBuffer);
                                if (cityBuffer.length() <= 0 || stateBuffer.length() <= 0) {
                                    WeatherSettingsUtil.this.mLocHandler.sendMessage(WeatherSettingsUtil.this.mLocHandler.obtainMessage(1, -1, WeatherSettingsUtil.LOC_GEO_FEEDBACK));
                                    return;
                                }
                                HashMap<String, Object> geoName = new HashMap();
                                geoName.put(WeatherSettingsUtil.KEY_GEO_CITY_NAME, cityBuffer.toString());
                                geoName.put(WeatherSettingsUtil.KEY_GEO_STATE_NAME, stateBuffer.toString());
                                WeatherSettingsUtil.this.mLocHandler.sendMessage(WeatherSettingsUtil.this.mLocHandler.obtainMessage(1, WeatherSettingsUtil.LOC_GEO_FEEDBACK, WeatherSettingsUtil.LOC_GEO_FEEDBACK, geoName));
                            }
                        }.start();
                        return;
                    }
                    return;
                case 1:
                    if (WeatherSettingsUtil.this.mProgressDialog != null && WeatherSettingsUtil.this.mProgressDialog.isShowing() && WeatherSettingsUtil.this.mNeedProgressDlg) {
                        WeatherSettingsUtil.this.mProgressDialog.dismiss();
                    }
                    switch (msg.arg1) {
                        case WeatherSettingsUtil.LOC_GEO_FEEDBACK /*0*/:
                            HashMap geoName = (HashMap) msg.obj;
                            WeatherSettingsUtil.this.setGeoName((String) geoName.get(WeatherSettingsUtil.KEY_GEO_CITY_NAME), (String) geoName.get(WeatherSettingsUtil.KEY_GEO_STATE_NAME));
                            return;
                        default:
                            return;
                    }
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    };
    private LocationManager mLocationManager;
    private boolean mNeedProgressDlg = true;
    private LocationListener mNetworkListener;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mSharedPreferences;

    public interface OnSettingChangeListener {
        void onGeoPositionChange(double d, double d2);

        void onLocNameChange(String str, String str2);
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        String mProvider;
        boolean mValid = WeatherSettingsUtil.DEBUG;

        public LocationListener(String provider) {
            this.mProvider = provider;
            this.mLastLocation = new Location(this.mProvider);
        }

        public void onLocationChanged(Location newLocation) {
            int i = 1;
            if (newLocation.getLatitude() >= 1.0E-8d || newLocation.getLongitude() >= 1.0E-8d) {
                this.mLastLocation.set(newLocation);
                this.mValid = true;
                Handler access$400 = WeatherSettingsUtil.this.mLocHandler;
                if (this.mProvider.compareTo("gps") == 0) {
                    i = WeatherSettingsUtil.LOC_GEO_FEEDBACK;
                }
                WeatherSettingsUtil.this.mLocHandler.sendMessage(access$400.obtainMessage(WeatherSettingsUtil.LOC_GEO_FEEDBACK, i, WeatherSettingsUtil.LOC_GEO_FEEDBACK));
            }
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
            this.mValid = WeatherSettingsUtil.DEBUG;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == 0) {
                this.mValid = WeatherSettingsUtil.DEBUG;
            }
        }

        public Location current() {
            return this.mValid ? this.mLastLocation : null;
        }
    }

    public WeatherSettingsUtil(Context context) {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        this.mLocationManager = (LocationManager) context.getSystemService("location");
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mProgressDialog = new ProgressDialog(context);
        this.mProgressDialog.setOnCancelListener(this);
        this.mProgressDialog.setCancelable(true);
        this.mProgressDialog.setButton(-2, this.mContext.getText(17039360), new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                WeatherSettingsUtil.this.stopListenLoc();
            }
        });
    }

    public boolean isWeatherEnabled() {
        return this.mSharedPreferences.getBoolean(KEY_DISPLAY_WEATHER, true);
    }

    public Integer getTempUnit() {
        return Integer.valueOf(Integer.parseInt(this.mSharedPreferences.getString(KEY_TEMP_UNITS, this.mContext.getString(R.string.default_temp_unit_value))));
    }

    public boolean useCurGeoLoc() {
        return this.mSharedPreferences.getBoolean(KEY_LOCATION_SRC, true);
    }

    public String getCityName() {
        if (useCurGeoLoc()) {
            return getGeoCityName();
        }
        return this.mSharedPreferences.getString(KEY_CITY_NAME, "Chicago");
    }

    public String getDisplayCityName() {
        if (!useCurGeoLoc()) {
            return this.mSharedPreferences.getString(KEY_CITY_NAME, "Chicago");
        }
        String cityName = getGeoCityName();
        if (cityName != null || getLatitude() == 360.0f || getLongitude() == 360.0f) {
            return cityName;
        }
        return this.mContext.getString(R.string.empty_replacer);
    }

    public String getStateName() {
        return this.mSharedPreferences.getString(KEY_STATE_NAME, "IL(Chicago)");
    }

    public String getCityCode() {
        return this.mSharedPreferences.getString(KEY_WEATHER_CITY, "60290");
    }

    public boolean isLocationServOn(Context context) {
        boolean enabled = this.mLocationManager.isProviderEnabled("network");
        if (!enabled) {
            enabled = this.mLocationManager.isProviderEnabled("gps");
            if (enabled) {
            }
        }
        return enabled;
    }

    public Boolean isLocServOnBySetting() {
        boolean z = (isGpsLocServOnBySetting().booleanValue() || isNetworkLocServOnBySetting().booleanValue()) ? true : DEBUG;
        return Boolean.valueOf(z);
    }

    public Boolean isGpsLocServOnBySetting() {
        boolean z = DEBUG;
        String allowedProviders = Secure.getString(this.mContext.getContentResolver(), "location_providers_allowed");
        if (allowedProviders == null) {
            return Boolean.valueOf(DEBUG);
        }
        if (allowedProviders.equals("gps") || allowedProviders.contains(",gps,") || allowedProviders.startsWith("gps,") || allowedProviders.endsWith(",gps")) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean isNetworkLocServOnBySetting() {
        boolean z = DEBUG;
        String allowedProviders = Secure.getString(this.mContext.getContentResolver(), "location_providers_allowed");
        if (allowedProviders == null) {
            return Boolean.valueOf(DEBUG);
        }
        if (allowedProviders.equals("network") || allowedProviders.contains(",network,") || allowedProviders.startsWith("network,") || allowedProviders.endsWith(",network")) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public void handleLocServOff() {
        Builder b = new Builder(this.mContext);
        b.setTitle(R.string.to_enable_network_title);
        b.setMessage(R.string.to_enable_network_msg);
        b.setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface v, int x) {
                Intent goLocationIntent = new Intent();
                goLocationIntent.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
                WeatherSettingsUtil.this.mContext.startActivity(goLocationIntent);
            }
        });
        b.setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface v, int x) {
            }
        });
        b.create().show();
    }

    public boolean isWifiConnected() {
        NetworkInfo netInfo = this.mConnectivityManager.getNetworkInfo(1);
        if (netInfo != null) {
            return netInfo.isConnected();
        }
        return DEBUG;
    }

    public void handleWifiOff() {
        Builder b = new Builder(this.mContext);
        b.setTitle(R.string.to_enable_wifi_title);
        b.setMessage(R.string.to_enable_wifi_msg);
        b.setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface v, int x) {
                Intent goWifiIntent = new Intent();
                goWifiIntent.setAction("android.settings.WIFI_SETTINGS");
                WeatherSettingsUtil.this.mContext.startActivity(goWifiIntent);
            }
        });
        b.setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface v, int x) {
            }
        });
        b.create().show();
    }

    public void setOnSettingChangeListener(OnSettingChangeListener listener) {
        this.mListener = listener;
    }

    public float getLatitude() {
        return this.mSharedPreferences.getFloat(KEY_GEO_LATITUDE, 360.0f);
    }

    public float getLongitude() {
        return this.mSharedPreferences.getFloat(KEY_GEO_LONGITUDE, 360.0f);
    }

    public static float getLatitude(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getFloat(KEY_GEO_LATITUDE, 360.0f);
    }

    public static float getLongitude(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getFloat(KEY_GEO_LONGITUDE, 360.0f);
    }

    public String getGeoCityName() {
        String name = this.mSharedPreferences.getString(KEY_GEO_CITY_NAME, INVALID_STR);
        if (name.compareTo(INVALID_STR) == 0) {
            return null;
        }
        return name;
    }

    public String getGeoStateName() {
        String name = this.mSharedPreferences.getString(KEY_GEO_STATE_NAME, INVALID_STR);
        if (name.compareTo(INVALID_STR) == 0) {
            return null;
        }
        return name;
    }

    public void invalidLatiAndLongitude() {
        setLatiAndLongitude(360.0d, 360.0d);
    }

    public void setLatiAndLongitude(double lati, double longi) {
        if (this.mSharedPreferences != null) {
            Editor editor = this.mSharedPreferences.edit();
            editor.putFloat(KEY_GEO_LATITUDE, (float) lati);
            editor.putFloat(KEY_GEO_LONGITUDE, (float) longi);
            editor.commit();
            if (this.mListener != null) {
                this.mListener.onGeoPositionChange(longi, lati);
            }
        }
    }

    public void invalidGeoName() {
        setGeoName(INVALID_STR, INVALID_STR);
    }

    public void setGeoName(String cityName, String stateName) {
        if (this.mSharedPreferences != null) {
            Editor editor = this.mSharedPreferences.edit();
            editor.putString(KEY_GEO_CITY_NAME, cityName);
            editor.putString(KEY_GEO_STATE_NAME, stateName);
            editor.commit();
            if (this.mListener != null) {
                this.mListener.onLocNameChange(stateName, cityName);
            }
        }
    }

    public Boolean refreshGeoLocation(boolean needProgressDlg) {
        this.mNeedProgressDlg = needProgressDlg;
        invalidLatiAndLongitude();
        invalidGeoName();
        Boolean locServOn = Boolean.valueOf(DEBUG);
        if (isNetworkLocServOnBySetting().booleanValue()) {
            locServOn = Boolean.valueOf(true);
            if (this.mNetworkListener == null) {
                this.mNetworkListener = new LocationListener("network");
            }
            try {
                this.mLocationManager.requestLocationUpdates("network", 1000, 0.0f, this.mNetworkListener);
            } catch (SecurityException e) {
                return Boolean.valueOf(DEBUG);
            } catch (IllegalArgumentException e2) {
                return Boolean.valueOf(DEBUG);
            }
        }
        if (locServOn.booleanValue() && this.mNeedProgressDlg) {
            if (this.mProgressDialog == null) {
                this.mProgressDialog = ProgressDialog.show(this.mContext, this.mContext.getText(R.string.search_location_title), this.mContext.getText(R.string.search_geo_location_msg), true, true);
            } else {
                this.mProgressDialog.setTitle(this.mContext.getText(R.string.search_location_title));
                this.mProgressDialog.setMessage(this.mContext.getText(R.string.search_geo_location_msg));
                this.mProgressDialog.show();
            }
        }
        return locServOn;
    }

    public void stopListenLoc() {
        if (this.mLocationManager != null) {
            try {
                this.mLocationManager.removeUpdates(this.mNetworkListener);
            } catch (Exception e) {
            }
        }
    }

    public void getLocNameFromGeo(double latitude, double longitude, StringBuffer cityName, StringBuffer stateName) {
        WeatherCondition weather_condition = new WeatherCondition();
        weather_condition.getClass();
        WeatherResult weather = new WeatherResult();
        if (weather_condition.getWeather(latitude, longitude, Integer.valueOf(LOC_GEO_FEEDBACK), weather).intValue() == 0) {
            cityName.append(weather.city);
            stateName.append(weather.state);
        }
    }

    public void onCancel(DialogInterface dialog) {
        stopListenLoc();
    }
}
