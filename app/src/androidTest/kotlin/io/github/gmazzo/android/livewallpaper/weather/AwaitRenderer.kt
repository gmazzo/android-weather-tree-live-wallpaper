package io.github.gmazzo.android.livewallpaper.weather

import androidx.test.espresso.IdlingResource

object AwaitRenderer : IdlingResource, Runnable {

   internal var view: WeatherView? = null
       set(value) {
           field?.renderer?.onAfterRender = null
           field = value
           value?.renderer?.onAfterRender = this
           run()
       }

    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName() = "WeatherRenderer"

    override fun isIdleNow() =
        view?.renderer?.hasPendingActions != true

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }

    override fun run() {
        if (isIdleNow) callback?.onTransitionToIdle() else view?.requestRender()
    }

}
