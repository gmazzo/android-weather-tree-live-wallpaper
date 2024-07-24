package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10

class SceneFog(
    models: Models,
    textures: Textures,
) : SceneBase(models, textures) {
    var fogEngineColorFinal: EngineColor
    var fog_todEngineColors: Array<EngineColor?>

    init {
        todEngineColorFinal = EngineColor()
        this.pref_todEngineColors = arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.fogEngineColorFinal = EngineColor()
        this.fog_todEngineColors = arrayOfNulls(4)
        this.reloadAssets = false
    }

    override fun load(gl: GL10) {
    }

    override fun updateWeather(weather: WeatherType) {
        windSpeedFromPrefs()
        todFromPrefs()
        pref_fog_density = 0.2f
    }

    override fun precacheAssets(gl: GL10) {
        textures[R.drawable.bg1]
        textures[R.drawable.trees_overlay]
        textures[R.raw.sun]
        textures[R.raw.sun_blend]
        models[R.raw.plane_16x16]
        models[R.raw.grass_overlay]
        models[R.raw.trees_overlay]
        models[R.raw.trees_overlay_terrain]
    }

    private fun todFromPrefs() {
        pref_todEngineColors[0].set("0.5 0.5 0.75 1", 0.0f, 1.0f)
        pref_todEngineColors[1].set("1 0.73 0.58 1", 0.0f, 1.0f)
        pref_todEngineColors[2].set("1 1 1 1", 0.0f, 1.0f)
        pref_todEngineColors[3].set("1 0.85 0.75 1", 0.0f, 1.0f)
        fog_todEngineColors[0] = EngineColor(0.2f, 0.2f, 0.2f, 1.0f)
        fog_todEngineColors[1] = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
        fog_todEngineColors[2] = EngineColor(0.8f, 0.8f, 0.8f, 1.0f)
        fog_todEngineColors[3] = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        val iMain = tod.mainIndex
        val iBlend = tod.blendIndex
        val blendAmount = tod.blendAmount
        todEngineColorFinal!!.blend(
            pref_todEngineColors[iMain],
            pref_todEngineColors[iBlend], blendAmount
        )
        fogEngineColorFinal.blend(
            fog_todEngineColors[iMain]!!,
            fog_todEngineColors[iBlend]!!, blendAmount
        )
        fogEngineColorFinal.setToArray(fogColor)
    }

    override fun draw(gl: GL10, time: GlobalTime) {
        thingManager.update(time.sTimeDelta)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glEnable(GL10.GL_FOG)
        gl.glFogf(GL10.GL_FOG_MODE, GL10.GL_LINEAR.toFloat())
        gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0)
        gl.glFogf(GL10.GL_FOG_DENSITY, pref_fog_density)
        gl.glFogf(GL10.GL_FOG_START, -10.0f)
        gl.glFogf(GL10.GL_FOG_END, 190.0f)
        gl.glFogf(GL10.GL_FOG_HINT, 4352.0f)
        renderBackground(gl, time.sTimeElapsed)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        thingManager.render(gl)
        drawTree(gl, time.sTimeDelta)
        gl.glDisable(GL10.GL_FOG)
    }

    private fun renderBackground(gl: GL10, timeDelta: Float) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[R.drawable.bg1].glId)
        gl.glColor4f(
            todEngineColorFinal!!.r,
            todEngineColorFinal!!.g,
            todEngineColorFinal!!.b,
            1.0f
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING)
        gl.glMatrixMode(GL10.GL_TEXTURE)
        gl.glPushMatrix()
        gl.glTranslatef(
            ((pref_windSpeed * timeDelta) * -0.005f) % 1.0f,
            0.0f,
            0.0f
        )
        val model = models[R.raw.plane_16x16]
        model.render()
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)

        gl.glPopMatrix()
    }

    companion object {
        var fogColor: FloatArray = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f)
        var pref_fog_density: Float = 0.2f
    }
}
