package io.github.gmazzo.android.livewallpaper.weather

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.SurfaceHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@SuppressLint("ViewConstructor") // we'll never inflate this view
class WeatherView @AssistedInject internal constructor(
    @Assisted context: Context,
    @Assisted tag: String,
    @Assisted demoMode: Boolean,
    rendererFactory: WeatherRenderer.Factory
) : GLSurfaceView(context) {

    private val renderer = rendererFactory.create(this, tag, demoMode)

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

    @AssistedFactory
    interface Factory {
        fun create(context: Context, tag: String, demoMode: Boolean): WeatherView
    }

}
