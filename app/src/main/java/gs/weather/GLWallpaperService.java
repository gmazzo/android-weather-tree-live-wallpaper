package gs.weather;

import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GLWallpaperService extends WallpaperService {

    public class GLEngine extends Engine {
        private static final String TAG = "GLEngine";
        protected RenderSurfaceView renderSurfaceView;

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.renderSurfaceView = new RenderSurfaceView(GLWallpaperService.this);
        }

        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                this.renderSurfaceView.onResume();
                if (isPreview()) {
                    this.renderSurfaceView.scrollOffset(0.5f);
                    return;
                }
                return;
            }
            this.renderSurfaceView.onPause();
        }

        public void onDestroy() {
            super.onDestroy();
            this.renderSurfaceView.onDestroy();
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            this.renderSurfaceView.surfaceChanged(holder, format, width, height);
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            this.renderSurfaceView.setServiceSurfaceHolder(holder);
            this.renderSurfaceView.surfaceCreated(holder);
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.renderSurfaceView.surfaceDestroyed(holder);
        }

        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

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
