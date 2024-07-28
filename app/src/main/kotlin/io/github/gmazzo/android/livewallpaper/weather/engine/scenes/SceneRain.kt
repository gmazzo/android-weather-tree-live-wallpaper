package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleRain
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingDarkCloud
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneRain @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
) : Scene(gl, models, textures) {
    
    private val particleRain by lazy { ParticleRain(models, textures, rainDensity) }
    
    private var lightDiffuse: FloatArray
    private var particleRainOrigin: Vector
    private val rainDensity: Int = 10
    private var lightDiffuseColor: EngineColor

    init {
        todEngineColorFinal = EngineColor()
        this.todEngineColors = arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.reloadAssets = false
        this.numClouds = 20
        this.numWisps = 6
        this.lightDiffuseColor = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
        this.lightDiffuse = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
        this.particleRainOrigin = Vector(0.0f, 25.0f, 10.0f)
    }

    override fun load() {
        spawnClouds(false)
    }

    private fun spawnClouds(force: Boolean) {
        spawnClouds(this.numClouds, force)
    }

    private fun spawnClouds(num_clouds: Int, force: Boolean) {
        val cloudsExist = thingManager.countByTargetname("dark_cloud") != 0
        if (force || !cloudsExist) {
            thingManager.clearByTargetname("dark_cloud")
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
                val cloud = ThingDarkCloud(models,textures, i, false)
                cloud.randomizeScale()
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.x *= -1.0f
                }
                cloud.origin.x = ((i.toFloat()) * (90.0f / (num_clouds.toFloat()))) - 0.099609375f
                cloud.origin.y = cloudDepthList[i]
                cloud.origin.z = GlobalRand.floatRange(-20.0f, -10.0f)
                cloud.targetName = "dark_cloud"
                cloud.velocity = Vector(pref_windSpeed * 1.5f, 0.0f, 0.0f)
                thingManager.add(cloud)
                i++
            }
        }
    }

    override fun updateWeather(weather: WeatherType) {
        windSpeedFromPrefs()
        numCloudsFromPrefs(weather)
        todFromPrefs()
    }

    private fun todFromPrefs() {
        todEngineColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f)
        todEngineColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f)
        todEngineColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f)
        todEngineColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f)
    }

    override fun precacheAssets() {
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
        lightDiffuseColor.blend(
            todEngineColors[iMain],
            todEngineColors[iBlend], tod.blendAmount
        )
    }

    private fun renderBackground(timeDelta: Float) {
        val stormBg = textures[R.drawable.storm_bg]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, stormBg.glId)
        gl.glColor4f(
            todEngineColorFinal!!.r,
            todEngineColorFinal!!.g,
            todEngineColorFinal!!.b,
            1.0f
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(this.bgPadding * 2.0f, this.bgPadding, this.bgPadding)
        gl.glMatrixMode(GL10.GL_TEXTURE)
        gl.glPushMatrix()
        gl.glTranslatef(
            ((pref_windSpeed * timeDelta) * -0.005f) % 1.0f,
            0.0f,
            0.0f
        )
        val mesh = models[R.raw.plane_16x16]
        mesh.render()
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
    }

    private fun renderRain(timeDelta: Float) {
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 0.0f, -5.0f)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        particleRain.update(timeDelta)
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO)
        particleRain.render(gl as GL11, this.particleRainOrigin)
        gl.glPopMatrix()
    }

    override fun draw(time: GlobalTime) {
        thingManager.update(time.sTimeDelta)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        lightDiffuse[0] = lightDiffuseColor.r
        lightDiffuse[1] = lightDiffuseColor.g
        lightDiffuse[2] = lightDiffuseColor.b
        lightDiffuse[3] = lightDiffuseColor.a
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4609, this.lightDiffuse, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4608, this.lightDiffuse, 0)
        renderBackground(time.sTimeElapsed)
        renderRain(time.sTimeDelta)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        thingManager.render(gl)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHTING)
        drawTree(time.sTimeDelta)
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
