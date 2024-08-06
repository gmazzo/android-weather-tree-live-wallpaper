package io.github.gmazzo.android.livewallpaper.weather.api

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
internal object HttpModule {

    @Provides
    @Reusable
    fun json(): Json =
        Json { ignoreUnknownKeys = true }

    @Provides
    @Reusable
    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(UserAgentInterceptor)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Provides
    @Reusable
    fun retrofit(client: OkHttpClient, json: Json): Retrofit.Builder = Retrofit.Builder()
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))

    @Provides
    @Reusable
    fun locationForecast(retrofit: Retrofit.Builder): LocationForecastAPI =
        retrofit.baseUrl(BuildConfig.FORECAST_ENDPOINT).build().create()

    @Provides
    @Reusable
    fun reverseGeocoding(retrofit: Retrofit.Builder): ReverseGeocodingAPI =
        retrofit.baseUrl(BuildConfig.REVERSE_GEOCODING_ENDPOINT).build().create()

}
