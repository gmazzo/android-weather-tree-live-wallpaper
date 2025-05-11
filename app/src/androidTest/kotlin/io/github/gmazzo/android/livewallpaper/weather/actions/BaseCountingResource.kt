package io.github.gmazzo.android.livewallpaper.weather.actions

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

abstract class BaseCountingResource(
    val counter: CountingIdlingResource,
) : IdlingResource by counter {

    constructor(name: String) : this(CountingIdlingResource(name))

}
