package com.hm.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.hm.weather.engine.DialogColor;
import com.hm.weather.engine.DialogColor.OnClickListener;
import com.hm.weather.engine.DialogSlider;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Utility.Logger;
import com.hm.weather.engine.Vector4;

public class BaseWallpaperSettings extends PreferenceActivity {
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
    public static final int REQUESTCODE_PREF_IMAGE = 1;
    private static final String TAG = "GL Engine";
    protected Context context;
    private Preference currentPref = null;
    private Uri selectedImageURI;

    public class PrefButtonColorListener implements OnPreferenceClickListener {
        String DEFAULT_COLOR = "1 1 1 1";

        public PrefButtonColorListener(String arg2) {
            this.DEFAULT_COLOR = arg2;
        }

        public boolean onPreferenceClick(Preference pref) {
            String key = pref.getKey();
            SharedPreferences sharedPrefs = pref.getSharedPreferences();
            String colorStr = sharedPrefs.getString(key, this.DEFAULT_COLOR);
            Vector4 v4 = new Vector4();
            v4.set(colorStr, 0.0f, 1.0f);
            new DialogColor(BaseWallpaperSettings.this.context, false, key, v4.getWebColor(), new PrefColorPickerClickListener(sharedPrefs), 0).show();
            return true;
        }
    }

    public class PrefButtonImageCustomListener implements OnPreferenceClickListener {
        private int _requestCode = 1;
        private String _tooltip = null;

        public PrefButtonImageCustomListener(int requestCode, int tooltipId) {
            this._requestCode = requestCode;
            if (tooltipId != 0) {
                this._tooltip = BaseWallpaperSettings.this.getResources().getString(tooltipId);
            }
        }

        public boolean onPreferenceClick(Preference prefs) {
            BaseWallpaperSettings.this.getImageFromBrowser(this._requestCode, prefs);
            if (this._tooltip != null) {
                Toast localToast = Toast.makeText(BaseWallpaperSettings.this.context, this._tooltip, 1);
                localToast.setGravity(17, localToast.getXOffset() / 2, 200);
                localToast.show();
            }
            return true;
        }
    }

    public class PrefButtonSliderListener implements OnPreferenceClickListener {
        String DEFAULT_VALUE = "0";
        String labelLeft;
        String labelRight;
        String labelTitle;
        int sliderMaxValue;
        int sliderMinValue;

        public PrefButtonSliderListener(String title, String value, int min, int max, String left, String right) {
            this.DEFAULT_VALUE = value;
            this.sliderMinValue = min;
            this.sliderMaxValue = max;
            this.labelLeft = left;
            this.labelRight = right;
            this.labelTitle = title;
        }

        public boolean onPreferenceClick(Preference pref) {
            String key = pref.getKey();
            SharedPreferences sharedPrefs = pref.getSharedPreferences();
            new DialogSlider(BaseWallpaperSettings.this.context, key, this.labelTitle, this.labelLeft, this.labelRight, this.sliderMinValue, this.sliderMaxValue, Integer.parseInt(sharedPrefs.getString(key, this.DEFAULT_VALUE)), new PrefSliderClickListener(sharedPrefs)).show();
            return true;
        }
    }

    public class PrefColorPickerClickListener implements OnClickListener {
        SharedPreferences prefs;

        public PrefColorPickerClickListener(SharedPreferences sharedPrefs) {
            this.prefs = sharedPrefs;
        }

        public void onClick(Object tag, int color) {
            Vector4 vector4 = new Vector4();
            vector4.set(color);
            String colorStr = vector4.toString();
            String prefName = (String) tag;
            Logger.v(BaseWallpaperSettings.TAG, "Color Result: " + colorStr);
            Editor editor = this.prefs.edit();
            editor.putString(prefName, colorStr);
            editor.commit();
        }
    }

    public class PrefSliderClickListener implements DialogSlider.OnClickListener {
        SharedPreferences prefs;

        public PrefSliderClickListener(SharedPreferences sharedPrefs) {
            this.prefs = sharedPrefs;
        }

        public void onClick(Object tag, int value) {
            String sValue = Integer.toString(value);
            String prefName = (String) tag;
            Logger.v("SliderResult", sValue);
            Editor localEditor1 = this.prefs.edit();
            localEditor1.putString(prefName, sValue);
            localEditor1.commit();
        }
    }

    private void getImageFromBrowser(int code, Preference preference) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        this.currentPref = preference;
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), code);
    }

    protected SharedPreferences getGlobalPrefs() {
        Logger.v(TAG, "getGlobalPrefs has not been overridden!");
        return null;
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = managedQuery(uri, new String[]{"_data"}, null, null, null);
        try {
            int i = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return cursor.getString(i);
        } catch (Exception e) {
            return "InvalidFile";
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && this.currentPref != null) {
            try {
                this.selectedImageURI = data.getData();
                String path = getRealPathFromURI(this.selectedImageURI);
                SharedPreferences preferences = this.currentPref.getSharedPreferences();
                if (preferences == null) {
                    Logger.v(TAG, "ERROR: Preferences was null!  Did you fail to override getGlobalPrefs?");
                    return;
                }
                String key = this.currentPref.getKey();
                String value = preferences.getString(key, "");
                this.context.deleteFile(key);
                this.context.deleteFile(value);
                if (!value.contains("_cstalt")) {
                    value = key + "_cstalt";
                }
                TextureManager.copyImageToCache(this.context, path, value);
                Editor editor = preferences.edit();
                editor.putString(key, value);
                editor.commit();
            } catch (Exception localException) {
                Logger.v(TAG, "ERROR: Problem getting image result!");
                localException.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.context = this;
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
    }
}
