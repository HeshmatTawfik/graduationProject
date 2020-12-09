package com.heshmat.doctoreta.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

import androidx.core.app.ActivityCompat;

public class LocationHelper {
    public static final int REQUEST_USER_LOCATION=5;
    private  Activity activity;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public LocationHelper(Activity activity){
        this.activity=activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Location l = locationManager.getLastKnownLocation(provider);


                if (l == null) {
                    continue;
                }
                if (bestLocation == null
                        || l.getAccuracy() < bestLocation.getAccuracy()) {

                    bestLocation = l;
                }

            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
    public Location getUserLocation() {
        String provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        Location l = null;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION);
        } else {
            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation != null) {
                l = lastKnownLocation;
            } else {
                assert getLastKnownLocation() != null;
                l = getLastKnownLocation();
            }
        }
        return l;
    }
}
