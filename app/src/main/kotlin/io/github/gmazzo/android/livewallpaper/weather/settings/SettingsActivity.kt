package io.github.gmazzo.android.livewallpaper.weather.settings

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import io.github.gmazzo.android.livewallpaper.weather.WeatherWallpaperService
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    @Inject
    internal lateinit var weatherViewFactory: WeatherView.Factory

    private val viewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission(), ::checkPermissions)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen(
                updateLocationEnabled = viewModel.updateLocationEnabled.collectAsState().value,
                sunPosition = viewModel.sunPosition.collectAsState().value,
                weatherConditions = viewModel.weatherConditions.collectAsState().value,
                missingLocationPermission = viewModel.missingLocationPermission.collectAsState().value,
                updateLocationEnabledChange = viewModel::updateLocationEnabled,
                onSceneSelected = viewModel::updateSelectedScene,
                onRequestLocationPermission = { checkPermissions(null) },
                onSetAsWallpaper = ::openWallpaperChooser,
                onNavigateBack = ::finish,
            ) {
                AndroidView(factory = { context ->
                    weatherViewFactory.create(context).also { it.isDemoMode = true }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResume()
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissions(granted: Boolean?) {
        when {
            granted == false -> if (hasLocationPermission && !hasBackgroundLocationPermission) {
                startActivityForResult(Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                ), 0)
            }
            !hasLocationPermission -> requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            !hasBackgroundLocationPermission -> requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun openWallpaperChooser() {
        startActivity(
            Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, WeatherWallpaperService::class.java)
            )
        )
        finish()
    }

}
