package io.github.gmazzo.android.livewallpaper.weather.forecast

import io.github.gmazzo.android.livewallpaper.weather.retrofit
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.create

class LocationForecastAPITest {

    private val api: LocationForecastAPI = retrofit.create()

    @Test
    fun `api call returns expected forecast`() {
        val response = api.getForecast(59.93, 10.72, 90).execute().body()!!
        val data = response.properties.timeSeries.first().data

        assertNotNull(data.nextHour.summary.symbolCode)
        assertNotNull(data.next6Hours.summary.symbolCode)
        assertNotNull(data.next12Hours.summary.symbolCode)
    }

}
