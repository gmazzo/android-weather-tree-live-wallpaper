package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.Context
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleSnow
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingCloud
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingWispy
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class SceneSnow(context: Context, gl: GL11?) : SceneBase(context, gl) {
    var particleSnow: ParticleSnow? = null
    var pref_snowDensity: Int = 0
    var snowPos1: Vector
    var snowPos2: Vector
    var snowPos3: Vector

    init {
        this.mThingManager = ThingManager()
        SceneBase.todEngineColorFinal = EngineColor()
        this.pref_todEngineColors = arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.reloadAssets = false
        this.pref_numClouds = 20
        this.pref_numWisps = 6
        this.snowPos1 = Vector(0.0f, CLOUD_Z_RANGE, -20.0f)
        this.snowPos2 = Vector(8.0f, 15.0f, -20.0f)
        this.snowPos3 = Vector(-8.0f, 10.0f, -20.0f)
    }

    override fun load(gl: GL10?) {
        spawnClouds(false)
    }

    override fun updateWeather(weather: WeatherType?) {
        windSpeedFromPrefs()
        numCloudsFromPrefs(weather!!)
        todFromPrefs()
        snowDensityFromPrefs()
        snowGravityFromPrefs()
        snowNoiseFromPrefs()
        snowTypeFromPrefs()
    }

    override fun precacheAssets(gl10: GL10?) {
        textures[R.drawable.bg2]
        textures[R.drawable.trees_overlay]
        textures[R.drawable.cloud1]
        textures[R.drawable.cloud2]
        textures[R.drawable.cloud3]
        textures[R.drawable.cloud4]
        textures[R.drawable.cloud5]
        textures[R.raw.wispy1]
        textures[R.raw.wispy2]
        textures[R.raw.wispy3]
        textures[R.raw.p_snow1]
        textures[R.raw.p_snow2]
        models[R.raw.plane_16x16]
        models[R.raw.cloud1m]
        models[R.raw.cloud2m]
        models[R.raw.cloud3m]
        models[R.raw.cloud4m]
        models[R.raw.cloud5m]
        models[R.raw.grass_overlay]
        models[R.raw.trees_overlay]
        models[R.raw.trees_overlay_terrain]
        models[R.raw.flakes]
    }

    private fun spawnClouds(force: Boolean) {
        spawnClouds(this.pref_numClouds, this.pref_numWisps, force)
    }

    private fun todFromPrefs() {
        pref_todEngineColors[0].set("0.5 0.5 0.75 1", 0.0f, 1.0f)
        pref_todEngineColors[1].set("1 0.73 0.58 1", 0.0f, 1.0f)
        pref_todEngineColors[2].set("1 1 1 1", 0.0f, 1.0f)
        pref_todEngineColors[3].set("1 0.85 0.75 1", 0.0f, 1.0f)
    }

    private fun snowDensityFromPrefs() {
        this.pref_snowDensity = 2
    }

    private fun snowGravityFromPrefs() {
        pref_snowGravity = 2 * 0.5f
    }

    private fun snowNoiseFromPrefs() {
        pref_snowNoise = 7 * 0.1f
    }

    private fun snowTypeFromPrefs() {
        pref_snowImage = "p_snow1"
        this.reloadAssets = true
    }

    private fun spawnClouds(num_clouds: Int, num_wisps: Int, force: Boolean) {
        val cloudsExist = mThingManager!!.countByTargetname("cloud") != 0
        if (force || !cloudsExist) {
            mThingManager!!.clearByTargetname("cloud")
            mThingManager!!.clearByTargetname("wispy")
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
                val cloud = ThingCloud()
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
                cloud.targetName = "cloud"
                cloud.velocity = Vector(SceneBase.Companion.pref_windSpeed * 1.5f, 0.0f, 0.0f)
                mThingManager!!.add(cloud)
                i++
            }
            i = 0
            while (i < cloudDepthList.size) {
                val which = ((i % 3) + 1)
                val wispy = ThingWispy()
                wispy.model = models[R.raw.plane_16x16]
                wispy.texture = textures[WISPY_TEXTURES[which - 1]]
                wispy.targetName = "wispy"
                wispy.velocity = Vector(SceneBase.Companion.pref_windSpeed * 1.5f, 0.0f, 0.0f)
                wispy.scale.set(
                    GlobalRand.floatRange(1.0f, 3.0f),
                    1.0f,
                    GlobalRand.floatRange(1.0f, 1.5f)
                )
                wispy.origin.x = ((i.toFloat()) * (120.0f / (num_wisps.toFloat()))) - 0.0703125f
                wispy.origin.y = GlobalRand.floatRange(87.5f, CLOUD_START_DISTANCE)
                wispy.origin.z = GlobalRand.floatRange(-40.0f, -20.0f)
                mThingManager!!.add(wispy)
                i++
            }
        }
    }

    override fun updateTimeOfDay(tod: TimeOfDay) {
        SceneBase.Companion.todSunPosition = tod.sunPosition
        super.updateTimeOfDay(tod)
    }

    override fun draw(gl: GL10, time: GlobalTime) {
        checkAssetReload(gl)
        mThingManager!!.update(time.sTimeDelta)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(gl, time.sTimeElapsed)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        mThingManager!!.render(gl, textures, models)
        renderSnow(gl, time.sTimeDelta)
        drawTree(gl, time.sTimeDelta)
    }

    private fun renderSnow(gl: GL10, timeDelta: Float) {
        if (this.particleSnow == null) {
            this.particleSnow = ParticleSnow()
        }
        particleSnow!!.update(timeDelta)
        particleSnow!!.render(gl as GL11, this.snowPos1)
        if (this.pref_snowDensity > 1) {
            particleSnow!!.render(gl, this.snowPos2)
        }
        if (this.pref_snowDensity > 2) {
            particleSnow!!.render(gl, this.snowPos3)
        }
    }

    private fun renderBackground(gl: GL10, timeDelta: Float) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[R.drawable.bg2].glId)
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

    companion object {
        private val CLOUD_MODELS = intArrayOf(
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m
        )
        private val CLOUD_TEXTURES = intArrayOf(
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5
        )
        private val WISPY_TEXTURES = intArrayOf(R.raw.wispy1, R.raw.wispy2, R.raw.wispy3)
        const val CLOUD_START_DISTANCE: Float = 175.0f
        const val CLOUD_X_RANGE: Float = 45.0f
        const val CLOUD_Z_RANGE: Float = 20.0f
        private const val TAG = "Snow"
        const val WISPY_X_RANGE: Float = 60.0f
        const val WISPY_Z_RANGE: Float = 30.0f
        var pref_snowGravity: Float = 0f
        var pref_snowImage: String? = null
        var pref_snowNoise: Float = 0f
    }
}
