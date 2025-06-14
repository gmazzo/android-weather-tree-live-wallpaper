package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest
import android.graphics.Bitmap
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.android.tools.screenshot.differ.ImageVerifier
import com.android.tools.screenshot.differ.PixelPerfect
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.gmazzo.android.livewallpaper.weather.actions.AdvanceTime
import io.github.gmazzo.android.livewallpaper.weather.actions.TakeSurfaceSnapshot
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.engine.time.TimeSource
import io.github.gmazzo.android.livewallpaper.weather.settings.SettingsActivity
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.io.FileNotFoundException
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@LargeTest
@HiltAndroidTest
@RunWith(Parameterized::class)
class WeatherViewSnapshotTest(
    private val scene: SceneMode,
    private var time: ZonedDateTime,
) {

    val hilt = HiltAndroidRule(this)

    val activity = activityScenarioRule<SettingsActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        .around(hilt)
        .around(activity)

    @BindValue
    val weather = MutableStateFlow<WeatherType>(WeatherType.valueOf(scene))

    @BindValue
    val timeSource: TimeSource = TimeSource(::time)

    private val outputDir =
        InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let(::File)
            ?: getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)

    @Test
    fun testScene() {
        hilt.inject()

        activity.scenario.onActivity {
            it.findViewById<WeatherView>(R.id.weatherView).renderMode = RENDERMODE_WHEN_DIRTY
        }

        val failures = mutableListOf<Throwable>()

        onView(withId(R.id.weatherView)).perform(
            AdvanceTime(5.seconds) { time += it.toJavaDuration() },
            TakeSurfaceSnapshot { _, bitmap -> assertSnapshot(bitmap, time, failures) },
            AdvanceTime(15.seconds) { time += it.toJavaDuration() },
            TakeSurfaceSnapshot { _, bitmap -> assertSnapshot(bitmap, time, failures) },
            AdvanceTime(10.seconds) { time += it.toJavaDuration() },
        )

        if (failures.isNotEmpty()) {
            throw failures.reduce { a, b -> a.addSuppressed(b); a }
        }
    }

    private fun assertSnapshot(
        bitmap: Bitmap?,
        time: ZonedDateTime,
        failures: MutableList<Throwable>,
    ) {
        assertEquals(scene, weather.value.scene)
        assertNotNull("Failed to get snapshot", bitmap)

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val referenceName = "${discriminatorFor("scene", scene, time)}.png"
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

        val actualFile = outputDir.resolve("screenshots").resolve(referenceName).apply {
            parentFile?.mkdirs()
            outputStream().use { bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }

        try {
            ImageVerifier(PixelPerfect(imageDiffThreshold = 0.01f)).verify(
                actualFile.absolutePath,
                referenceFile.absolutePath,
                outputDir.resolve("screenshots-diffs").resolve(referenceName).absolutePath,
            )

        } catch (ex: Throwable) {
            failures.add(AssertionError("Scene $scene does not match", ex))
        }
    }

    companion object {
        private val TEST_HOURS = longArrayOf(0, 5, 6, 7, 12, 17, 18, 19)

        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = SceneMode.entries.flatMap { scene ->
            TEST_HOURS.map { arrayOf(scene, REFERENCE_DATE.plusHours(it)) }
        }

    }

}
