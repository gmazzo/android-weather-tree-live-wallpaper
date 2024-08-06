// Source:
// https://www.bigdatacloud.com/free-api/free-reverse-geocode-to-city-api
// Example API:
//  https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=41.4054054054054&longitude=2.23408&localityLanguage=es
package io.github.gmazzo.android.livewallpaper.weather.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingAPI {

    @GET("reverse-geocode-client")
    suspend fun findCity(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") language: String? = null,
    ): City

    @Serializable
    data class City(
        @SerialName("city") val name: String,
        val locality: String,
        @SerialName("principalSubdivision") val region: String,
        val countryCode: String,
        @SerialName("countryName") val country: String,
        val continent: String,
        val continentCode: String,
    )

}
