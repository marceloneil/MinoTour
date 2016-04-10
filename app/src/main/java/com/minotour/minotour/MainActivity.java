package com.minotour.minotour;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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




        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        //provider = "gps";
        //ActivityCompat.requestPermissions(this,
          //      new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            //    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {

            System.out.print("Location not available");
        }



    }

    /* Request updates at startup */
    @Override
    protected void onResume() throws SecurityException {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() throws SecurityException {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (location.getLatitude());
        lng = (location.getLongitude());
        System.out.println("Latitude: " + lat + "     Longitude: " + lng);
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 200, GooglePlaces.MAXIMUM_RESULTS));
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
}
