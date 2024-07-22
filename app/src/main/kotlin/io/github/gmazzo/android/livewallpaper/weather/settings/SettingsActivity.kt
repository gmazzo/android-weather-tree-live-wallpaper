package io.github.gmazzo.android.livewallpaper.weather.settings

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import io.github.gmazzo.android.livewallpaper.weather.WallpaperService
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.hasBackgroundLocationPermission
import io.github.gmazzo.android.livewallpaper.weather.hasLocationPermission
import kotlinx.coroutines.flow.MutableStateFlow
import me.zhanghai.compose.preference.Preferences
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission(), ::checkPermissions)

    @Inject
    lateinit var preferences: MutableStateFlow<Preferences>

    @Inject
    lateinit var weatherConditions: MutableStateFlow<WeatherConditions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { SettingsScreen(preferences, weatherConditions) }
    }

    override fun onResume() {
        super.onResume()

        checkPermissions(null)
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissions(granted: Boolean?) {
        when {
            granted == false -> {}
            !hasLocationPermission -> requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            !hasBackgroundLocationPermission -> requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun openWallpaperChooser() {
        finish()

        if (BuildConfig.DEBUG) {
            startActivity(
                Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                    .putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        Intent(this, WallpaperService::class.java).component
                    )
            )
        }
    }

}
