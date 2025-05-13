package io.github.gmazzo.android.livewallpaper.weather.actions

import android.util.Log
import android.view.View
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import io.github.gmazzo.android.livewallpaper.weather.WeatherView.SnapshotCallback
import org.hamcrest.Matcher
import java.util.concurrent.ConcurrentHashMap

class TakeSurfaceSnapshot(
    private val onSnapshot: SnapshotCallback,
) : ViewAction {

    override fun getConstraints(): Matcher<View> =
        isAssignableFrom(WeatherView::class.java)

    override fun getDescription() =
        "Take a snapshot of the view"

    override fun perform(uiController: UiController, view: View) {
        pending.add(onSnapshot)

        (view as WeatherView).takeSnapshot { result, bitmap ->
            try {
                Log.d("TakeSnapshot", "Snapshot taken: result=$result, $bitmap")

                onSnapshot.onSnapshot(result, bitmap)

            } finally {
                bitmap?.recycle()
                pending.remove(onSnapshot)
                checkIdle()
            }
        }
        view.requestRender()
    }

    companion object : IdlingResource {
        private val pending = ConcurrentHashMap.newKeySet<SnapshotCallback>()
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName() = "TakeSnapshot"

        override fun isIdleNow() = pending.isEmpty()

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
            checkIdle()
        }

        private fun checkIdle() {
            if (isIdleNow()) callback?.onTransitionToIdle()
        }

        fun reset() {
            pending.clear()
            checkIdle()
        }

    }

}
