package io.github.gmazzo.android.livewallpaper.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.view.PixelCopy
import android.view.SurfaceHolder
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@SuppressLint("ViewConstructor") // we'll never inflate this view
class WeatherView @AssistedInject internal constructor(
    @Assisted context: Context,
    @Assisted logTag: String,
    @Assisted demoMode: Boolean,
    rendererFactory: WeatherRenderer.Factory
) : GLSurfaceView(context) {

    @VisibleForTesting
    internal val renderer = rendererFactory.create(this, logTag, demoMode)

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

    fun takeSnapshot(@MainThread onSnapshot: (Bitmap?) -> Unit): Unit = renderer.postRender {
        val bitmap = Bitmap.createBitmap(
            renderer.screenWidth.toInt(),
            renderer.screenHeight.toInt(),
            Bitmap.Config.ARGB_8888
        )

        PixelCopy.request(this, bitmap, { result ->
            onSnapshot(bitmap.takeIf { result == PixelCopy.SUCCESS })
        }, handler)
    }

    @AssistedFactory
    fun interface Factory {
        fun create(context: Context, logTag: String, demoMode: Boolean): WeatherView
    }

}
