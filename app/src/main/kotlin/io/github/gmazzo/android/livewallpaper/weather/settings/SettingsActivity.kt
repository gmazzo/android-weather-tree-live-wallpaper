@file:SuppressLint("InlinedApi")

package io.github.gmazzo.android.livewallpaper.weather.settings

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import io.github.gmazzo.android.livewallpaper.weather.WeatherWallpaperService
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    @Inject
    internal lateinit var viewFactory: WeatherView.Factory

    private val weatherView by lazy {
        viewFactory.create(this, TAG, demoMode = true).also { it.id = R.id.weatherView }
    }

    internal val viewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        callback = ::onRequestPermissionResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen(
                now = viewModel.clock.collectAsState().value.time,
                timeSpeed = viewModel.timeSpeed.collectAsState().value,
                location = viewModel.location.collectAsState().value,
                updateLocationEnabled = viewModel.updateLocationEnabled.collectAsState().value,
                weather = viewModel.weather.collectAsState().value,
                forecastWeather = viewModel.forecastWeather.collectAsState().value,
                missingLocationPermission = !hasBackgroundLocationPermission,
                updateLocationEnabledChange = ::onUpdateLocationEnabledChange,
                onSpeedSelected = viewModel.timeSpeed::value::set,
                onSceneSelected = viewModel::updateSelectedScene,
                onRequestBackgroundLocationPermission = ::requestBackgroundLocationPermission,
                onSetAsWallpaper = ::openWallpaperChooser,
                onNavigateBack = ::finish,
                onDragGesture = viewModel::updateHomeOffset,
            ) { AndroidView(factory = { weatherView }) }
        }
    }

    override fun onStart() {
        super.onStart()

        weatherView.onResume()
    }

    override fun onStop() {
        super.onStop()

        weatherView.onPause()
    }

    private fun onUpdateLocationEnabledChange(enabled: Boolean) {
        if (enabled && !hasLocationPermission) {
            requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)

        } else if (enabled && !hasBackgroundLocationPermission) {
            requestBackgroundLocationPermission()
        }

        viewModel.updateLocationEnabled.value = enabled && hasLocationPermission
    }

    private fun onRequestPermissionResult(granted: Boolean) {
        viewModel.updateLocationEnabled.value = hasLocationPermission
    }

    private fun requestBackgroundLocationPermission() {
        if (shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION)) {
            requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)

        } else {
            openAppSettings()
        }
    }

    /**
     * When a few background permissions requests are made but the user cancels it,
     * the SO no longer shows the dialog and denies the request automatically
     * We open the settings in case
     */
    private fun openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    private fun openWallpaperChooser() {
        try {
            startActivity(
                Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this, WeatherWallpaperService::class.java)
                )
            )
            finish()

        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()

            Toast.makeText(this, R.string.settings_wallpaper_chooser_not_found, Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }

}
