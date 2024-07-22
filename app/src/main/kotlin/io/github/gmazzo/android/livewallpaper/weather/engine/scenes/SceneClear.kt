package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.Context
import androidx.annotation.DrawableRes
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingCloud
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingMoon
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingSun
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingWispy
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

open class SceneClear @JvmOverloads constructor(
    context: Context,
    gl: GL11?,
    @field:DrawableRes @param:DrawableRes private val backgroundId: Int = R.drawable.bg3
) : SceneBase(context, gl) {
    protected var batteryLevel: Int
    protected var nextUfoSpawn: Float
    protected var smsLastUnreadCheckTime: Long
    protected var smsUnreadCount: Int

    init {
        this.mThingManager = ThingManager()
        todEngineColorFinal = EngineColor()
        this.pref_todEngineColors = arrayOf(EngineColor(), EngineColor(), EngineColor(), EngineColor())
        this.reloadAssets = true
        this.batteryLevel = 100
        this.pref_numClouds = 20
        this.pref_numWisps = 6
        this.nextUfoSpawn = WISPY_X_RANGE
        this.smsUnreadCount = 0
        this.smsLastUnreadCheckTime = 0
    }

    override fun load(gl: GL10?) {
        checkSun()
        checkMoon()
        spawnClouds(false)
    }

    override fun updateWeather(weather: WeatherType?) {
        windSpeedFromPrefs()
        numCloudsFromPrefs(weather!!)
        todFromPrefs()
        checkSun()
        checkMoon()
    }

    override fun precacheAssets(gl10: GL10?) {
        textures[backgroundId]
        textures[R.drawable.trees_overlay]
        textures[R.drawable.cloud1]
        textures[R.drawable.cloud2]
        textures[R.drawable.cloud3]
        textures[R.drawable.cloud4]
        textures[R.drawable.cloud5]
        textures[R.drawable.stars]
        textures[R.drawable.noise]
        textures[R.raw.wispy1]
        textures[R.raw.wispy2]
        textures[R.raw.wispy3]
        textures[R.raw.sun]
        textures[R.raw.sun_blend]
        textures[R.drawable.moon_0]
        models[R.raw.plane_16x16]
        models[R.raw.cloud1m]
        models[R.raw.cloud2m]
        models[R.raw.cloud3m]
        models[R.raw.cloud4m]
        models[R.raw.cloud5m]
        models[R.raw.grass_overlay]
        models[R.raw.trees_overlay]
        models[R.raw.trees_overlay_terrain]
        models[R.raw.stars]
    }

    protected fun spawnClouds(force: Boolean) {
        spawnClouds(this.pref_numClouds, this.pref_numWisps, force)
    }

    private fun checkMoon() {
        spawnMoon()
    }

    private fun checkSun() {
        spawnSun()
    }

    private fun todFromPrefs() {
        pref_todEngineColors[0].set("0.5 0.5 0.75 1", 0.0f, 1.0f)
        pref_todEngineColors[1].set("1 0.73 0.58 1", 0.0f, 1.0f)
        pref_todEngineColors[2].set("1 1 1 1", 0.0f, 1.0f)
        pref_todEngineColors[3].set("1 0.85 0.75 1", 0.0f, 1.0f)
    }

    private fun removeMoon() {
        mThingManager!!.clearByTargetname("moon")
    }

    private fun removeSun() {
        mThingManager!!.clearByTargetname("sun")
    }

    private fun spawnMoon() {
        if (mThingManager!!.countByTargetname("moon") == 0) {
            val moon = ThingMoon()
            moon.origin.set(-30.0f, 100.0f, -100.0f)
            moon.targetName = "moon"
            mThingManager!!.add(moon)
        }
    }

    private fun spawnSun() {
        if (mThingManager!!.countByTargetname("sun") == 0) {
            val sun = ThingSun()
            sun.origin.set(WISPY_Z_RANGE, 100.0f, 0.0f)
            sun.targetName = "sun"
            mThingManager!!.add(sun)
        }
    }

    protected fun spawnClouds(num_clouds: Int, num_wisps: Int, force: Boolean) {
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
                cloud.which = (i % 5) + 1
                cloud.targetName = "cloud"
                cloud.velocity = Vector(SceneBase.Companion.pref_windSpeed * 1.5f, 0.0f, 0.0f)
                mThingManager!!.add(cloud)
                i++
            }
            i = 0
            while (i < cloudDepthList.size) {
                val wispy = ThingWispy()
                wispy.which = (i % 3) + 1
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
        drawTree(gl, time.sTimeDelta)
    }

    private fun renderBackground(gl: GL10, timeDelta: Float) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[backgroundId].glId)
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
        renderStars(gl, timeDelta)
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
    }

    private fun renderStars(gl: GL10, timeDelta: Float) {
        if (SceneBase.Companion.todSunPosition <= 0.0f) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, SceneBase.Companion.todSunPosition * -2.0f)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            val starMesh = models[R.raw.stars]
            val noise = textures[R.drawable.noise]
            val star = textures[R.drawable.stars]
            gl.glTranslatef((0.1f * timeDelta) % 1.0f, 300.0f, -100.0f)
            if (gl is GL11) {
                starMesh.renderFrameMultiTexture(noise, star, GL10.GL_MODULATE, false)
                return
            }
            gl.glBindTexture(GL10.GL_TEXTURE0, star.glId)
            starMesh.render()
        }
    }

    companion object {
        protected const val BALLOON_START_ALTITUDE: Float = -50.0f
        protected const val CLOUD_START_DISTANCE: Float = 175.0f
        const val CLOUD_X_RANGE: Float = 45.0f
        protected const val CLOUD_Z_RANGE: Float = 20.0f
        private const val TAG = "Clear"
        protected const val UFO_START_ALTITUDE: Float = 65.0f
        protected const val WISPY_X_RANGE: Float = 60.0f
        protected const val WISPY_Z_RANGE: Float = 30.0f
        protected var validBalloonTextures: Array<String> =
            arrayOf("bal_red", "bal_blue", "bal_yellow", "bal_green")
    }
}
