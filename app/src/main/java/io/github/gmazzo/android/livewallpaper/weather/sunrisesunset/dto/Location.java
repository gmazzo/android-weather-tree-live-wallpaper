package io.github.gmazzo.android.livewallpaper.weather.sunrisesunset.dto;

import java.math.BigDecimal;

public class Location {
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Location(String latitude, String longitude) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }

    public Location(double latitude, double longitude) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }
}
