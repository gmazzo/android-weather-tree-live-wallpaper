package io.github.gmazzo.android.livewallpaper.weather

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode

open class GLWallpaperService : WallpaperService() {

    override fun onCreateEngine() = GLEngine()

    open inner class GLEngine : Engine() {
        protected var renderSurfaceView: RenderSurfaceView? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            this.renderSurfaceView = RenderSurfaceView(this@GLWallpaperService)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                renderSurfaceView!!.isDemoMode = BuildConfig.DEMO_MODE || isPreview
                renderSurfaceView!!.onResume()
                return
            }
            renderSurfaceView!!.onPause()
        }

        override fun onDestroy() {
            super.onDestroy()
            renderSurfaceView!!.onDestroy()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            renderSurfaceView!!.surfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            renderSurfaceView!!.setServiceSurfaceHolder(holder)
            renderSurfaceView!!.surfaceCreated(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            renderSurfaceView!!.surfaceDestroyed(holder)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (renderSurfaceView!!.isDemoMode && event.action == MotionEvent.ACTION_DOWN) {
                val current = this@GLWallpaperService.weatherConditions.scene
                val scenes: List<SceneMode> = SceneMode.entries
                val next = scenes[(scenes.indexOf(current) + 1) % scenes.size]
                var nextWeather = WeatherType.SUNNY_DAY
                for (weather in WeatherType.entries) {
                    if (weather.scene == next) {
                        nextWeather = weather
                        break
                    }
                }
                this@GLWallpaperService.weatherConditions = nextWeather
                renderSurfaceView!!.changeScene(nextWeather)
            }
            super.onTouchEvent(event)
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            renderSurfaceView!!.scrollOffset(if (isPreview) 0.5f else xOffset)
        }

    }

}
