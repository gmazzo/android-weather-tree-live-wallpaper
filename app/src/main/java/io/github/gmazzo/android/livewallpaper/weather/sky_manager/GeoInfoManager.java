package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import static io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager.INVALID_COORD;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class GeoInfoManager {
    private static final String TAG = "GeoInfoManager";

    public static double[] getLastGPS(Context context) {
        double[] axis = new double[]{INVALID_COORD, INVALID_COORD};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Missing " + Manifest.permission.ACCESS_COARSE_LOCATION + " to access location");
            return axis;
        }
        LocationManager localLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        for (String provider : localLocationManager.getAllProviders()) {
            Location location = localLocationManager.getLastKnownLocation(provider);
            if (location != null) {
                axis[0] = location.getLatitude();
                axis[1] = location.getLongitude();
                Log.i(TAG, "Latitude:" + axis[0] + " Longitude" + axis[1]);
                break;
            }
        }
        return axis;
    }

}
