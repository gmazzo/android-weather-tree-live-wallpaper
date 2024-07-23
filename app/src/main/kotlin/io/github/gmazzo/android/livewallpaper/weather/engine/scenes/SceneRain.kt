package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.Context
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleRain
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingDarkCloud
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneRain(context: Context, gl: GL11) : SceneBase(context, gl) {
    var light_diffuse: FloatArray
    var particleRain: ParticleRain?
    var particleRainOrigin: Vector
    val rainDensity: Int = 10
    var v_light_diffuse: EngineColor

    init {
        this.mThingManager = ThingManager()
        todEngineColorFinal = EngineColor()
        this.pref_todEngineColors = arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.reloadAssets = false
        this.pref_numClouds = 20
        this.pref_numWisps = 6
        this.v_light_diffuse = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
        this.light_diffuse = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
        this.particleRain = ParticleRain(this.rainDensity)
        this.particleRainOrigin = Vector(0.0f, 25.0f, 10.0f)
    }

    override fun load(gl: GL10?) {
        spawnClouds(false)
    }

    private fun spawnClouds(force: Boolean) {
        spawnClouds(this.pref_numClouds, force)
    }

    private fun spawnClouds(num_clouds: Int, force: Boolean) {
        val cloudsExist = mThingManager!!.countByTargetname("dark_cloud") != 0
        if (force || !cloudsExist) {
            mThingManager!!.clearByTargetname("dark_cloud")
            val cloudDepthList = FloatArray(num_clouds)
            val cloudDepthStep = 131.25f / (num_clouds.toFloat())
            var i = 0
            while (i < cloudDepthList.size) {
                cloudDepthList[i] = ((i.toFloat()) * cloudDepthStep) + 43.75f
                i++
            }
            i = 0
            while (i < cloudDepthList.size) {
                val f4 = cloudDepthList[i]
                val i2 = GlobalRand.intRange(0, cloudDepthList.size)
                cloudDepthList[i] = cloudDepthList[i2]
                cloudDepthList[i2] = f4
                i++
            }
            i = 0
            while (i < cloudDepthList.size) {
                val cloud = ThingDarkCloud(false)
                cloud.randomizeScale()
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.x = cloud.scale.x * -1.0f
                }
                cloud.origin.x = ((i.toFloat()) * (90.0f / (num_clouds.toFloat()))) - 0.099609375f
                cloud.origin.y = cloudDepthList[i]
                cloud.origin.z = GlobalRand.floatRange(-20.0f, -10.0f)
                val which = (i % 5) + 1
                cloud.model = models[CLOUD_MODELS[which - 1]]
                cloud.texture = textures[CLOUD_TEXTURES[which - 1]]
                cloud.targetName = "dark_cloud"
                cloud.velocity = Vector(SceneBase.Companion.pref_windSpeed * 1.5f, 0.0f, 0.0f)
                mThingManager!!.add(cloud)
                i++
            }
        }
    }

    override fun updateWeather(weather: WeatherType?) {
        windSpeedFromPrefs()
        numCloudsFromPrefs(weather!!)
        todFromPrefs()
    }

    private fun todFromPrefs() {
        pref_todEngineColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f)
        pref_todEngineColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f)
        pref_todEngineColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f)
        pref_todEngineColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f)
    }

    override fun precacheAssets(gl10: GL10?) {
        textures[R.drawable.storm_bg]
        textures[R.drawable.trees_overlay]
        textures[R.drawable.clouddark1]
        textures[R.drawable.clouddark2]
        textures[R.drawable.clouddark3]
        textures[R.drawable.clouddark4]
        textures[R.drawable.clouddark5]
        textures[R.drawable.raindrop]
        models[R.raw.plane_16x16]
        models[R.raw.cloud1m]
        models[R.raw.cloud2m]
        models[R.raw.cloud3m]
        models[R.raw.cloud4m]
        models[R.raw.cloud5m]
        models[R.raw.grass_overlay]
        models[R.raw.trees_overlay]
        models[R.raw.trees_overlay_terrain]
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        val iMain = tod.mainIndex
        val iBlend = tod.blendIndex
        v_light_diffuse.blend(
            pref_todEngineColors[iMain],
            pref_todEngineColors[iBlend], tod.blendAmount
        )
    }

    private fun renderBackground(gl: GL10, timeDelta: Float) {
        val storm_bg = textures[R.drawable.storm_bg]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, storm_bg.glId)
        gl.glColor4f(
            SceneBase.Companion.todEngineColorFinal!!.r,
            SceneBase.Companion.todEngineColorFinal!!.g,
            SceneBase.Companion.todEngineColorFinal!!.b,
            1.0f
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING)
        gl.glMatrixMode(GL10.GL_TEXTURE)
        gl.glPushMatrix()
        gl.glTranslatef(
            ((SceneBase.Companion.pref_windSpeed * timeDelta) * -0.005f) % 1.0f,
            0.0f,
            0.0f
        )
        val mesh = models[R.raw.plane_16x16]
        mesh.render()
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
    }

    private fun renderRain(gl: GL10, timeDelta: Float) {
        if (this.particleRain == null) {
            this.particleRain = ParticleRain(this.rainDensity)
        }
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 0.0f, -5.0f)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        particleRain!!.update(timeDelta)
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO)
        particleRain!!.render(gl as GL11, this.particleRainOrigin)
        gl.glPopMatrix()
    }

    override fun draw(gl: GL10, time: GlobalTime) {
        checkAssetReload(gl)
        mThingManager!!.update(time.sTimeDelta)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        light_diffuse[0] = v_light_diffuse.r
        light_diffuse[1] = v_light_diffuse.g
        light_diffuse[2] = v_light_diffuse.b
        light_diffuse[3] = v_light_diffuse.a
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4609, this.light_diffuse, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4608, this.light_diffuse, 0)
        renderBackground(gl, time.sTimeElapsed)
        renderRain(gl, time.sTimeDelta)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        mThingManager!!.render(gl, this.textures, this.models)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHTING)
        drawTree(gl, time.sTimeDelta)
    }

    companion object {
        private val CLOUD_MODELS = intArrayOf(
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m
        )
        private val CLOUD_TEXTURES = intArrayOf(
            R.drawable.clouddark1, R.drawable.clouddark2, R.drawable.clouddark3,
            R.drawable.clouddark4, R.drawable.clouddark5
        )
    }
}
