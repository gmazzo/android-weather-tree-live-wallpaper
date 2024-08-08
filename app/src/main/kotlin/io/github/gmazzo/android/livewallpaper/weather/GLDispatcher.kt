package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@WeatherRendererScoped
class GLDispatcher @Inject constructor(
    private val view: GLSurfaceView,
) : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        view.queueEvent(block)
    }

}
