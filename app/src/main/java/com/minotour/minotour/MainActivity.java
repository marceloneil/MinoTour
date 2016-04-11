package com.minotour.minotour;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.minotour.minotour.adapters.SearchAdapter;
import com.minotour.minotour.models.PlaceResult;
import com.minotour.minotour.models.Weather;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Main Activity
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchAdapter.IZoneClick, LocationListener{

    protected MainApplication app;

    // Location Info
    private String provider;

    // Google API info
    private double lat;
    private double lng;

    // Weather API info
    private boolean goodWeather;

    // UI info
    private RecyclerView mLstSearch;
    private SearchAdapter mSearchAdapter;
    private ArrayList<PlaceResult> mData = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Booleans
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void onCreate(Bundle savedInstanceState) throws SecurityException, IllegalArgumentException, NullPointerException {

        super.onCreate(savedInstanceState);

        getUI();
        getLocation(false);
        getNearby();
        getWeather();
    }

    public void getLocation(Boolean Done){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(!Done) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Log.i("Location", "Location Permission Error");
            }
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
                Log.i("Location", "Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                Log.i("Location", "Location not available");
            }
        }
    }

    public void getNearby(){
        String keyword = null;
        String type;
        if(goodWeather){
            type = "park";
        } else {
            type = "cafe";
        }
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat, lng, keyword, type));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces(MainActivity.this);
        get.execute(array);
        onItemsLoadComplete();
    }

    public void getWeather(){
        ArrayList<Object> arrayW = new ArrayList<Object>(Arrays.asList(lat, lng, "Toronto,ON"));
        RetrieveWeather getW = new RetrieveWeather(MainActivity.this);
        getW.execute(arrayW);
    }

    public void getUI(){
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mLstSearch = (RecyclerView) findViewById(R.id.content_main_lstSearch);
        if(mLstSearch != null) {
            mLstSearch.setHasFixedSize(true);
        } else {
            Log.i("mLstSearch", "mLstSearch is null");
        }

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLstSearch.setLayoutManager(mLayoutManager);
        mSearchAdapter = new SearchAdapter(mData, this, this);
        mLstSearch.setAdapter(mSearchAdapter);

        if(toolbar != null) {
            toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            setSupportActionBar(toolbar);
        } else {
            Log.i("toolbar", "toolbar is null");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(drawer != null) {
            drawer.addDrawerListener(toggle);
        } else {
            Log.i("drawer", "drawer is null");
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        } else {
            Log.i("navigationView", "navigationView is null");
        }

        //updates the thing
        mSearchAdapter.notifyDataSetChanged();

        // Get the application instance
        app = (MainApplication) getApplication();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } else {
            Log.i("drawer", "drawer is null");
            super.onBackPressed();
        }
    }

    //refresh
    void refreshItems() {
        // Load items
        // ...

        // Load complete
        getNearby();
    }


    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_recommended) {


        } else if (id == R.id.nav_restaurants) {

        } else if (id == R.id.nav_transportation) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.i("drawer", "drawer is null");
        }
        return true;
    }

    //@Override
    public void zoneClick(PlaceResult model) {


        Intent myIntent = new Intent(MainActivity.this, expand_card.class);
        myIntent.putExtra("query_name", model.name);
        myIntent.putExtra("query_address", model.vicinity);
        if (model.rating != null) {
            myIntent.putExtra("query_rating", model.rating.toString());
        } else {
            myIntent.putExtra("query_rating", "No Rating");
        }
        myIntent.putExtra("query_distance", model.distance.text);

        Integer price_int = model.price_level;
        String price_string;
        if (price_int == null) {

            price_string = "Not Available";
        } else if (price_int == 0) {
            price_string = "Free";
        } else if (price_int == 1) {
            price_string = "Inexpensive";
        } else if (price_int == 2) {
            price_string = "Moderate";
        } else if (price_int == 3) {
            price_string = "Expensive";
        } else {
            price_string = "Very Expensive";
        }


        myIntent.putExtra("query_price", price_string);
        if (model.photoUrl != null) {
            myIntent.putExtra("query_image", model.photoUrl.replaceAll("\\\\u0026", "&").replaceAll("\\\\u003d", "="));
        }
        MainActivity.this.startActivity(myIntent);


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
        System.out.println("Latitude: " + lat + "     Longitude: " + lng);
        getNearby();
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
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation(true);
            } else {
                Log.i("Location", "Location Permission error");
            }
        } else if (requestCode == 2) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(provider, 400, 1, this);
            } else {
                Log.i("Location", "Location Permission error");
            }
        } else if (requestCode == 3) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.removeUpdates(this);
            } else {
                Log.i("Location", "Location Permission error");
            }
        }
    }

    public void OnRetrievedNearbyPlaces(ArrayList<PlaceResult> results) {
        /**
         * results = ArrayList of nearby places, sorted by 'relevence'
         * results.get(i) with 0 <= i < results.size() give each element individually, loop through instead!!!:
         * for(PlaceResult result : results){
         *      result.geometry.location.lat = latitude
         *      result.geometry.location.lng = longitude
         *      result.distance.text = distance in km
         *      result.distance.value = distance value in m (I think)
         *      result.destination_addresses = address <-- maybe wrong, based off of lat/lng instead of place api, use vicinity
         *      result.name = name
         *      result.types = ArrayList<String> with google api types
         *      result.vicinity = Actual address, use instead of destination_addresses
         *      result.rating = Float from 0-5, or null if no rating
         *      result.price_level =
         *          null — No price recorded
         *          0 — Free
         *          1 — Inexpensive
         *          2 — Moderate
         *          3 — Expensive
         *          4 — Very Expensive
         *      result.photoUrl.replaceAll("\\\\u0026","&").replaceAll("\\\\u003d","=") = link to photo
         * }
         */
        mData.clear();
        for (PlaceResult result : results) {
            mData.add(result);
        }
        //this will update the search queries


        mSearchAdapter = new SearchAdapter(mData, this, this);
        mLstSearch.setAdapter(mSearchAdapter);
    }

    public void OnRetrievedWeather(ArrayList<Weather> results) {
        String w = results.get(0).main;
        goodWeather = !(w.equals("Rain") || w.equals("Snow") || w.equals("Extreme") || w.equals("Clouds"));
        Log.i("weather", "" + goodWeather);
    }
}
