package io.github.gmazzo.android.livewallpaper.weather

import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class WeatherWallpaperService : WallpaperService() {

    @Inject
    internal lateinit var weatherViewFactory: WeatherView.Factory

    @Inject
    @Named("homeOffset")
    internal lateinit var homeOffset: MutableStateFlow<Float>

    override fun onCreateEngine() = GLEngine()

    open inner class GLEngine : Engine() {

        private val surfaceView by lazy {
            weatherViewFactory.create(
                context = this@WeatherWallpaperService,
                logTag = TAG,
                demoMode = isPreview
            )
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                surfaceView.onResume()

            } else {
                surfaceView.onPause()
            }
        }

        override fun onDestroy() {
            super.onDestroy()

            surfaceView.onDestroy()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)

            surfaceView.surfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            surfaceView.surfaceCreated(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)

            surfaceView.surfaceDestroyed(holder)
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            Log.d(TAG, "onOffsetsChanged: xOffset=$xOffset, yOffset=$yOffset")

            homeOffset.value = xOffset
        }

    }

    companion object {
        private const val TAG = "WallpaperService"
    }

}
