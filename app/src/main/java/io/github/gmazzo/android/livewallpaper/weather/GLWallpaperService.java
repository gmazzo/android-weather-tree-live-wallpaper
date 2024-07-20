package io.github.gmazzo.android.livewallpaper.weather;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

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
                if (isPreview()) {
                    this.renderSurfaceView.scrollOffset(0.5f);
                    return;
                }
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
                WeatherType current = SettingsUtils.getWeatherConditions(GLWallpaperService.this);
                WeatherType next = WeatherType.getEntries().get(0);
                for (WeatherType weatherType : WeatherType.getEntries()) {
                    if (weatherType.getScene().ordinal() > current.ordinal()) {
                        next = weatherType;
                        break;
                    }
                }

                SettingsUtils.setWeatherConditions(GLWallpaperService.this, next);
                this.renderSurfaceView.changeScene(next);
            }
            super.onTouchEvent(event);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            this.renderSurfaceView.scrollOffset(xOffset);
        }

        protected void setEGLContextClientVersion(int version) {
            this.renderSurfaceView.setEGLContextClientVersion(version);
        }
    }

    public Engine onCreateEngine() {
        return new GLEngine();
    }
}
