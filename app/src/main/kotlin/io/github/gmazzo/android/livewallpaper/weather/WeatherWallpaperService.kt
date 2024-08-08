package io.github.gmazzo.android.livewallpaper.weather

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeatherWallpaperService : WallpaperService() {

    @Inject
    internal lateinit var weatherViewFactory: WeatherView.Factory

    override fun onCreateEngine() = GLEngine()

    open inner class GLEngine : Engine() {

        private val surfaceView =
            weatherViewFactory.create(
                context = this@WeatherWallpaperService,
                tag = "WeatherService",
                demoMode = BuildConfig.DEMO_MODE || isPreview
            )

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
            surfaceView.scrollOffset(if (isPreview) .5f else xOffset)
        }

    }

}
