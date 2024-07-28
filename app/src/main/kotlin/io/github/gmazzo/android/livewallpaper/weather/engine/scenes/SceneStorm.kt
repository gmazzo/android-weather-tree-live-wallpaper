package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleRain
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingDarkCloud
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingLightning
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneStorm @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
) : Scene(gl, models, textures) {
    private var lightAmbientLight: FloatArray
    private var lightPosition: FloatArray
    private var lightFlashTime: Float
    private var lightFlashX: Float
    private var ambientLight: FloatArray
    private var flashColor: FloatArray
    private var possition: FloatArray
    private var specularLight: FloatArray
    private var particleRain: ParticleRain?
    private var particleRainOrigin: Vector
    private var boltFrequency: Float = 2.0f
    private var diffuseLight: FloatArray
    private var flashLights: Boolean = true
    private var randomBoltColor: Boolean = false
    private val rainDensity: Int = 10
    private var lightAmbientLightColor: EngineColor

    init {
        this.lightFlashTime = 0.0f
        this.lightFlashX = 0.0f
        this.ambientLight = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        this.diffuseLight = floatArrayOf(1.5f, 1.5f, 1.5f, 1.0f)
        this.specularLight = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
        this.possition = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        this.flashColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        this.lightAmbientLightColor = EngineColor(0.5f, 0.5f, 0.5f, 1.0f)
        this.lightAmbientLight = FloatArray(4)
        this.lightPosition = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        this.numClouds = 20
        todEngineColorFinal = EngineColor()
        this.todEngineColors =
            arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.particleRain = ParticleRain(models, textures, rainDensity)
        this.particleRainOrigin = Vector(0.0f, 25.0f, 10.0f)
        this.reloadAssets = false
    }

    override fun updateWeather(weather: WeatherType) {
        windSpeedFromPrefs()
        numCloudsFromPrefs(weather)
        todFromPrefs()
        this.randomBoltColor = false
        boltColorFromPrefs()
        boltFrequencyFromPrefs()
    }

    private fun todFromPrefs() {
        todEngineColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f)
        todEngineColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f)
        todEngineColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f)
        todEngineColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f)
    }

    private fun boltColorFromPrefs() {
        pref_boltEngineColor.set("1 1 1 1", 0.0f, 1.0f)
    }

    private fun boltFrequencyFromPrefs() {
        this.boltFrequency = 5f
    }

    override fun precacheAssets() {
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

    override fun load() {
        spawnClouds(false)
    }

    override fun unload() {
        super.unload()

        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        val iMain = tod.mainIndex
        val iBlend = tod.blendIndex
        lightAmbientLightColor.blend(
            todEngineColors[iMain],
            todEngineColors[iBlend], tod.blendAmount
        )
    }

    override fun draw(time: GlobalTime) {
        thingManager.update(time.sTimeDelta)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.sTimeElapsed)
        renderRain(time.sTimeDelta)
        checkForLightning(time.sTimeDelta)
        updateLightValues(time.sTimeDelta)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        thingManager.render(gl)
        drawTree(time.sTimeDelta)
    }

    private fun renderBackground(timeDelta: Float) {
        val storm_bg = textures[R.drawable.storm_bg]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, storm_bg.glId)
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
        if (!this.flashLights || this.lightFlashTime <= 0.0f) {
            gl.glEnable(GL10.GL_LIGHTING)
            gl.glEnable(GL10.GL_LIGHT1)
            lightAmbientLight[0] = lightAmbientLightColor.r
            lightAmbientLight[1] = lightAmbientLightColor.g
            lightAmbientLight[2] = lightAmbientLightColor.b
            lightAmbientLight[3] = lightAmbientLightColor.a
            gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, this.lightAmbientLight, 0)
        }
        val mesh = models[R.raw.plane_16x16]
        mesh.render()
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
    }

    private fun renderRain(timeDelta: Float) {
        if (this.particleRain == null) {
            this.particleRain = ParticleRain(models, textures, rainDensity)
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
        spawnClouds(this.numClouds, force)
    }

    private fun spawnClouds(clouds: Int, force: Boolean) {
        val cloudsExist = thingManager.countByTargetname("dark_cloud") != 0
        if (force || !cloudsExist) {
            thingManager.clearByTargetname("dark_cloud")
            val cloudDepthList = FloatArray(clouds)
            val cloudDepthStep = 131.25f / (clouds.toFloat())
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
                val cloud = ThingDarkCloud(models, textures, i, true)
                cloud.randomizeScale()
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.x *= -1.0f
                }
                cloud.origin.x = ((i.toFloat()) * (90.0f / clouds)) - 0.099609375f
                cloud.origin.y = cloudDepthList[i]
                cloud.origin.z = GlobalRand.floatRange(-20.0f, -10.0f)
                cloud.targetName = "dark_cloud"
                cloud.velocity = Vector(pref_windSpeed * 1.5f, 0.0f, 0.0f)
                thingManager.add(cloud)
                i++
            }
        }
    }

    private fun spawnLightning() {
        if (this.randomBoltColor) {
            GlobalRand.randomNormalizedVector(pref_boltEngineColor)
        }
        val lightning = ThingLightning(models, textures, pref_boltEngineColor)
        lightning.origin.set(
            GlobalRand.floatRange(-25.0f, 25.0f),
            GlobalRand.floatRange(95.0f, 168.0f),
            20.0f
        )
        if (GlobalRand.intRange(0, 2) == 0) {
            lightning.scale.z = lightning.scale.z * -1.0f
        }
        thingManager.add(lightning)
        thingManager.sortByY()
        this.lightFlashTime = 0.25f
        this.lightFlashX = lightning.origin.x
    }

    private fun checkForLightning(timeDelta: Float) {
        if (GlobalRand.floatRange(0.0f, this.boltFrequency * 0.75f) < timeDelta) {
            spawnLightning()
        }
    }

    private fun updateLightValues(timeDelta: Float) {
        val lightPosX: Float = GlobalTime.Companion.waveCos(0.0f, 500.0f, 0.0f, 0.005f)
        if (!this.flashLights || this.lightFlashTime <= 0.0f) {
            possition[0] = lightPosX
            gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4610, this.specularLight, 0)
        } else {
            val flashRemaining = this.lightFlashTime / 0.25f
            possition[0] =
                (this.lightFlashX * flashRemaining) + ((1.0f - flashRemaining) * lightPosX)
            flashColor[0] = pref_boltEngineColor.r
            flashColor[1] = pref_boltEngineColor.g
            flashColor[2] = pref_boltEngineColor.b
            gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4610, this.flashColor, 0)
            this.lightFlashTime -= timeDelta
        }
        possition[1] = 50.0f
        possition[2] = GlobalTime.Companion.waveSin(0.0f, 500.0f, 0.0f, 0.005f)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4608, this.ambientLight, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4609, this.diffuseLight, 0)
        gl.glLightfv(GL10.GL_COLOR_BUFFER_BIT, 4611, this.possition, 0)
    }

    companion object {
        private const val TAG = "Storm"
        var pref_boltEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
