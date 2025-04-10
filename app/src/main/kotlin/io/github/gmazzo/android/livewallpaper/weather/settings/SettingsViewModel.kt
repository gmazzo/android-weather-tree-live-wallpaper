package io.github.gmazzo.android.livewallpaper.weather.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.gmazzo.android.livewallpaper.weather.Location
import io.github.gmazzo.android.livewallpaper.weather.LocationManager
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.WeatherUpdateWorker.Companion.disableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.WeatherUpdateWorker.Companion.enableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.api.ReverseGeocodingAPI
import io.github.gmazzo.android.livewallpaper.weather.api.ReverseGeocodingAPI.Companion.findCity
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: DataStore<Preferences>,
    val time: GlobalTime.Fast,
    @Named("fastTimeSpeed") val timeSpeed: MutableStateFlow<Float>,
    @Named("homeOffset") private val homeOffset: MutableStateFlow<Float>,
    val weather: MutableStateFlow<WeatherType>,
    val location: StateFlow<Location?>,
    private val workManager: WorkManager,
    private val locationManager: LocationManager,
    private val reverseGeocodingAPI: ReverseGeocodingAPI,
) : ViewModel(), DefaultLifecycleObserver {

    private val settingLocationOn = booleanPreferencesKey("location_on")

    val updateLocationEnabled = MutableStateFlow(false)

    init {
        updateLocationEnabled()
        updateUIFromLocationEnabled()
        retrieveCityNameFromLocation()
    }

    private fun updateLocationEnabled() = viewModelScope.launch {
        preferences.data.collectLatest {
            val locationOn = it[settingLocationOn] == true

            updateLocationEnabled.value = locationOn && context.hasLocationPermission
        }
    }

    private fun updateUIFromLocationEnabled() = viewModelScope.launch {
        updateLocationEnabled.collectLatest { enabled ->
            preferences.edit { it[settingLocationOn] = enabled }
            if (enabled) workManager.enableWeatherConditionsUpdate()
            else workManager.disableWeatherConditionsUpdate()
        }
    }

    private fun retrieveCityNameFromLocation() = viewModelScope.launch(Dispatchers.IO) {
        location.collectLatest {
            if (it != null && it.city == null) {
                val city = runCatching {
                    reverseGeocodingAPI.findCity(
                        it.latitude,
                        it.longitude,
                        Locale.getDefault().language
                    )
                }.onFailure { it.printStackTrace() }.getOrNull() ?: return@collectLatest

                val cityName =
                    sequenceOf(city.name, city.locality, city.region, city.country).filter(
                        String::isNotBlank
                    ).firstOrNull()

                locationManager.flow.value = it.copy(city = cityName)
            }
        }
    }

    fun updateSelectedScene(scene: SceneMode) {
        weather.value = WeatherType.valueOf(scene)
    }

    fun updateHomeOffset(forward: Boolean) = homeOffset.update {
        (if (forward) it + .5f else it - .5f).coerceIn(0f, 1f)
    }

}
