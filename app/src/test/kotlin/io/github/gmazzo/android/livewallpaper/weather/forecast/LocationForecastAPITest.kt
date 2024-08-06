package io.github.gmazzo.android.livewallpaper.weather.forecast

import io.github.gmazzo.android.livewallpaper.weather.api.HttpModule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Test

class LocationForecastAPITest {

    private val api by lazy { HttpModule.locationForecast(
        HttpModule.retrofit(
            HttpModule.okHttpClient(),
            HttpModule.json()
        )
    ) }

    @Test
    fun `api call returns expected forecast`() = runTest {
        val response = api.getForecast(41.3825, 2.176944, 0)
        val data = response.properties.timeSeries.first().data

        assertNotNull(data.nextHour.summary.symbolCode)
        assertNotNull(data.next6Hours.summary.symbolCode)
        assertNotNull(data.next12Hours.summary.symbolCode)
    }

}
