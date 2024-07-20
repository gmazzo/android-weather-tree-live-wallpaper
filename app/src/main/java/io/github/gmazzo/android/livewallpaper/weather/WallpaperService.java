package io.github.gmazzo.android.livewallpaper.weather;

import android.util.Log;
import android.view.SurfaceHolder;

import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager.WeatherStateReceiver;

public class WallpaperService extends GLWallpaperService {
    WeatherInfoManager mWeatherInfo;

    public class WeatherWallpaperEngine extends GLEngine implements WeatherStateReceiver {
        private static final String TAG = "WeatherWallpaperEngine";

        public WeatherWallpaperEngine() {
            super();
        }

        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                WallpaperService.this.mWeatherInfo = WeatherInfoManager.getWeatherInfo(WallpaperService.this, this);
                WallpaperService.this.mWeatherInfo.update(1000);
            } else if (WallpaperService.this.mWeatherInfo != null) {
                WallpaperService.this.mWeatherInfo.onStop();
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
            WeatherType weather = SettingsUtils.getWeatherConditions(WallpaperService.this);

            this.renderSurfaceView.updateWeatherType(weather);
        }
    }

    public Engine onCreateEngine() {
        return new WeatherWallpaperEngine();
    }
}
