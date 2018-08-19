package com.hm.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.hm.weather.sky_manager.WeatherCondition.WeatherResult;
import com.hm.weather.sky_manager.WeatherInfoManager;
import com.hm.weather.sky_manager.WeatherInfoManager.WeatherStateReceiver;

public class TestActivity extends Activity implements WeatherStateReceiver {
    private static final int WEATHER_DATA_INPUT_LIVE_WEATHER = 0;
    static final String[] bgList = new String[]{"bg1", "bg2", "bg3"};
    private boolean at_night = false;
    int bgCurrent = 0;
    ButtonHomeSimListener homeSimListener;
    private int mWeaDataSrc = 0;
    private int mWeaTypeFrmSetting = -1;
    private int mWeaTypeFrmWeb = -1;
    private boolean mWeaTypeFrmWebChged = false;
    private WeatherResult mWeatherData = null;
    private WeatherInfoManager mWeatherInfo = null;
    SharedPreferences prefs;
    RenderSurfaceView renderSurfaceView;
    ButtonScenesListener sceneListener;
    ButtonSettingsListener settingsListener;

    private class ButtonHomeSimListener implements OnClickListener {
        private ButtonHomeSimListener() {
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_1:
                    TestActivity.this.renderSurfaceView.scrollOffset(0.05f);
                    return;
                case R.id.button_2:
                    TestActivity.this.renderSurfaceView.scrollOffset(0.275f);
                    return;
                case R.id.button_3:
                    TestActivity.this.renderSurfaceView.scrollOffset(0.5f);
                    return;
                case R.id.button_4:
                    TestActivity.this.renderSurfaceView.scrollOffset(0.725f);
                    return;
                case R.id.button_5:
                    TestActivity.this.renderSurfaceView.scrollOffset(0.95f);
                    return;
                default:
                    return;
            }
        }
    }

    private class ButtonScenesListener implements OnClickListener {
        private ButtonScenesListener() {
        }

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_clear:
                    TestActivity.this.renderSurfaceView.updateWeatherType(33);
                    return;
                case R.id.button_cloudy:
                    TestActivity.this.renderSurfaceView.updateWeatherType(7);
                    return;
                case R.id.button_rain:
                    TestActivity.this.renderSurfaceView.updateWeatherType(18);
                    return;
                case R.id.button_storm:
                    TestActivity.this.renderSurfaceView.updateWeatherType(15);
                    return;
                case R.id.button_snow:
                    TestActivity.this.renderSurfaceView.updateWeatherType(22);
                    return;
                case R.id.button_fog:
                    TestActivity.this.renderSurfaceView.updateWeatherType(11);
                    return;
                default:
                    return;
            }
        }
    }

    private class ButtonSettingsListener implements OnClickListener {
        private ButtonSettingsListener() {
        }

        public void onClick(View view) {
            TestActivity.this.startActivity(new Intent(TestActivity.this, WallpaperSettings.class));
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.testactivitylayout);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setInitialPrefs();
        this.homeSimListener = new ButtonHomeSimListener();
        this.settingsListener = new ButtonSettingsListener();
        this.sceneListener = new ButtonScenesListener();
        ((Button) findViewById(R.id.button_1)).setOnClickListener(this.homeSimListener);
        ((Button) findViewById(R.id.button_2)).setOnClickListener(this.homeSimListener);
        ((Button) findViewById(R.id.button_3)).setOnClickListener(this.homeSimListener);
        ((Button) findViewById(R.id.button_4)).setOnClickListener(this.homeSimListener);
        ((Button) findViewById(R.id.button_5)).setOnClickListener(this.homeSimListener);
        ((Button) findViewById(R.id.button_clear)).setOnClickListener(this.sceneListener);
        ((Button) findViewById(R.id.button_cloudy)).setOnClickListener(this.sceneListener);
        ((Button) findViewById(R.id.button_rain)).setOnClickListener(this.sceneListener);
        ((Button) findViewById(R.id.button_storm)).setOnClickListener(this.sceneListener);
        ((Button) findViewById(R.id.button_snow)).setOnClickListener(this.sceneListener);
        ((Button) findViewById(R.id.button_fog)).setOnClickListener(this.sceneListener);
    }

    public void onPause() {
        super.onPause();
        this.renderSurfaceView.onPause();
        if (this.mWeatherInfo != null) {
            this.mWeatherInfo.onStop();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.renderSurfaceView == null) {
            LinearLayout mainView = (LinearLayout) findViewById(R.id.gamesurfaceview);
            this.renderSurfaceView = new RenderSurfaceView(this);
            mainView.addView(this.renderSurfaceView);
        }
        this.renderSurfaceView.onResume();
    }

    public void queryWeatherInfo() {
        this.mWeatherInfo = WeatherInfoManager.getWeatherInfo(this, this);
        this.mWeatherInfo.update(1000);
    }

    public synchronized void updateWeatherState() {
        Log.i("HM", "updateWeatherState");
        try {
            WeatherResult w = this.mWeatherInfo.getWeather();
            if (w != null) {
                this.mWeatherData = w;
                this.mWeaTypeFrmWebChged = this.mWeaTypeFrmWeb != this.mWeatherData.type.intValue();
                this.mWeaTypeFrmWeb = this.mWeatherData.type.intValue();
                if (this.mWeaDataSrc == 0 && this.mWeaTypeFrmWebChged) {
                    Log.i("HM", "onWeatherStateChanged updated weather type == " + this.mWeatherData.type);
                    Log.i("HM", "onWeatherStateChanged updated weather city == " + this.mWeatherData.city);
                    Log.i("HM", "onWeatherStateChanged updated weather current temp == " + this.mWeatherData.tempCurrent);
                    Log.i("HM", "onWeatherStateChanged updated weather weather == " + this.mWeatherData.conditionTxt);
                }
                this.renderSurfaceView.updateWeatherType(this.mWeaDataSrc == 0 ? this.mWeaTypeFrmWeb : this.mWeaTypeFrmSetting);
            }
        } catch (NullPointerException e) {
            Log.w("HM", "NullPointerException: " + e);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean ret = super.onTouchEvent(motionEvent);
        try {
            this.renderSurfaceView.onTouchEvent(motionEvent);
            Thread.sleep(33);
            return ret;
        } catch (Exception e) {
            return false;
        }
    }

    public void setInitialPrefs() {
        String background = this.prefs.getString("pref_background", "bg1");
        for (int i = 0; i < bgList.length; i++) {
            if (bgList[i].equals(background)) {
                this.bgCurrent = i;
                return;
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case BaseWallpaperSettings.REQUESTCODE_PREF_IMAGE /*1*/:
                startActivity(new Intent(this, WallpaperSettings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
