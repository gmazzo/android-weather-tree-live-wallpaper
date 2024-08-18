package io.github.gmazzo.android.livewallpaper.weather.actions

import android.util.Log
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.idling.CountingIdlingResource
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
        idlingResource.increment()
        advanceTime(view as WeatherView, amount)
    }

    private fun advanceTime(view: WeatherView, amount: Duration) {
        Log.d("AdvanceTime", "request: amount=$amount")
        if (amount > Duration.ZERO) {
            view.renderer.postRender {
                onTimeOffset(step)
                Log.d("AdvanceTime", "updatedTime")

                advanceTime(view, amount - step)
            }
            view.requestRender()

        } else {
            idlingResource.decrement()
            Log.d("AdvanceTime", "finished")
        }
    }

    companion object {
        val idlingResource = CountingIdlingResource("AdvanceTime")
    }

}
