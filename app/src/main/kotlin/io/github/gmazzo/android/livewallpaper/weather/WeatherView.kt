package io.github.gmazzo.android.livewallpaper.weather

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.SurfaceHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

@SuppressLint("ViewConstructor") // we'll never inflate this view
class WeatherView @AssistedInject internal constructor(
    @Assisted context: Context,
    rendererFactory: WeatherViewRenderer.Factory
) : GLSurfaceView(context) {

    private val renderer = rendererFactory.create(context, OpenGLDispatcher())

    var isDemoMode by renderer::demoMode

    private var externalSurfaceHolder: SurfaceHolder? = null

    init {
        setRenderer(renderer)
    }

    override fun getHolder(): SurfaceHolder =
        externalSurfaceHolder ?: super.getHolder()

    override fun surfaceCreated(holder: SurfaceHolder) {
        super.surfaceCreated(holder)

        if (holder !== this.externalSurfaceHolder) {
            this.externalSurfaceHolder = holder
        }
        onResume()
    }

    override fun onResume() {
        super.onResume()
        renderer.onResume()
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onPause() {
        super.onPause()
        renderer.onPause()
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun onDestroy() {
        super.onDetachedFromWindow()
    }

    fun scrollOffset(offset: Float) {
        renderer.updateOffset(offset)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        renderer.setTouchPos(motionEvent.x, motionEvent.y)
        return super.onTouchEvent(motionEvent)
    }

    @AssistedFactory
    interface Factory {
        fun create(context: Context): WeatherView
    }

    private inner class OpenGLDispatcher : CoroutineDispatcher() {

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            queueEvent(block)
        }

    }

}
