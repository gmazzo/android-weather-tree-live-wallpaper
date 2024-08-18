package io.github.gmazzo.android.livewallpaper.weather.settings

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.MediumTest
import com.dropbox.dropshots.Dropshots
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.gmazzo.android.livewallpaper.weather.REFERENCE_DATE
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.discriminatorFor
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.random.Random

@MediumTest
@HiltAndroidTest
class SettingsScreenSnapshotTest {

    private val time = REFERENCE_DATE

    @get:Rule
    val hilt = HiltAndroidRule(this)

    @get:Rule
    val scenario = activityScenarioRule<ComponentActivity>()

    @get:Rule
    val dropshots = Dropshots()

    @JvmField
    @BindValue
    val timeProvider = { time }

    @Inject
    lateinit var random: Random

    @Before
    fun setup() {
        hilt.inject()
    }

    @Test
    fun clear() = testSettings(SceneMode.CLEAR)

    @Test
    fun cloudy() = testSettings(SceneMode.CLOUDY)

    @Test
    fun rain() = testSettings(SceneMode.RAIN)

    @Test
    fun storm() = testSettings(SceneMode.STORM)

    @Test
    fun snow() = testSettings(SceneMode.SNOW)

    @Test
    fun fog() = testSettings(SceneMode.FOG)

    @Test
    fun missingLocation() = testSettings(SceneMode.CLEAR, missingLocation = true)

    private fun testSettings(scene: SceneMode, missingLocation: Boolean = false) {
        scenario.scenario.onActivity {
            it.setContent {
                SettingsScreen(
                    now = time,
                    weather = WeatherType.valueOf(scene),
                    updateLocationEnabled = missingLocation,
                    missingLocationPermission = missingLocation,
                )
            }
        }

        onView(isRoot()).perform(captureToBitmap { bitmap ->
            dropshots.assertSnapshot(
                name = discriminatorFor(
                    "settings", scene, time, random,
                    "missingLocation".takeIf { missingLocation }),
                bitmap = bitmap,
            )
        })
    }

}
