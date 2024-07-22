package io.github.gmazzo.android.livewallpaper.weather.forecast

import io.github.gmazzo.android.livewallpaper.weather.api.HttpModule
import org.junit.Assert.assertNotNull
import org.junit.Test

class LocationForecastAPITest {

    private val api by lazy { HttpModule.provideLocationForecast(
        HttpModule.provideRetrofit(
            HttpModule.provideOkHttpClient(),
            HttpModule.provideJson()
        )
    ) }

    @Test
    fun `api call returns expected forecast`() {
        val response = api.getForecast(59.93f, 10.72f, 90).execute().body()!!
        val data = response.properties.timeSeries.first().data

        assertNotNull(data.nextHour.summary.symbolCode)
        assertNotNull(data.next6Hours.summary.symbolCode)
        assertNotNull(data.next12Hours.summary.symbolCode)
    }

}
