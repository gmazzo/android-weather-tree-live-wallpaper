package io.github.gmazzo.android.livewallpaper.weather.actions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class AwaitRenderer(private val timeout: Duration = 5.seconds) : ViewAction {

    abstract fun perform(uiController: UiController, view: WeatherView)

    override fun getConstraints(): Matcher<View> =
        isAssignableFrom(WeatherView::class.java)

    final override fun perform(uiController: UiController, view: View) {
        val until = System.currentTimeMillis() + timeout.inWholeMilliseconds
        while ((view as WeatherView).renderer.hasPendingActions) {
            if (System.currentTimeMillis() > until) {
                throw TimeoutException("Timeout waiting for WeatherView to be idle")
            }
            view.requestRender()
            uiController.loopMainThreadForAtLeast(50)
        }

        perform(uiController, view)
    }

}
