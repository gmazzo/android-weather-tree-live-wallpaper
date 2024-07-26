package io.github.gmazzo.android.livewallpaper.weather

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.SurfaceHolder
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class WeatherSurfaceView(context: Context) : GLSurfaceView(context) {

    private val deps = EntryPoints.get(context.applicationContext, Dependencies::class.java)

    private val renderer =
        WeatherRenderer(context, OpenGLDispatcher(), deps.sunPosition, deps.weatherConditions)

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

    private inner class OpenGLDispatcher : CoroutineDispatcher() {

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            queueEvent(block)
        }

    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface Dependencies {

        @get:Named("sunPosition")
        val sunPosition: MutableStateFlow<Float>

        val weatherConditions: MutableStateFlow<WeatherConditions>

    }

}
