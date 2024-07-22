package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.Context
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.AnimatedModel
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

abstract class SceneBase(context: Context, gl: GL11?) : Scene(context, gl) {
    protected var BG_PADDING: Float = 20.0f
    protected val DBG: Boolean = false
    protected var TREE_ANIMATE_DELAY_MIN: Float = 3.0f
    protected var TREE_ANIMATE_DELAY_RANGE: Float = 7.0f
    protected var mGlobalTime: GlobalTime? = null
    protected var pref_numClouds: Int = 0
    protected var pref_numWisps: Int = 0
    lateinit var pref_todEngineColors: Array<EngineColor>
    protected var pref_treeAnim: Boolean = true
    protected var reloadAssets: Boolean = false
    protected var treesAnim: AnimPlayer = AnimPlayer(0, 19, 5.0f, false)
    protected var treesAnimateDelay: Float = 5.0f

    protected fun checkAssetReload(gl10: GL10?) {
        if (this.reloadAssets) {
            synchronized(this) {
                models.close()
                textures.close()
                precacheAssets(gl10)
                this.reloadAssets = false
            }
        }
    }

    override fun unload(gl: GL10?) {
        models.close()
        textures.close()
        mThingManager!!.clear()
    }

    fun numCloudsFromPrefs(weather: WeatherType) {
        this.pref_numClouds = weather.clouds
    }

    fun windSpeedFromPrefs() {
        pref_windSpeed = 3 * 0.5f
    }

    override fun update(globalTime: GlobalTime?) {
        this.mGlobalTime = globalTime
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        val iMain = tod.mainIndex
        val iBlend = tod.blendIndex
        todEngineColorFinal!!.blend(
            pref_todEngineColors[iMain],
            pref_todEngineColors[iBlend], tod.blendAmount
        )
    }

    protected fun drawTree(gl: GL10, timeDelta: Float) {
        if (this.pref_treeAnim && treesAnim.count > 0) {
            this.treesAnimateDelay -= timeDelta
            if (this.treesAnimateDelay <= 0.0f) {
                this.treesAnimateDelay =
                    this.TREE_ANIMATE_DELAY_MIN + (this.TREE_ANIMATE_DELAY_RANGE * GlobalRand.rand.nextFloat())
                treesAnim.reset()
            }
        }

        val tree_terrain = models[R.raw.trees_overlay_terrain]
        val trees_overlay = textures[R.drawable.trees_overlay]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, trees_overlay.glId)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        if (this.mLandscape) {
            gl.glTranslatef(2.0f, 70.0f, -65.0f)
        } else {
            gl.glTranslatef(-8.0f, 70.0f, -70.0f)
        }
        gl.glScalef(5.0f, 5.0f, 5.0f)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        tree_terrain.render()

        val grass = models[R.raw.grass_overlay] as AnimatedModel
        val tree = models[R.raw.trees_overlay] as AnimatedModel
        grass.animator = treesAnim
        tree.animator = treesAnim
        treesAnim.update(timeDelta)
        tree.render()
        grass.render()

        gl.glPopMatrix()
    }

    companion object {
        var pref_windSpeed: Float = 3.0f
        var todEngineColorFinal: EngineColor? = null
        var todSunPosition: Float = 0.0f
    }
}
