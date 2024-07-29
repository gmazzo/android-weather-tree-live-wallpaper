package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLU
import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneFactory
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Named
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

internal class WeatherViewRenderer @AssistedInject constructor(
    private val openGLBuilder: OpenGLComponent.Builder,
    @Assisted dispatcher: CoroutineDispatcher,
    @Named("sunPosition") private val sunPosition: MutableStateFlow<Float>,
    private val weatherConditions: MutableStateFlow<WeatherConditions>,
) : Renderer {
    private var landscape: Boolean = false
    private val tod = TimeOfDay()
    private val cameraDir = Vector()
    private var cameraFOV = 65.0f
    private val cameraPos: Vector
    private var currentSceneMode: SceneMode? = null
    private var currentScene: Scene? = null
    private val desiredCameraPos: Vector
    private val globalTime: GlobalTime
    private val cameraSpeed: Float = 1.0f
    var demoMode: Boolean = false
    private var screenHeight = 0f
    private var screenRatio = 1.0f
    private var screenWidth = 0f
    private lateinit var glContext: GLContext
    private val coroutineScope = CoroutineScope(dispatcher)
    private var watchWeatherChanges: Job? = null
    private val isPaused get() = watchWeatherChanges == null

    init {
        homeOffsetPercentage = 0.5f
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
        if (::glContext.isInitialized) {
            watchWeatherChanges()
        }
    }

    private fun watchWeatherChanges() {
        watchWeatherChanges?.cancel()
        watchWeatherChanges = coroutineScope.launch {
            weatherConditions.collectLatest(::onSceneChanged)
        }
    }

    @Synchronized
    private fun onSceneChanged(weather: WeatherConditions) {
        updateTimeOfDayTable(weather)

        if (currentSceneMode != weather.weatherType.scene) {
            currentSceneMode = weather.weatherType.scene
            currentScene?.unload()
            currentScene = glContext.sceneFactory.create(weather.weatherType.scene) {
                it.landscape = landscape
                it.load(weather.weatherType)
            }
        }

        Log.i(TAG, "Weather changed to $weather, isDemoMode=$demoMode")
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glContext = EntryPoints.get(openGLBuilder.openGL(gl as GL11).build(), GLContext::class.java)
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        this.screenWidth = w.toFloat()
        this.screenHeight = h.toFloat()
        this.screenRatio = this.screenWidth / this.screenHeight
        this.landscape = this.screenRatio > 1.0f

        gl.glViewport(0, 0, w, h)
        setRenderDefaults(gl)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        currentScene?.unload()
        currentScene = null
        glContext.models.close()
        glContext.textures.close()

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
    }

    @Synchronized
    override fun onDrawFrame(gl: GL10) {
        val scene = currentScene ?: return

        if (!this.isPaused) {
            globalTime.updateTime()
            calculateTimeOfDay()
            updateCameraPosition(globalTime.sTimeDelta)
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
            scene.draw(globalTime)
        }
    }

    fun setTouchPos(x: Float, y: Float) {
        val vPos = Vector()
        val depth = Random.nextFloat(35.0f, 68.0f) - cameraPos.y
        val ratioX = (cameraFOV * (screenWidth / screenHeight)) * 0.01111111f
        val z =
            (cameraFOV * 0.01111111f) * ((((1.0f - (y / screenHeight)) - 0.5f) * 2.0f) * depth)
        vPos.x = ((((x / screenWidth) - 0.5f) * 2.0f) * depth) * ratioX
        vPos.y = depth
        vPos.z = z
        vPos.x += cameraPos.x
    }

    fun updateOffset(offset: Float) {
        homeOffsetPercentage = offset
    }

    private fun updateTimeOfDayTable(current: WeatherConditions) {
        val (latitude, longitude) = current

        tod.calculateTimeTable(
            latitude.takeUnless(Float::isNaN) ?: 0f,
            longitude.takeUnless(Float::isNaN) ?: 0f
        )
    }

    private fun calculateTimeOfDay() {
        val (latitude, longitude) = weatherConditions.value

        tod.calculateTimeTable(
            latitude.takeUnless(Float::isNaN) ?: 0f,
            longitude.takeUnless(Float::isNaN) ?: 0f
        )

        val cal = Calendar.getInstance()
        var minutes = (cal[Calendar.HOUR_OF_DAY] * 60) + cal[Calendar.MINUTE]
        if (this.demoMode) {
            minutes = ((globalTime.msTimeCurrent / 10) % 1440).toInt()
        }
        tod.update(minutes, true)
        currentScene?.updateTimeOfDay(this.tod)
        sunPosition.value = tod.sunPosition
    }

    private fun updateCameraPosition(timeDelta: Float) {
        desiredCameraPos.set((28.0f * homeOffsetPercentage) - CAMERA_X_RANGE, 0.0f, 0.0f)
        val rate = (3.5f * timeDelta) * this.cameraSpeed
        val dx = (desiredCameraPos.x - cameraPos.x) * rate
        val dy = (desiredCameraPos.y - cameraPos.y) * rate
        val dz = (desiredCameraPos.z - cameraPos.z) * rate
        cameraPos.x += dx
        cameraPos.y += dy
        cameraPos.z += dz
        cameraDir.x = 0f
        cameraDir.y = 100.0f - cameraPos.y
        if (this.landscape) {
            this.cameraFOV = 45.0f
        } else {
            this.cameraFOV = 70.0f
        }
        horizontalFOV = this.cameraFOV * this.screenRatio
    }

    @AssistedFactory
    interface Factory {
        fun create(dispatcher: CoroutineDispatcher): WeatherViewRenderer
    }

    @EntryPoint
    @InstallIn(OpenGLComponent::class)
    interface GLContext {
        val textures: Textures
        val models: Models
        val sceneFactory: SceneFactory
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
