package io.github.gmazzo.android.livewallpaper.weather.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.WeatherView
import io.github.gmazzo.android.livewallpaper.weather.WeatherWallpaperService
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    @Inject
    internal lateinit var viewFactory: WeatherView.Factory

    private val weatherView by lazy { viewFactory.create(this, TAG, demoMode = true) }

    internal val viewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        callback = ::onRequestPermissionResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsScreen(
                now = viewModel.time.time.collectAsState().value,
                timeSpeed = viewModel.timeSpeed.collectAsState().value,
                location = viewModel.location.collectAsState().value,
                updateLocationEnabled = viewModel.updateLocationEnabled.collectAsState().value,
                weather = viewModel.weather.collectAsState().value,
                updateLocationEnabledChange = ::onUpdateLocationEnabledChange,
                onSpeedSelected = viewModel.timeSpeed::value::set,
                onSceneSelected = viewModel::updateSelectedScene,
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

        } else {
            viewModel.updateLocationEnabled.value = enabled
        }
    }

    private fun onRequestPermissionResult(granted: Boolean) {
        viewModel.updateLocationEnabled.value = granted
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

    companion object {
        private const val TAG = "SettingsActivity"
    }

}
