package io.github.gmazzo.android.livewallpaper.weather.actions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import org.hamcrest.Matcher
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class AdvanceTime(
    private val amount: Duration,
    private val step: Duration = 100.milliseconds,
    private val onTimeOffset: (Duration) -> Unit,
) : ViewAction {

    override fun getConstraints(): Matcher<View> =
        isAssignableFrom(WeatherView::class.java)

    override fun getDescription() =
        "Advances the time by ${amount}ms"

    override fun perform(uiController: UiController, view: View) {
        advanceTime(view as WeatherView, amount)
    }

    private fun advanceTime(view: WeatherView, amount: Duration) {
        view.renderer.postRender {
            if (amount > Duration.ZERO) {
                onTimeOffset(step)
                advanceTime(view, amount - step)
            }
        }
        view.requestRender()
    }

}
