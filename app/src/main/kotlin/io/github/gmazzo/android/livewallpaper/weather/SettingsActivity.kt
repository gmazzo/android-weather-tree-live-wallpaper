package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.WallpaperManager
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject
    @Named("hasLocationPermission")
    lateinit var hasLocationPermission: Provider<Boolean>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            afterPermissionDialog()
        }

    override fun onResume() {
        super.onResume()

        when {
            hasLocationPermission.get() -> afterPermissionDialog()
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
