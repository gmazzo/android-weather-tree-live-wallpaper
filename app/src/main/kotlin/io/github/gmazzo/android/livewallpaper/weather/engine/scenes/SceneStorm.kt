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
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingLightning
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneStorm(context: Context, gl: GL11?) : SceneBase(context, gl) {
    var lastLightningSpawn: Float
    var light1_ambientLight: FloatArray
    var light1_position: FloatArray
    var lightFlashTime: Float
    var lightFlashX: Float
    var light_ambientLight: FloatArray
    var light_flashColor: FloatArray
    var light_position: FloatArray
    var light_specularLight: FloatArray
    var particleRain: ParticleRain?
    var particleRainOrigin: Vector
    var pref_boltFrequency: Float = 2.0f
    var pref_diffuseLight: FloatArray
    var pref_flashLights: Boolean = true
    var pref_randomBoltColor: Boolean = false
    val rainDensity: Int = 10
    var v_light1_ambientLight: EngineColor

    init {
        this.mThingManager = ThingManager()
        this.lastLightningSpawn = 0.0f
        this.lightFlashTime = 0.0f
        this.lightFlashX = 0.0f
        this.light_ambientLight = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        this.pref_diffuseLight = floatArrayOf(1.5f, 1.5f, 1.5f, 1.0f)
        this.light_specularLight = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
        this.light_position = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        this.light_flashColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        this.v_light1_ambientLight = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
        this.light1_ambientLight = FloatArray(4)
        this.light1_position = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        this.pref_numClouds = 20
        SceneBase.Companion.todEngineColorFinal = EngineColor()
        this.pref_todEngineColors = arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.particleRain = ParticleRain(this.rainDensity)
        this.particleRainOrigin = Vector(0.0f, 25.0f, 10.0f)
        this.reloadAssets = false
    }

    override fun updateWeather(weatherType: WeatherType?) {
        windSpeedFromPrefs()
        numCloudsFromPrefs(weatherType!!)
        todFromPrefs()
        this.pref_randomBoltColor = false
        boltColorFromPrefs()
        boltFrequencyFromPrefs()
    }

    private fun todFromPrefs() {
        pref_todEngineColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f)
        pref_todEngineColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f)
        pref_todEngineColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f)
        pref_todEngineColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f)
    }

    fun boltColorFromPrefs() {
        pref_boltEngineColor.set("1 1 1 1", 0.0f, 1.0f)
    }

    fun boltFrequencyFromPrefs() {
        this.pref_boltFrequency = 5f
    }

    override fun precacheAssets(gl10: GL10?) {
        textures[R.drawable.storm_bg]
        textures[R.drawable.trees_overlay]
        textures[R.drawable.clouddark1]
        textures[R.drawable.clouddark2]
        textures[R.drawable.clouddark3]
        textures[R.drawable.clouddark4]
        textures[R.drawable.clouddark5]
        textures[R.drawable.cloudflare1]
        textures[R.drawable.cloudflare2]
        textures[R.drawable.cloudflare3]
        textures[R.drawable.cloudflare4]
        textures[R.drawable.cloudflare5]
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

    override fun load(gl: GL10?) {
        spawnClouds(false)
    }

    override fun unload(gl: GL10?) {
        super.unload(gl)
        gl!!.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        val iMain = tod.mainIndex
        val iBlend = tod.blendIndex
        v_light1_ambientLight.blend(
            pref_todEngineColors[iMain],
            pref_todEngineColors[iBlend], tod.blendAmount
        )
    }

    override fun draw(gl: GL10, time: GlobalTime) {
        checkAssetReload(gl)
        mThingManager!!.update(time.sTimeDelta)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(gl, time.sTimeElapsed)
        renderRain(gl, time.sTimeDelta)
        checkForLightning(time.sTimeDelta)
        updateLightValues(gl, time.sTimeDelta)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        mThingManager!!.render(gl, textures, models)
        drawTree(gl, time.sTimeDelta)
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
        if (!this.pref_flashLights || this.lightFlashTime <= 0.0f) {
            gl.glEnable(GL10.GL_LIGHTING)
            gl.glEnable(GL10.GL_LIGHT1)
            light1_ambientLight[0] = v_light1_ambientLight.r
            light1_ambientLight[1] = v_light1_ambientLight.g
            light1_ambientLight[2] = v_light1_ambientLight.b
            light1_ambientLight[3] = v_light1_ambientLight.a
            gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, this.light1_ambientLight, 0)
        }
        val mesh = models[R.raw.plane_16x16]
        mesh.render()
        gl.glDisable(GL10.GL_LIGHT1)
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
                val cloud = ThingDarkCloud(true)
                cloud.randomizeScale()
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.x = cloud.scale.x * -1.0f
                }
                cloud.origin.x = ((i.toFloat()) * (90.0f / (num_clouds.toFloat()))) - 0.099609375f
                cloud.origin.y = cloudDepthList[i]
                cloud.origin.z = GlobalRand.floatRange(-20.0f, -10.0f)
                cloud.which = (i % 5) + 1
                cloud.model = null
                cloud.texture = null
                cloud.texNameFlare = null
                cloud.targetName = "dark_cloud"
                cloud.velocity = Vector(SceneBase.Companion.pref_windSpeed * 1.5f, 0.0f, 0.0f)
                mThingManager!!.add(cloud)
                i++
            }
        }
    }

    private fun spawnLightning() {
        if (this.pref_randomBoltColor) {
            GlobalRand.randomNormalizedVector(pref_boltEngineColor)
        }
        val lightning =
            ThingLightning(pref_boltEngineColor.r, pref_boltEngineColor.g, pref_boltEngineColor.b)
        lightning.origin.set(
            GlobalRand.floatRange(-25.0f, 25.0f),
            GlobalRand.floatRange(95.0f, 168.0f),
            20.0f
        )
        if (GlobalRand.intRange(0, 2) == 0) {
            lightning.scale.z = lightning.scale.z * -1.0f
        }
        mThingManager!!.add(lightning)
        mThingManager!!.sortByY()
        this.lightFlashTime = 0.25f
        this.lightFlashX = lightning.origin.x
    }

    private fun checkForLightning(timeDelta: Float) {
        if (GlobalRand.floatRange(0.0f, this.pref_boltFrequency * 0.75f) < timeDelta) {
            spawnLightning()
        }
    }

    private fun updateLightValues(gl: GL10, timeDelta: Float) {
        val lightPosX: Float = GlobalTime.Companion.waveCos(0.0f, 500.0f, 0.0f, 0.005f)
        if (!this.pref_flashLights || this.lightFlashTime <= 0.0f) {
            light_position[0] = lightPosX
            gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4610, this.light_specularLight, 0)
        } else {
            val flashRemaining = this.lightFlashTime / 0.25f
            light_position[0] =
                (this.lightFlashX * flashRemaining) + ((1.0f - flashRemaining) * lightPosX)
            light_flashColor[0] = pref_boltEngineColor.r
            light_flashColor[1] = pref_boltEngineColor.g
            light_flashColor[2] = pref_boltEngineColor.b
            gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4610, this.light_flashColor, 0)
            this.lightFlashTime -= timeDelta
        }
        light_position[1] = 50.0f
        light_position[2] = GlobalTime.Companion.waveSin(0.0f, 500.0f, 0.0f, 0.005f)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4608, this.light_ambientLight, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4609, this.pref_diffuseLight, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4611, this.light_position, 0)
    }

    companion object {
        private const val TAG = "Storm"
        var pref_boltEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
