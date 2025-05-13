package io.github.gmazzo.android.livewallpaper.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.view.SurfaceHolder
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.createBitmap
import dagger.Lazy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Named

@SuppressLint("ViewConstructor") // we'll never inflate this view
class WeatherView @AssistedInject internal constructor(
    @Assisted context: Context,
    @Assisted logTag: String,
    @Assisted demoMode: Boolean,
    rendererFactory: WeatherRenderer.Factory,
    @Named("takeSnapshot") private val snapshotThread: Lazy<HandlerThread>,
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

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        super.surfaceDestroyed(holder)

        renderer.onSurfaceDestroyed()
    }

    override fun onResume() {
        super.onResume()

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onPause() {
        super.onPause()

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun onDestroy() {
        super.onDetachedFromWindow()
    }

    fun takeSnapshot(onSnapshot: SnapshotCallback) = renderer.postRender {
        val bitmap = createBitmap(renderer.screenWidth.toInt(), renderer.screenHeight.toInt())

        PixelCopy.request(this, bitmap, { result ->
            onSnapshot.onSnapshot(result, bitmap.takeIf { result == PixelCopy.SUCCESS })
        }, Handler(snapshotThread.get().looper))
    }

    @AssistedFactory
    fun interface Factory {
        fun create(context: Context, logTag: String, demoMode: Boolean): WeatherView
    }

    fun interface SnapshotCallback {
        fun onSnapshot(result: Int, bitmap: Bitmap?)
    }

}
