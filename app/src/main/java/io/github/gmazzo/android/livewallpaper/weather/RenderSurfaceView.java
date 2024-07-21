package io.github.gmazzo.android.livewallpaper.weather;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.github.gmazzo.android.livewallpaper.weather.engine.IsolatedRenderer;

public class RenderSurfaceView extends GLSurfaceView {
    protected boolean isPaused;
    protected boolean isDemoMode;
    protected BaseRenderer mBaseRenderer;
    protected SurfaceHolder mServiceSurfaceHolder;

    protected class BaseRenderer implements Renderer {
        private final IsolatedRenderer renderer;
        private boolean wasCreated = false;

        public BaseRenderer() {
            this.renderer = new IsolatedRenderer(RenderSurfaceView.this.getContext());
        }

        public void onPause() {
            this.renderer.onPause();
        }

        public void onResume() {
            this.renderer.isDemoMode = isDemoMode;
            this.renderer.onResume();
        }

        public void onDrawFrame(GL10 gl) {
            if (this.wasCreated) {
                this.renderer.drawFrame(gl);
            }
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            this.renderer.onSurfaceChanged(gl, w, h);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig eglconfig) {
            this.renderer.onSurfaceCreated(gl, eglconfig);
            this.wasCreated = true;
        }
    }

    public RenderSurfaceView(Context context) {
        this(context, null);
    }

    public RenderSurfaceView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        this.isPaused = false;
        this.mBaseRenderer = new BaseRenderer();
        setRenderer(this.mBaseRenderer);
    }

    public SurfaceHolder getHolder() {
        if (this.mServiceSurfaceHolder != null) {
            return this.mServiceSurfaceHolder;
        }
        return super.getHolder();
    }

    public void setServiceSurfaceHolder(SurfaceHolder holder) {
        this.mServiceSurfaceHolder = holder;
    }

    public void onPause() {
        this.mBaseRenderer.onPause();
        setRenderMode(0);
    }

    public void onResume() {
        this.mBaseRenderer.onResume();
        setRenderMode(1);
    }

    public void onDestroy() {
        super.onDetachedFromWindow();
    }

    public void changeScene(WeatherType weather) {
        this.mBaseRenderer.renderer.onSceneChanged(weather);
    }

    public void scrollOffset(float offset) {
        this.mBaseRenderer.renderer.updateOffset(offset);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mBaseRenderer.renderer.setTouchPos(motionEvent.getX(), motionEvent.getY());
        return super.onTouchEvent(motionEvent);
    }

    public void updateWeatherType(WeatherType type) {
        changeScene(type);
    }
}
