package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.WallpaperManager
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.LocationProvider.hasLocationPermission

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            afterPermissionDialog()
        }

    override fun onResume() {
        super.onResume()

        when {
            hasLocationPermission -> afterPermissionDialog()
            else -> requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun afterPermissionDialog() {
        finish()

        if (BuildConfig.DEBUG) {
            startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, Intent(this, WallpaperService::class.java).component))
        }
    }

}
