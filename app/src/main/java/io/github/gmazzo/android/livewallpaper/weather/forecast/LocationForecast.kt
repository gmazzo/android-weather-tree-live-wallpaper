package io.github.gmazzo.android.livewallpaper.weather.forecast

import io.github.gmazzo.android.livewallpaper.weather.retrofit
import retrofit2.create

object LocationForecast : LocationForecastAPI by retrofit.create()
