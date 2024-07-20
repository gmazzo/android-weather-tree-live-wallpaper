package io.github.gmazzo.android.livewallpaper.weather;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.List;

import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode;

public class GLWallpaperService extends WallpaperService {

    public class GLEngine extends Engine {
        private static final String TAG = "GLEngine";
        protected RenderSurfaceView renderSurfaceView;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            this.renderSurfaceView = new RenderSurfaceView(GLWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                this.renderSurfaceView.isDemoMode =  BuildConfig.DEMO_MODE || isPreview();
                this.renderSurfaceView.onResume();
                return;
            }
            this.renderSurfaceView.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            this.renderSurfaceView.onDestroy();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.renderSurfaceView.surfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            this.renderSurfaceView.setServiceSurfaceHolder(holder);
            this.renderSurfaceView.surfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.renderSurfaceView.surfaceDestroyed(holder);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (renderSurfaceView.isDemoMode && event.getAction() == MotionEvent.ACTION_DOWN) {
                SceneMode current = SettingsUtils.getWeatherConditions(GLWallpaperService.this).getScene();
                List<SceneMode> scenes = SceneMode.getEntries();
                SceneMode next = scenes.get((scenes.indexOf(current) + 1) % scenes.size());
                WeatherType nextWeather = WeatherType.SUNNY_DAY;
                for (WeatherType weather : WeatherType.getEntries()) {
                    if (weather.getScene() == next) {
                        nextWeather = weather;
                        break;
                    }
                }
                SettingsUtils.setWeatherConditions(GLWallpaperService.this, nextWeather);
                this.renderSurfaceView.changeScene(nextWeather);
            }
            super.onTouchEvent(event);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            this.renderSurfaceView.scrollOffset(isPreview() ? 0.5f : xOffset);
        }

        protected void setEGLContextClientVersion(int version) {
            this.renderSurfaceView.setEGLContextClientVersion(version);
        }
    }

    public Engine onCreateEngine() {
        return new GLEngine();
    }
}
