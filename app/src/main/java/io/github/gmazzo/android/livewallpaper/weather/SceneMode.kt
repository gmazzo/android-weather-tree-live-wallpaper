package io.github.gmazzo.android.livewallpaper.weather

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(SceneMode.CLEAR, SceneMode.CLOUDY, SceneMode.FOG, SceneMode.RAIN, SceneMode.SNOW, SceneMode.STORM)
annotation class SceneMode {

    companion object {
        const val CLEAR = IsolatedRenderer.SCENE_CLEAR
        const val CLOUDY = IsolatedRenderer.SCENE_CLOUDY
        const val FOG = IsolatedRenderer.SCENE_FOG
        const val RAIN = IsolatedRenderer.SCENE_RAIN
        const val SNOW = IsolatedRenderer.SCENE_SNOW
        const val STORM = IsolatedRenderer.SCENE_STORM
    }

}
