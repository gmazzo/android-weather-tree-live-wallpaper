package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLU
import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

internal class WeatherRenderer @AssistedInject constructor(
    private val openGLFactory: OpenGLComponent.Factory,
    @Assisted private val view: GLSurfaceView,
    private val weather: MutableStateFlow<WeatherType>,
) : Renderer {
    private var landscape: Boolean = false
    private var cameraFOV = 65f
    private var cameraPos = Vector(0f, 0f, 0f)
    private var currentScene: SceneComponent? = null
    private val cameraSpeed: Float = 1f
    var demoMode = false
    private var screenHeight = 0f
    private var screenRatio = 1f
    private var screenWidth = 0f
    private lateinit var glContext: OpenGLComponent
    private var watchWeatherChanges: Job? = null
    private val homeOffset = MutableStateFlow(.5f)
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
            weather.collectLatest(::onSceneChanged)
        }
    }

    @Synchronized
    private fun onSceneChanged(weather: WeatherType) {
        val mode = weather.scene

        if (currentScene?.mode != mode) {
            currentScene?.scene?.unload()
            currentScene = glContext.sceneFactory.create(mode).also {
                it.scene.landscape = landscape
                it.scene.load()
            }
        }
        currentScene!!.scene.update(weather)

        Log.i(TAG, "Weather changed to $weather, isDemoMode=$demoMode")
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glContext = openGLFactory.create(view, gl as GL11, demoMode, homeOffset)
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
        screenRatio = screenWidth / screenHeight
        landscape = screenRatio > 1f

        gl.glViewport(0, 0, w, h)
        gl.setRenderDefaults()

        currentScene?.scene?.unload()
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
        glAlphaFunc(GL_GEQUAL, .02f)
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
        val scene = currentScene?.scene ?: return

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
        GLU.gluPerspective(this, cameraFOV, screenRatio, 1f, 400f)
        GLU.gluLookAt(this,
            cameraPos.x, cameraPos.y, cameraPos.z,
            cameraPos.x, 400f, cameraPos.z,
            0f, 0f, 1f,
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
        fun create(view: GLSurfaceView): WeatherRenderer
    }

    companion object {
        private const val TAG = "IsolatedRenderer"
    }
}
