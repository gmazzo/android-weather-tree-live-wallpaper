package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission(), ::checkPermissions)

    override fun onResume() {
        super.onResume()

        checkPermissions(null)

    }

    @SuppressLint("InlinedApi")
    private fun checkPermissions(granted: Boolean?) {
        when {
            granted == false -> afterPermissionDialog()
            !hasLocationPermission -> requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            !hasBackgroundLocationPermission -> requestPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
            else -> afterPermissionDialog()
        }
    }

    private fun afterPermissionDialog() {
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
