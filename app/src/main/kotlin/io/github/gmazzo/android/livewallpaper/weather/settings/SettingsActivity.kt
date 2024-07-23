package io.github.gmazzo.android.livewallpaper.weather.settings

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.WallpaperService
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission(), ::checkPermissions)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen(
                modal = true,
                updateLocationEnabled = viewModel.updateLocationEnabled.collectAsState().value,
                weatherConditions = viewModel.weatherConditions.collectAsState().value,
                missingLocationPermission = viewModel.missingLocationPermission.collectAsState().value,
                updateLocationEnabledChange = viewModel::updateLocationEnabled,
                onRequestLocationPermission = { checkPermissions(null) },
                onSetAsWallpaper = ::openWallpaperChooser,
                onNavigateBack = ::finish
            )
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResume()
    }

    override fun onPostResume() {
        super.onPostResume()

        lifecycleScope.launch {
            viewModel.updateLocationEnabled.drop(1).collectLatest { enabled ->
                if (enabled) checkPermissions(null)
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissions(granted: Boolean?) {
        when {
            granted == false -> {}
            !hasLocationPermission -> requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            !hasBackgroundLocationPermission -> requestPermissionLauncher.launch(
                ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun openWallpaperChooser() = startActivity(
        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(this, WallpaperService::class.java)
        )
    )

}
