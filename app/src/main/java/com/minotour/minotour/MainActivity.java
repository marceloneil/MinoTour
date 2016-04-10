package com.minotour.minotour;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import se.walkercrou.places.GooglePlaces;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Main Activity
 */
public class MainActivity extends Activity implements LocationListener{

    protected MainApplication app;
    private LocationManager locationManager;
    private String provider;
    private double lat;
    private double lng;

    public void onCreate(Bundle savedInstanceState) throws SecurityException, IllegalArgumentException{

        super.onCreate(savedInstanceState);

        setContentView(R.layout.application_main);

        // Get the application instance
        app = (MainApplication) getApplication();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Get the location manager
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define the criteria how to select the location provider -> use
            // default
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            // Initialize the location fields
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                System.out.print("Location not available");
            }
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() throws SecurityException {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() throws SecurityException {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (location.getLatitude());
        lng = (location.getLongitude());
        System.out.println("Latitude: " + lat + "     Longitude: " + lng);
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 1000, GooglePlaces.MAXIMUM_RESULTS, "Food"));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces();
        get.execute(array);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) throws SecurityException {
        if (requestCode == 1) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Get the location manager
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // Define the criteria how to select the location provider -> use
                // default
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);

                // Initialize the location fields
                if (location != null) {
                    System.out.println("Provider " + provider + " has been selected.");
                    onLocationChanged(location);
                } else {
                    System.out.print("Location not available");
                }
            } else {
                System.out.print("Location not available");
            }
        } else if (requestCode == 2){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        } else if (requestCode == 3){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }
    }
}
