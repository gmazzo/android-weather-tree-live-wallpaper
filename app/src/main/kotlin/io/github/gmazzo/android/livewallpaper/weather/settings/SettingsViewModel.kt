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
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditionsUpdateWorker.Companion.disableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditionsUpdateWorker.Companion.enableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: DataStore<Preferences>,
    val weatherConditions: MutableStateFlow<WeatherConditions>,
    private val workManager: WorkManager,
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

                if (enabled) workManager.enableWeatherConditionsUpdate()
                else workManager.disableWeatherConditionsUpdate()
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

    fun updateLocationEnabled(enabled: Boolean) = viewModelScope.launch {
        preferences.edit { it[settingLocationOn] = enabled }
    }

    fun updateSelectedScene(scene: SceneMode) = weatherConditions.update { prefs ->
        prefs.copy(weatherType = WeatherType.entries.first { it.scene == scene })
    }

}
