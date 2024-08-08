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
import kotlinx.coroutines.cancelChildren
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
    private val openGLFactory: WeatherRendererComponent.Factory,
    private val weather: MutableStateFlow<WeatherType>,
    @Assisted private val view: GLSurfaceView,
    @Assisted private val tag: String,
    @Assisted private val demoMode: Boolean,
) : Renderer {
    private var landscape: Boolean = false
    private var cameraFOV = 65f
    private var cameraPos = Vector(0f, 0f, 0f)
    private var currentScene: SceneComponent? = null
    private val cameraSpeed: Float = 1f
    private var screenHeight = 0f
    private var screenRatio = 1f
    private var screenWidth = 0f
    private val homeOffset = MutableStateFlow(.5f)
    private lateinit var component: WeatherRendererComponent

    private fun log(message: String, logger: (String?, String) -> Int = Log::d) {
        logger(
            tag,
            "$message demoMode=$demoMode, thread=${Thread.currentThread().name}"
        )
    }

    fun onPause() {
        log("onPause:")

        component.coroutineJob.cancelChildren()
        unloadScene()
    }

    fun onResume() {
        log("onResume:")

        if (::component.isInitialized) {
            watchWeatherChanges()
        }
    }

    private fun watchWeatherChanges() {
        log("watchWeatherChanges:")

        component.coroutineScope.launch {
            weather.collectLatest(::onSceneChanged)
        }
    }

    private fun onSceneChanged(weather: WeatherType) {
        log("onSceneChanged: weather=$weather,")

        val mode = weather.scene

        if (currentScene?.mode != mode) {
            unloadScene()
            currentScene = component.sceneFactory.create(mode, landscape)
        }
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        component = openGLFactory.create(view, gl as GL11, demoMode, homeOffset)
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        log("onSurfaceChanged:")

        screenWidth = w.toFloat()
        screenHeight = h.toFloat()
        screenRatio = screenWidth / screenHeight
        landscape = screenRatio > 1f

        gl.glViewport(0, 0, w, h)
        gl.setRenderDefaults()

        unloadScene()
        component.models.close()
        component.textures.close()

        watchWeatherChanges()
    }

    private fun unloadScene() {
        val scene = currentScene ?: return
        currentScene = null

        if (scene.scene.isInitialized()) {
            scene.scene.value.close()
        }
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

    override fun onDrawFrame(gl: GL10) {
        val scene = currentScene?.scene?.value ?: return

        component.time.update()
        component.timeOfDay.update()

        updateCameraPosition()
        gl.updateProjection()

        scene.draw()
    }

    private fun GL10.updateProjection() {
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        GLU.gluPerspective(this, cameraFOV, screenRatio, 1f, 400f)
        GLU.gluLookAt(
            this,
            cameraPos.x, cameraPos.y, cameraPos.z,
            cameraPos.x, 400f, cameraPos.z,
            0f, 0f, 1f,
        )
    }

    fun updateOffset(offset: Float) {
        homeOffset.value = offset
    }

    private fun updateCameraPosition() {
        val rate = (3.5f * component.time.deltaSeconds) * cameraSpeed
        val diff = (Vector(28 * homeOffset.value - 14, 0f, 0f) - cameraPos) * rate

        cameraPos += diff
        cameraFOV = if (landscape) 45f else 70f
    }

    @AssistedFactory
    interface Factory {
        fun create(view: GLSurfaceView, tag: String, demoMode: Boolean): WeatherRenderer
    }

}
