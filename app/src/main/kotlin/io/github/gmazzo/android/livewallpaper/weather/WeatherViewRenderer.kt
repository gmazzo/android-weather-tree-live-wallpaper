package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLU
import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Named
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_BACK
import javax.microedition.khronos.opengles.GL10.GL_BLEND
import javax.microedition.khronos.opengles.GL10.GL_COLOR_MATERIAL
import javax.microedition.khronos.opengles.GL10.GL_FASTEST
import javax.microedition.khronos.opengles.GL10.GL_GEQUAL
import javax.microedition.khronos.opengles.GL10.GL_LEQUAL
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT
import javax.microedition.khronos.opengles.GL10.GL_PROJECTION
import javax.microedition.khronos.opengles.GL10.GL_SMOOTH
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE0
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV_MODE
import javax.microedition.khronos.opengles.GL10.GL_VERTEX_ARRAY
import javax.microedition.khronos.opengles.GL11

internal class WeatherViewRenderer @AssistedInject constructor(
    private val openGLFactory: OpenGLComponent.Factory,
    @Assisted private val view: GLSurfaceView,
    @Named("homeOffset") private val homeOffset: MutableStateFlow<Float>,
    private val weatherState: MutableStateFlow<WeatherState>,
) : Renderer {
    private var landscape: Boolean = false
    private var cameraFOV = 65.0f
    private var cameraPos = Vector(0.0f, 0.0f, 0.0f)
    private var currentScene: Scene? = null
    private val cameraSpeed: Float = 1.0f
    var demoMode = false
    private var screenHeight = 0f
    private var screenRatio = 1.0f
    private var screenWidth = 0f
    private lateinit var glContext: OpenGLComponent
    private var watchWeatherChanges: Job? = null
    private val isPaused get() = watchWeatherChanges == null

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
        watchWeatherChanges = CoroutineScope(glContext.dispatcher).launch {
            weatherState.collectLatest(::onSceneChanged)
        }
    }

    @Synchronized
    private fun onSceneChanged(state: WeatherState) {
        val mode = state.weatherType.scene

        if (currentScene?.mode != mode) {
            currentScene?.unload()
            currentScene = glContext.sceneFactory.create(mode) {
                it.landscape = landscape
                it.load()
            }
        }
        currentScene!!.updateWeather(state.weatherType)

        Log.i(TAG, "Weather changed to $state, isDemoMode=$demoMode")
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glContext = openGLFactory.create(view, gl as GL11, demoMode)
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
        screenRatio = screenWidth / screenHeight
        landscape = screenRatio > 1.0f

        gl.glViewport(0, 0, w, h)
        gl.setRenderDefaults()

        currentScene?.unload()
        currentScene = null
        glContext.models.close()
        glContext.textures.close()

        watchWeatherChanges()
    }

    private fun GL10.setRenderDefaults() {
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST)
        glShadeModel(GL_SMOOTH)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)
        glAlphaFunc(GL_GEQUAL, 0.02f)
        glDepthMask(false)
        glDepthFunc(GL_LEQUAL)
        glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE)
        glEnableClientState(GL_VERTEX_ARRAY)
        glEnableClientState(GL_TEXTURE_COORD_ARRAY)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
        glCullFace(GL_BACK)
        glActiveTexture(GL_TEXTURE0)
        glEnable(GL_COLOR_MATERIAL)
    }

    @Synchronized
    override fun onDrawFrame(gl: GL10) {
        val scene = currentScene ?: return

        if (!isPaused) {
            glContext.time.update()
            glContext.timeOfDay.update()

            updateCameraPosition()
            gl.updateProjection()

            scene.draw()
        }
    }

    private fun GL10.updateProjection() {
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        GLU.gluPerspective(this, cameraFOV, screenRatio, 1.0f, 400.0f)
        GLU.gluLookAt(this,
            cameraPos.x, cameraPos.y, cameraPos.z,
            cameraPos.x, 400.0f, cameraPos.z,
            0.0f, 0.0f, 1.0f,
        )
    }

    fun updateOffset(offset: Float) {
        homeOffset.value = offset
    }

    private fun updateCameraPosition() {
        val rate = (3.5f * glContext.time.deltaSeconds) * cameraSpeed
        val diff = (Vector(28 * homeOffset.value - 14, 0f, 0f) - cameraPos) * rate

        cameraPos += diff
        cameraFOV = if (landscape) 45f else 70f
    }

    @AssistedFactory
    interface Factory {
        fun create(view: GLSurfaceView): WeatherViewRenderer
    }

    companion object {
        private const val TAG = "IsolatedRenderer"
    }
}
