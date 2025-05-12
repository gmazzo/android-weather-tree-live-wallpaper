package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest
import android.graphics.Bitmap
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import androidx.activity.ComponentActivity
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.android.tools.screenshot.ImageDiffer
import com.android.tools.screenshot.Verify
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.gmazzo.android.livewallpaper.weather.actions.AdvanceTime
import io.github.gmazzo.android.livewallpaper.weather.actions.TakeSurfaceSnapshot
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.io.FileNotFoundException
import java.time.ZonedDateTime
import javax.imageio.ImageIO
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.outputStream
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@LargeTest
@HiltAndroidTest
class WeatherViewSnapshotTest {

    @get:Rule
    val permissions = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @get:Rule
    val hilt = HiltAndroidRule(this)

    @get:Rule
    val scenario = activityScenarioRule<ComponentActivity>()

    @Inject
    lateinit var weather: MutableStateFlow<WeatherType>

    @Inject
    lateinit var viewFactory: WeatherView.Factory

    private val outputDir =
        InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let(::Path)
            ?: getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toPath()

    @Test
    fun clear00hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE)

    @Test
    fun clear05hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(5))

    @Test
    fun clear06hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(6))

    @Test
    fun clear07hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(7))

    @Test
    fun clear12hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(12))

    @Test
    fun clear17hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(17))

    @Test
    fun clear18hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(18))

    @Test
    fun clear19hs() = testScene(SceneMode.CLEAR, REFERENCE_DATE.plusHours(19))

    @Test
    fun cloudy00hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE)

    @Test
    fun cloudy05hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(5))

    @Test
    fun cloudy06hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(6))

    @Test
    fun cloudy07hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(7))

    @Test
    fun cloudy12hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(12))

    @Test
    fun cloudy17hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(17))

    @Test
    fun cloudy18hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(18))

    @Test
    fun cloudy19hs() = testScene(SceneMode.CLOUDY, REFERENCE_DATE.plusHours(19))

    @Test
    fun rain00hs() = testScene(SceneMode.RAIN, REFERENCE_DATE)

    @Test
    fun rain05hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(5))

    @Test
    fun rain06hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(6))

    @Test
    fun rain07hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(7))

    @Test
    fun rain12hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(12))

    @Test
    fun rain17hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(17))

    @Test
    fun rain18hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(18))

    @Test
    fun rain19hs() = testScene(SceneMode.RAIN, REFERENCE_DATE.plusHours(19))

    @Test
    fun storm00hs() = testScene(SceneMode.STORM, REFERENCE_DATE)

    @Test
    fun storm05hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(5))

    @Test
    fun storm06hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(6))

    @Test
    fun storm07hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(7))

    @Test
    fun storm12hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(12))

    @Test
    fun storm17hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(17))

    @Test
    fun storm18hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(18))

    @Test
    fun storm19hs() = testScene(SceneMode.STORM, REFERENCE_DATE.plusHours(19))

    @Test
    fun show00hs() = testScene(SceneMode.SNOW, REFERENCE_DATE)

    @Test
    fun show05hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(5))

    @Test
    fun show06hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(6))

    @Test
    fun show07hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(7))

    @Test
    fun show12hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(12))

    @Test
    fun show17hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(17))

    @Test
    fun show18hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(18))

    @Test
    fun show19hs() = testScene(SceneMode.SNOW, REFERENCE_DATE.plusHours(19))

    @Test
    fun fog00hs() = testScene(SceneMode.FOG, REFERENCE_DATE)

    @Test
    fun fog05hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(5))

    @Test
    fun fog06hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(6))

    @Test
    fun fog07hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(7))

    @Test
    fun fog12hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(12))

    @Test
    fun fog17hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(17))

    @Test
    fun fog18hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(18))

    @Test
    fun fog19hs() = testScene(SceneMode.FOG, REFERENCE_DATE.plusHours(19))

    private fun testScene(scene: SceneMode, startTime: ZonedDateTime) {
        var time = startTime
        currentTime = { time }
        hilt.inject()

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val referenceName = "${discriminatorFor("scene", scene, startTime)}.png"
        val referenceFile = instrumentation.targetContext.cacheDir.resolve(referenceName).apply {
            parentFile?.mkdirs()
            try {
                instrumentation.context.assets.open("screenshots/$referenceName").use { content ->
                    outputStream().use(content::copyTo)
                }
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            }
        }

        weather.value = WeatherType.valueOf(scene)

        scenario.scenario.onActivity { activity ->
            val view = viewFactory.create(activity, "WeatherViewSnapshotTest", false)
            view.id = R.id.weatherView
            view.renderMode = RENDERMODE_WHEN_DIRTY

            activity.setContentView(view)
        }

        val actualFile = outputDir.resolve("screenshots").resolve(referenceName)
            .createParentDirectories()
            .createFile()

        onView(withId(R.id.weatherView)).perform(
            AdvanceTime(amount = 5.seconds) { time += it.toJavaDuration() },
            TakeSurfaceSnapshot { bitmap ->
                actualFile.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            },
        )
        onIdle()

        val verify = Verify(
            imageDiffer = ImageDiffer.PixelPerfect(imageDiffThreshold = 0.01f),
            diffFilePath = outputDir.resolve("screenshots-diffs").resolve(referenceName),
        )
        val result = verify.assertMatchReference(
            referencePath = referenceFile.toPath(),
            image = ImageIO.read(actualFile.toFile())!!,
        )

        assertTrue(
            "Scenes doesn't match: ${result.message}",
            result is Verify.AnalysisResult.Passed
        )
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            IdlingRegistry.getInstance().register(
                AdvanceTime,
                TakeSurfaceSnapshot,
            )
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            IdlingRegistry.getInstance().unregister(
                AdvanceTime,
                TakeSurfaceSnapshot,
            )
        }

    }

}
