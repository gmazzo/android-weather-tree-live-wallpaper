package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLU
import android.util.Log
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Utility
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class WeatherRenderer(
    context: Context,
    private val openGLDispatcher: CoroutineDispatcher,
    private val weatherConditions: MutableStateFlow<WeatherConditions>,
) : Renderer {
    var landscape: Boolean = false
    private val _tod = TimeOfDay()
    private var calendarInstance: Calendar?
    private val cameraDir = Vector()
    private var cameraFOV = 65.0f
    private val cameraPos: Vector
    private var currentScene: Scene? = null
    private val desiredCameraPos: Vector
    private val globalTime: GlobalTime
    private var lastCalendarUpdate: Float
    private var lastPositionUpdate: Float
    private lateinit var gl: GL11
    var pref_cameraSpeed: Float = 1.0f
    var demoMode: Boolean = false
    private var screenHeight = 0f
    private var screenRatio = 1.0f
    private var screenWidth = 0f

    private val textures by lazy { Textures(context.resources, gl!!) }

    private val models by lazy { Models(context.resources, gl!!) }

    private val coroutineScope = CoroutineScope(openGLDispatcher)
    private var watchWeatherChanges: Job? = null

    private val isPaused get() = watchWeatherChanges == null

    init {
        homeOffsetPercentage = 0.5f
        this.calendarInstance = null
        this.lastCalendarUpdate = 10.0f
        this.lastPositionUpdate = 300.0f
        this.globalTime = GlobalTime()
        this.cameraPos = Vector(0.0f, 0.0f, 0.0f)
        this.desiredCameraPos = Vector(0.0f, 0.0f, 0.0f)
    }

    @Synchronized
    fun onPause() {
        watchWeatherChanges?.cancel()
        watchWeatherChanges = null
    }

    @Synchronized
    fun onResume() {
        lastCalendarUpdate = 10.0f
        lastPositionUpdate = 300.0f
        watchWeatherChanges()
    }

    private fun watchWeatherChanges() {
        watchWeatherChanges?.cancel()
        watchWeatherChanges = coroutineScope.launch {
            weatherConditions
                .runningFold(null as WeatherConditions? to WeatherConditions()) { (_, prev), it -> prev to it }
                .collect { (previous, current) -> onSceneChanged(previous, current) }
        }
    }

    @Synchronized
    private fun onSceneChanged(previous: WeatherConditions?, current: WeatherConditions) {
        if (!this::gl.isInitialized) return // TODO review this later

        if (currentScene == null || previous?.weatherType?.scene != current.weatherType.scene) {
            currentScene?.unload(gl)
            currentScene = when (current.weatherType.scene) {
                SceneMode.CLEAR -> SceneClear(models, textures)
                SceneMode.CLOUDY -> SceneCloudy(models, textures)
                SceneMode.STORM -> SceneStorm(models, textures)
                SceneMode.SNOW -> SceneSnow(models, textures)
                SceneMode.FOG -> SceneFog(models, textures)
                SceneMode.RAIN -> SceneRain(models, textures)
            }.also { it.load(gl) }
        }
        currentScene?.landscape = landscape
        currentScene?.updateWeather(current.weatherType)

        Log.i(TAG, "Weather changed to $current, isDemoMode=$demoMode")
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        this.gl = gl as GL11
        this.screenWidth = w.toFloat()
        this.screenHeight = h.toFloat()
        this.screenRatio = this.screenWidth / this.screenHeight
        this.landscape = this.screenRatio > 1.0f

        gl.glViewport(0, 0, w, h)
        setRenderDefaults(gl)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        currentScene?.unload(gl)
        currentScene = null
        models.close()
        textures.close()

        watchWeatherChanges()
    }

    private fun setRenderDefaults(gl: GL10) {
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
    override fun onDrawFrame(gl: GL10) {


        val scene = currentScene ?: return

        if (!this.isPaused) {
            globalTime.updateTime()
            updateCalendar(globalTime.sTimeDelta)
            updateCameraPosition(gl, globalTime.sTimeDelta)
            gl.glClear(GL10.GL_DEPTH_BUFFER_BIT)
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            if (this.landscape) {
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
            scene.draw(gl, this.globalTime)
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
        vPos.x += cameraPos.x
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
            val (latitude, longitude) = weatherConditions.value

            _tod.calculateTimeTable(
                latitude.takeUnless(Float::isNaN) ?: 0f,
                longitude.takeUnless(Float::isNaN) ?: 0f
            )
            this.lastPositionUpdate = 0.0f
        }
        calculateTimeOfDay(timeDelta)
    }

    private fun calculateTimeOfDay(timeDelta: Float) {
        var minutes =
            (calendarInstance!![Calendar.HOUR_OF_DAY] * 60) + calendarInstance!![Calendar.MINUTE]
        if (this.demoMode) {
            minutes = ((globalTime.msTimeCurrent / 10) % 1440).toInt()
        }
        _tod.update(minutes, true)
        currentScene?.updateTimeOfDay(this._tod)
    }

    private fun updateCameraPosition(gl: GL10, timeDelta: Float) {
        desiredCameraPos.set((28.0f * homeOffsetPercentage) - CAMERA_X_RANGE, 0.0f, 0.0f)
        val rate = (3.5f * timeDelta) * this.pref_cameraSpeed
        val dx = (desiredCameraPos.x - cameraPos.x) * rate
        val dy = (desiredCameraPos.y - cameraPos.y) * rate
        val dz = (desiredCameraPos.z - cameraPos.z) * rate
        cameraPos.x += dx
        cameraPos.y += dy
        cameraPos.z += dz
        cameraDir.x = cameraPos.x - cameraPos.x // FIXME isn't this 0?
        cameraDir.y = 100.0f - cameraPos.y
        if (this.landscape) {
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
        var homeOffsetPercentage: Float = 0.5f
        var horizontalFOV: Float = 45.0f
    }
}
