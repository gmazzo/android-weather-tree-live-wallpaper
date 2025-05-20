package io.github.gmazzo.android.livewallpaper.weather.actions

import android.util.Log
import androidx.test.espresso.UiController
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import io.github.gmazzo.android.livewallpaper.weather.WeatherView.SnapshotCallback

class TakeSurfaceSnapshot(
    private val onSnapshot: SnapshotCallback,
) : AwaitRenderer() {

    override fun getDescription() =
        "Take a snapshot of the view"

    override fun perform(uiController: UiController, view: WeatherView) {
        view.takeSnapshot { result, bitmap ->
            try {
                Log.d("TakeSnapshot", "Snapshot taken: result=$result, $bitmap")

                onSnapshot.onSnapshot(result, bitmap)

            } finally {
                bitmap?.recycle()
            }
        }
        view.requestRender()
    }

}
