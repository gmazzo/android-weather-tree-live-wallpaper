package io.github.gmazzo.android.livewallpaper.weather.actions

import androidx.test.espresso.UiController
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class AdvanceTime(
    private val amount: Duration,
    private val step: Duration = 100.milliseconds,
    private val onTimeOffset: (Duration) -> Unit,
) : AwaitRenderer() {

    override fun getDescription() =
        "Advances the time by ${amount}ms"

    override fun perform(uiController: UiController, view: WeatherView) {
        advanceTime(view, amount)
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
