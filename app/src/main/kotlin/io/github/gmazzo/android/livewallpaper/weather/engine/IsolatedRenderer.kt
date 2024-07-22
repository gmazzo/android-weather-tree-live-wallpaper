package io.github.gmazzo.android.livewallpaper.weather.engine

import android.content.Context
import android.content.SharedPreferences
import android.opengl.GLU
import android.util.Log
import android.widget.Toast
import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneClear
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneCloudy
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneFog
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneRain
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneSnow
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneStorm
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import io.github.gmazzo.android.livewallpaper.weather.weatherState
import java.util.Calendar
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class IsolatedRenderer(ctx: Context) {
    var IS_LANDSCAPE: Boolean = false
    private val _tod = TimeOfDay()
    private var calendarInstance: Calendar?
    private val cameraDir = Vector()
    private var cameraFOV = 65.0f
    private val cameraPos: Vector
    var context: Context
        private set
    private var currentScene: Scene? = null
    private val desiredCameraPos: Vector
    private val globalTime: GlobalTime
    var isPaused: Boolean
    private var lastCalendarUpdate: Float
    private var lastPositionUpdate: Float
    private var gl: GL11? = null
    var pref_cameraSpeed: Float = 1.0f
    var isDemoMode: Boolean = false
    var prefs: SharedPreferences? = null
    private var screenHeight = 0f
    private var screenRatio = 1.0f
    private var screenWidth = 0f

    init {
        homeOffsetPercentage = 0.5f
        this.isPaused = false
        this.calendarInstance = null
        this.lastCalendarUpdate = 10.0f
        this.lastPositionUpdate = 300.0f
        this.globalTime = GlobalTime()
        this.cameraPos = Vector(0.0f, 0.0f, 0.0f)
        this.desiredCameraPos = Vector(0.0f, 0.0f, 0.0f)
        this.context = ctx
    }

    @Synchronized
    fun onPause() {
        this.isPaused = true
    }

    @Synchronized
    fun onResume() {
        this.lastCalendarUpdate = 10.0f
        this.lastPositionUpdate = 300.0f
        this.isPaused = false
    }

    @Synchronized
    fun onSceneChanged(weather: WeatherType) {
        if (weather.scene != currentSceneId) {
            currentScene!!.unload(this.gl)
            when (weather.scene) {
                SceneMode.CLEAR -> {
                    this.currentScene = SceneClear(this.context, this.gl)
                    currentSceneId = SceneMode.CLEAR
                }

                SceneMode.CLOUDY -> {
                    this.currentScene = SceneCloudy(this.context, this.gl)
                    currentSceneId = SceneMode.CLOUDY
                }

                SceneMode.STORM -> {
                    this.currentScene = SceneStorm(this.context, this.gl)
                    currentSceneId = SceneMode.STORM
                }

                SceneMode.SNOW -> {
                    this.currentScene = SceneSnow(this.context, this.gl)
                    currentSceneId = SceneMode.SNOW
                }

                SceneMode.FOG -> {
                    this.currentScene = SceneFog(this.context, this.gl)
                    currentSceneId = SceneMode.FOG
                }

                SceneMode.RAIN -> {
                    this.currentScene = SceneRain(this.context, this.gl)
                    currentSceneId = SceneMode.RAIN
                }
            }
            currentScene!!.load(this.gl)
        }
        currentScene!!.setScreenMode(this.IS_LANDSCAPE)
        currentScene!!.updateWeather(weather)

        if (BuildConfig.DEBUG) {
            Toast.makeText(context, weather.scene.name, Toast.LENGTH_SHORT).show()
        }
        Log.i(TAG, "Weather changed to " + weather.name + ", isDemoMode=" + isDemoMode)
    }

    fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        gl.glViewport(0, 0, w, h)
        this.screenWidth = w.toFloat()
        this.screenHeight = h.toFloat()
        this.screenRatio = this.screenWidth / this.screenHeight
        this.IS_LANDSCAPE = this.screenRatio > 1.0f
        setRenderDefaults(gl)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        if (gl !== this.gl) {
            this.gl = gl as GL11
            if (this.currentScene == null) {
                if (Scene.Companion.sTextures == null) {
                    Scene.Companion.sTextures = Textures(context.resources, gl)
                }
                if (Scene.Companion.sModels == null) {
                    Scene.Companion.sModels = Models(context.resources, gl)
                }

                currentSceneId = SceneMode.CLEAR
                currentScene = SceneClear(context, gl)
                context = context
            } else {
                currentScene!!.unload(gl)
                currentScene!!.precacheAssets(gl)
            }
        }
        currentScene!!.setScreenMode(this.IS_LANDSCAPE)
        currentScene!!.load(gl)
    }

    fun onSurfaceCreated(gl10: GL10?, eglconfig: EGLConfig?) {
    }

    fun setRenderDefaults(gl: GL10) {
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_BLEND)
        gl.glAlphaFunc(GL10.GL_GEQUAL, 0.02f)
        gl.glDepthMask(false)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glCullFace(GL10.GL_BACK)
        gl.glActiveTexture(GL10.GL_TEXTURE0)
        gl.glEnable(GL10.GL_COLOR_MATERIAL)
        gl.glMatrixMode(GL10.GL_TEXTURE)
        gl.glPopMatrix()
        gl.glPopMatrix()
        gl.glLoadIdentity()
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glPopMatrix()
        gl.glPopMatrix()
        gl.glLoadIdentity()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
        gl.glPopMatrix()
        gl.glLoadIdentity()
    }

    @Synchronized
    fun drawFrame(gl: GL10) {
        if (!this.isPaused) {
            globalTime.updateTime()
            updateCalendar(globalTime.sTimeDelta)
            updateCameraPosition(gl, globalTime.sTimeDelta)
            gl.glClear(GL10.GL_DEPTH_BUFFER_BIT)
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            if (this.IS_LANDSCAPE) {
                GLU.gluPerspective(gl, this.cameraFOV, this.screenRatio, 1.0f, 400.0f)
            } else {
                GLU.gluPerspective(gl, this.cameraFOV, this.screenRatio, 1.0f, 400.0f)
            }
            GLU.gluLookAt(
                gl,
                cameraPos.x,
                cameraPos.y,
                cameraPos.z,
                cameraPos.x, 400.0f,
                cameraPos.z, 0.0f, 0.0f, 1.0f
            )
            currentScene!!.draw(gl, this.globalTime)
        }
    }

    fun setTouchPos(x: Float, y: Float) {
        val vPos = Vector()
        Utility.adjustScreenPosForDepth(
            vPos,
            this.cameraFOV,
            this.screenWidth,
            this.screenHeight,
            x,
            y,
            GlobalRand.floatRange(35.0f, 68.0f) - cameraPos.y
        )
        vPos.x = vPos.x + cameraPos.x
    }

    fun updateOffset(offset: Float) {
        homeOffsetPercentage = offset
    }

    private fun updateCalendar(timeDelta: Float) {
        this.lastCalendarUpdate += timeDelta
        if (this.lastCalendarUpdate >= 10.0f || this.calendarInstance == null) {
            this.calendarInstance = Calendar.getInstance()
            this.lastCalendarUpdate = 0.0f
        }
        if (this.lastPositionUpdate >= 300.0f) {
            val state = context.weatherState

            _tod.calculateTimeTable(
                state.latitude.takeUnless(Float::isNaN) ?: 0f,
                state.longitude.takeUnless(Float::isNaN) ?: 0f
            )
            this.lastPositionUpdate = 0.0f
        }
        calculateTimeOfDay(timeDelta)
    }

    private fun calculateTimeOfDay(timeDelta: Float) {
        var minutes =
            (calendarInstance!![Calendar.HOUR_OF_DAY] * 60) + calendarInstance!![Calendar.MINUTE]
        if (this.isDemoMode) {
            minutes = ((globalTime.msTimeCurrent / 10) % 1440).toInt()
        }
        _tod.update(minutes, true)
        currentScene!!.updateTimeOfDay(this._tod)
    }

    private fun updateCameraPosition(gl: GL10, timeDelta: Float) {
        desiredCameraPos.set((28.0f * homeOffsetPercentage) - CAMERA_X_RANGE, 0.0f, 0.0f)
        val rate = (3.5f * timeDelta) * this.pref_cameraSpeed
        val dx = (desiredCameraPos.x - cameraPos.x) * rate
        val dy = (desiredCameraPos.y - cameraPos.y) * rate
        val dz = (desiredCameraPos.z - cameraPos.z) * rate
        cameraPos.x = cameraPos.x + dx
        cameraPos.y = cameraPos.y + dy
        cameraPos.z = cameraPos.z + dz
        cameraDir.x = cameraPos.x - cameraPos.x
        cameraDir.y = 100.0f - cameraPos.y
        if (this.IS_LANDSCAPE) {
            this.cameraFOV = 45.0f
        } else {
            this.cameraFOV = 70.0f
        }
        horizontalFOV = this.cameraFOV * this.screenRatio
    }

    companion object {
        const val BACKGROUND_DISTANCE: Float = 300.0f
        const val CALENDAR_UPDATE_INTERVAL: Float = 10.0f
        const val CAMERA_X_POSITION: Float = 0.0f
        const val CAMERA_X_RANGE: Float = 14.0f
        const val CAMERA_Y_POSITION: Float = 0.0f
        const val CAMERA_Z_POSITION: Float = 0.0f
        const val CAMERA_Z_RANGE: Float = 10.0f
        const val POSITION_UPDATE_INTERVAL: Float = 300.0f
        private const val TAG = "IsolatedRenderer"
        var currentSceneId: SceneMode? = null
        var homeOffsetPercentage: Float = 0.5f
        var horizontalFOV: Float = 45.0f
    }
}
