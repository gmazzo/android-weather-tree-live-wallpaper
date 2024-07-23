package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.SurfaceHolder
import io.github.gmazzo.android.livewallpaper.weather.engine.IsolatedRenderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderSurfaceView(context: Context) : GLSurfaceView(context) {
    var isDemoMode: Boolean = false
    private val mBaseRenderer = BaseRenderer().also(::setRenderer)
    protected var mServiceSurfaceHolder: SurfaceHolder? = null

    inner class BaseRenderer : Renderer {

        val renderer = IsolatedRenderer(context)

        private var wasCreated = false

        fun onPause() {
            renderer.onPause()
        }

        fun onResume() {
            renderer.onResume()
        }

        override fun onDrawFrame(gl: GL10) {
            if (this.wasCreated) {
                renderer.drawFrame(gl)
            }
        }

        override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
            renderer.isDemoMode = isDemoMode
            renderer.onSurfaceChanged(gl, w, h)
        }

        override fun onSurfaceCreated(gl: GL10, eglconfig: EGLConfig) {
            renderer.onSurfaceCreated(gl, eglconfig)

            renderer.isDemoMode = isDemoMode
            this.wasCreated = true
        }
    }

    override fun getHolder(): SurfaceHolder {
        if (this.mServiceSurfaceHolder != null) {
            return mServiceSurfaceHolder!!
        }
        return super.getHolder()
    }

    fun setServiceSurfaceHolder(holder: SurfaceHolder?) {
        this.mServiceSurfaceHolder = holder
    }

    override fun onPause() {
        mBaseRenderer.onPause()
        renderMode = 0
    }

    override fun onResume() {
        mBaseRenderer.onResume()
        renderMode = 1
    }

    fun onDestroy() {
        super.onDetachedFromWindow()
    }

    fun scrollOffset(offset: Float) {
        mBaseRenderer.renderer.updateOffset(offset)
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        mBaseRenderer.renderer.setTouchPos(motionEvent.x, motionEvent.y)
        return super.onTouchEvent(motionEvent)
    }

    fun updateWeather(weather: WeatherConditions) {
        mBaseRenderer.renderer.onSceneChanged(weather)
    }
}
