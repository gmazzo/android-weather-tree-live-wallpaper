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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Named
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_BACK
import javax.microedition.khronos.opengles.GL10.GL_BLEND
import javax.microedition.khronos.opengles.GL10.GL_COLOR_MATERIAL
import javax.microedition.khronos.opengles.GL10.GL_FASTEST
import javax.microedition.khronos.opengles.GL10.GL_GEQUAL
import javax.microedition.khronos.opengles.GL10.GL_LEQUAL
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_PERSPECTIVE_CORRECTION_HINT
import javax.microedition.khronos.opengles.GL10.GL_PROJECTION
import javax.microedition.khronos.opengles.GL10.GL_SMOOTH
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE0
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV_MODE
import javax.microedition.khronos.opengles.GL10.GL_VERTEX_ARRAY
import javax.microedition.khronos.opengles.GL11

internal class WeatherRenderer @AssistedInject constructor(
    private val componentFactory: WeatherRendererComponent.Factory,
    private val weather: MutableStateFlow<WeatherType>,
    @Named("homeOffset") private val homeOffset: MutableStateFlow<Float>,
    @Assisted private val view: GLSurfaceView,
    @Assisted private val logTag: String,
    @Assisted private val demoMode: Boolean,
) : Renderer {
    var screenWidth = 0f
        private set
    var screenHeight = 0f
        private set

    private val screenRatio get() = screenWidth / screenHeight
    private val landscape get() = screenRatio > 1
    private var cameraFOV = 65f
    private var cameraPos = Vector(0f, 0f, 0f)
    private val cameraSpeed: Float = 1f
    private val postRenderActions = ConcurrentLinkedQueue<() -> Unit>()
    private var component: WeatherRendererComponent? = null
    private var scene: SceneComponent? = null

    private fun log(message: String, logger: (String?, String) -> Int = Log::d) {
        logger(
            logTag,
            "$message demoMode=$demoMode, thread=${Thread.currentThread().name}"
        )
    }

    private fun onSceneChanged(weather: WeatherType) {
        val mode = weather.scene

        log("onSceneChanged: scene=$mode,")

        if (scene?.mode != mode) {
            unloadScene()
            scene = component?.sceneFactory?.create(mode, landscape)
        }
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        log("onSurfaceCreated:")
    }

    override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
        log("onSurfaceChanged:")

        screenWidth = w.toFloat()
        screenHeight = h.toFloat()

        gl.glViewport(0, 0, w, h)
        gl.setRenderDefaults()

        onSurfaceDestroyed()
        component = componentFactory.create(view, gl as GL11, demoMode).apply {
            updateCameraPosition(immediate = true)
            coroutineScope.launch { weather.collectLatest(::onSceneChanged) }
        }
    }

    fun onSurfaceDestroyed() = component?.apply {
        coroutineScope.coroutineContext.job.cancel()
        unloadScene()
        models.close()
        textures.close()
    }

    private fun unloadScene() {
        log("unloadScene: scene=${scene?.mode},")
        val sceneLazy = scene?.scene ?: return

        this.scene = null
        if (sceneLazy.isInitialized()) sceneLazy.value.close()
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

        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glMatrixMode(GL_TEXTURE)
        glLoadIdentity()
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
    }

    override fun onDrawFrame(gl: GL10) {
        val scene = scene ?: return

        val component = component!!
        component.time.update()
        component.timeOfDay.update()
        component.updateCameraPosition(immediate = false)
        gl.updateProjection()

        scene.scene.value.draw()

        if (postRenderActions.isNotEmpty()) {
            view.queueEvent {
                Log.d(logTag, "Running ${postRenderActions.size} postRenderActions")

                while (postRenderActions.isNotEmpty()) {
                    postRenderActions.poll()!!()
                }
            }
        }
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

    private fun WeatherRendererComponent.updateCameraPosition(immediate: Boolean) {
        val target = Vector(28 * homeOffset.value - 14, 0f, 0f)

        if (immediate) {
            cameraPos = target

        } else {
            val rate = (3.5f * time.deltaSeconds) * cameraSpeed
            val diff = (target - cameraPos) * rate

            cameraPos += diff
        }
        cameraFOV = if (landscape) 45f else 70f
    }

    fun postRender(action: () -> Unit) {
        postRenderActions.add(action)
    }

    @AssistedFactory
    fun interface Factory {
        fun create(view: GLSurfaceView, logTag: String, demoMode: Boolean): WeatherRenderer
    }

}
