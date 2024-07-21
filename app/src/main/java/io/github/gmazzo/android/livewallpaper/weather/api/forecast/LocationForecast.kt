package io.github.gmazzo.android.livewallpaper.weather.api.forecast

import io.github.gmazzo.android.livewallpaper.weather.api.retrofit
import retrofit2.create

object LocationForecast : LocationForecastAPI by retrofit.create()
