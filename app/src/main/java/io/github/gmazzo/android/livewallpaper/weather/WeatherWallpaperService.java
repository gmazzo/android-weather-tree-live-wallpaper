package io.github.gmazzo.android.livewallpaper.weather;

import android.util.Log;
import android.view.SurfaceHolder;

import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherCondition.WeatherResult;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager.WeatherStateReceiver;

public class WeatherWallpaperService extends GLWallpaperService {
    WeatherInfoManager mWeatherInfo;

    public class WeatherWallpaperEngine extends GLEngine implements WeatherStateReceiver {
        private static final String TAG = "WeatherWallpaperEngine";

        public WeatherWallpaperEngine() {
            super();
        }

        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                WeatherWallpaperService.this.mWeatherInfo = WeatherInfoManager.getWeatherInfo(WeatherWallpaperService.this, this);
                WeatherWallpaperService.this.mWeatherInfo.update(1000);
            } else if (WeatherWallpaperService.this.mWeatherInfo != null) {
                WeatherWallpaperService.this.mWeatherInfo.onStop();
            }
            super.onVisibilityChanged(visible);
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        public void onDestroy() {
            super.onDestroy();
        }

        public synchronized void updateWeatherState() {
            Log.i("HM", "updateWeatherState");
            try {
                WeatherResult wr = WeatherWallpaperService.this.mWeatherInfo.getWeather();
                if (wr != null) {
                    boolean weaTypeFrmWebChged = -1 != wr.type.intValue();
                    int weaTypeFrmWeb = wr.type.intValue();
                    if (weaTypeFrmWebChged) {
                        Log.i("HM", "onWeatherStateChanged updated weather type == " + wr.type);
                        Log.i("HM", "onWeatherStateChanged updated weather city == " + wr.city);
                        Log.i("HM", "onWeatherStateChanged updated weather current temp == " + wr.tempCurrent);
                        Log.i("HM", "onWeatherStateChanged updated weather weather == " + wr.conditionTxt);
                    }
                    this.renderSurfaceView.updateWeatherType(weaTypeFrmWeb);
                }
            } catch (NullPointerException e) {
                Log.w("HM", "NullPointerException: " + e);
            }
        }
    }

    public Engine onCreateEngine() {
        return new WeatherWallpaperEngine();
    }
}
