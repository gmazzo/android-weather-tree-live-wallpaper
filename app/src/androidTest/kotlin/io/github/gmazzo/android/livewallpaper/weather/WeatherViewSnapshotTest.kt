package io.github.gmazzo.android.livewallpaper.weather

import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.dropbox.dropshots.Dropshots
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.gmazzo.android.livewallpaper.weather.actions.AdvanceTime
import io.github.gmazzo.android.livewallpaper.weather.actions.TakeSnapshot
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@HiltAndroidTest
@UninstallModules(WeatherModule.Nondeterministic::class)
@RunWith(Parameterized::class)
class WeatherViewSnapshotTest(
    private val scene: SceneMode,
    private var time: ZonedDateTime,
) {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val dropshots = Dropshots()

    @JvmField
    @BindValue
    val random: Random = DeterministicRandom.reset()

    @JvmField
    @BindValue
    val timeProvider = { time }

    @Inject
    lateinit var weather: MutableStateFlow<WeatherType>

    @Inject
    lateinit var viewFactory: WeatherView.Factory

    @Before
    fun setUp() {
        check(AdvanceTime.idlingResource.isIdleNow)
        check(TakeSnapshot.idlingResource.isIdleNow)

        hiltRule.inject()
        weather.value = WeatherType.entries.first { it.scene == scene }
    }

    @Test
    fun testScene(): Unit = launchActivity<ComponentActivity>().use { scenario ->
        scenario.onActivity { activity ->
            val view = viewFactory.create(activity, "WeatherViewSnapshotTest", false)
            view.id = R.id.weatherView
            view.renderMode = RENDERMODE_WHEN_DIRTY

            activity.setContentView(view)
        }
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.weatherView)).perform(
            AdvanceTime(5.seconds) { time += it.toJavaDuration() },
            TakeSnapshot { bitmap ->
                val scenePart = scene.name.lowercase()
                val timePart = time.format(DateTimeFormatter.ofPattern("YYYYMMdd'T'HHmmss"))
                val randomPart = random.nextInt(10)

                dropshots.assertSnapshot(
                    name = "scene_${scenePart}_${timePart}_$randomPart",
                    bitmap = bitmap,
                )
            })
        onIdle()
    }

    companion object {

        private val date = ZonedDateTime.of(2024, 8, 1, 0, 0, 0, 0, ZoneId.systemDefault())

        private val times = listOf(
            date,
            date.plusHours(5),
            date.plusHours(6),
            date.plusHours(7),
            date.plusHours(12),
            date.plusHours(17),
            date.plusHours(18),
            date.plusHours(19),
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{0} at {1}")
        fun parameters() = SceneMode.entries.flatMap { scene ->
            times.asSequence().map { time -> arrayOf(scene, time) }
        }

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            IdlingRegistry.getInstance().register(
                AdvanceTime.idlingResource,
                TakeSnapshot.idlingResource,
            )
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            IdlingRegistry.getInstance().unregister(
                AdvanceTime.idlingResource,
                TakeSnapshot.idlingResource,
            )
        }

    }

}
