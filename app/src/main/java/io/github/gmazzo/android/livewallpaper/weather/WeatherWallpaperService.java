package io.github.gmazzo.android.livewallpaper.weather;

import android.util.Log;
import android.view.SurfaceHolder;

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
            @WeatherType int weaTypeFrmWeb = WeatherWallpaperService.this.mWeatherInfo.getWeather();

            this.renderSurfaceView.updateWeatherType(weaTypeFrmWeb);
        }
    }

    public Engine onCreateEngine() {
        return new WeatherWallpaperEngine();
    }
}
