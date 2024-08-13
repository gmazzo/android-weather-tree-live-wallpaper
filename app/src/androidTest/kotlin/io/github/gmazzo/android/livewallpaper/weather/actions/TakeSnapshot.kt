package io.github.gmazzo.android.livewallpaper.weather.actions

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import org.hamcrest.Matcher

class TakeSnapshot(
    private val onSnapshot: (bitmap: Bitmap) -> Unit,
) : ViewAction {

    override fun getConstraints(): Matcher<View> =
        isAssignableFrom(WeatherView::class.java)

    override fun getDescription() =
        "Take a snapshot of the view"

    override fun perform(uiController: UiController, view: View) {
        idlingResource.increment()

        (view as WeatherView).takeSnapshot { bitmap ->
            try {
                checkNotNull(bitmap) { "Failed to get snapshot" }
                Log.d("TakeSnapshot", "Snapshot taken: $bitmap")

                onSnapshot(bitmap)

            } finally {
                bitmap?.recycle()
                idlingResource.decrement()
            }
        }
        view.requestRender()
    }

    companion object {
        val idlingResource = CountingIdlingResource("TakeSnapshot")
    }

}
