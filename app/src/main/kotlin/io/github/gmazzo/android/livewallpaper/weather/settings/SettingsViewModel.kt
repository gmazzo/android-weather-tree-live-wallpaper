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
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.WeatherUpdateWorker.Companion.disableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.WeatherUpdateWorker.Companion.enableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.api.ReverseGeocodingAPI
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    val weather: MutableStateFlow<WeatherType>,
    private val workManager: WorkManager,
    val location: MutableStateFlow<Location?>,
    private val reverseGeocodingAPI: ReverseGeocodingAPI,
) : ViewModel(), DefaultLifecycleObserver {

    private val settingLocationOn = booleanPreferencesKey("location_on")

    val updateLocationEnabled = MutableStateFlow(false)

    val missingLocationPermission = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            preferences.data.collectLatest {
                val locationOn = it[settingLocationOn] ?: false

                updateLocationEnabled.value = locationOn
            }
        }
        viewModelScope.launch {
            updateLocationEnabled.collectLatest { enabled ->
                computeMissingLocationPermission(enabled)
                enableWeatherConditionsUpdate(enabled)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            location.collectLatest {
                if (it != null) {
                    val city = reverseGeocodingAPI
                        .findCity(it.latitude, it.longitude, Locale.getDefault().language)

                    val cityName = sequenceOf(city.name, city.locality, city.region, city.country)
                        .filter(String::isNotBlank)
                        .firstOrNull()

                    location.value = it.copy(city = cityName)
                }
            }
        }
    }

    fun onResume() {
        computeMissingLocationPermission(updateLocationEnabled.value)
    }

    private fun computeMissingLocationPermission(locationOn: Boolean) {
        missingLocationPermission.value = locationOn &&
                !(context.hasLocationPermission && context.hasBackgroundLocationPermission)
    }

    fun enableWeatherConditionsUpdate(enabled: Boolean = updateLocationEnabled.value) {
        if (enabled) workManager.enableWeatherConditionsUpdate()
        else workManager.disableWeatherConditionsUpdate()
    }

    fun updateLocationEnabled(enabled: Boolean) = viewModelScope.launch {
        preferences.edit { it[settingLocationOn] = enabled }
    }

    fun updateSelectedScene(scene: SceneMode) {
        weather.value = WeatherType.entries.first { it.scene == scene }
    }

}
