package io.github.gmazzo.android.livewallpaper.weather.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditionsUpdateWorker.Companion.disableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditionsUpdateWorker.Companion.enableWeatherConditionsUpdate
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.Preferences
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val workManager: WorkManager,
    val preferences: MutableStateFlow<Preferences>,
    val weatherConditions: StateFlow<WeatherConditions>,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val updateLocationEnabled = MutableStateFlow(false)

    val missingLocationPermission = MutableStateFlow(false)

    init {
        checkLocationPermission()

        viewModelScope.launch {
            preferences.collectLatest {
                val locationOn = it.get<Boolean>(SETTING_LOCATION_ON) ?: false

                updateLocationEnabled.value = locationOn
                if (locationOn) workManager.enableWeatherConditionsUpdate()
                else workManager.disableWeatherConditionsUpdate()
            }
        }
    }

    fun checkLocationPermission() {
        missingLocationPermission.value = updateLocationEnabled.value &&
                !(context.hasLocationPermission && context.hasBackgroundLocationPermission)
    }

}
