package com.minotour.minotour;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;

import com.minotour.minotour.adapters.SearchAdapter;
import com.minotour.minotour.models.TestModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Main Activity
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SearchAdapter.IZoneClick,
        LocationListener{

    protected MainApplication app;
    private LocationManager locationManager;
    private String provider;
    private double lat;
    private double lng;
    private RecyclerView mLstSearch;
    private SearchAdapter mSearchAdapter;
    private ArrayList<TestModel> mData = new ArrayList<TestModel>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public void onCreate(Bundle savedInstanceState) throws SecurityException, IllegalArgumentException{

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);



        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                // Refresh items
                refreshItems();
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mLstSearch = (RecyclerView) findViewById(R.id.content_main_lstSearch);

        mLstSearch.setHasFixedSize(true);

        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLstSearch.setLayoutManager(mLayoutManager);

        mSearchAdapter = new SearchAdapter(mData, this);
        mLstSearch.setAdapter(mSearchAdapter);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //updates the thing
        mSearchAdapter.notifyDataSetChanged();

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
        System.out.println("Latitude: " + lat + "     Longitude: " + lng);
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 1000, 1, "Food"));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces();
        get.execute(array);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //refresh
    void refreshItems() {
        // Load items
        // ...

        // Load complete
        System.out.println("Latitude: " + lat + "     Longitude: " + lng);
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 1000, 1, "Food"));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces();
        get.execute(array);
        onItemsLoadComplete();
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /*public void appendQuery(String title, String distance, String address) {

    }*/


    @Override
    public void zoneClick(TestModel model) {

        //this will update the search queries
        mData.clear();
        mData.add(new TestModel());
        mSearchAdapter = new SearchAdapter(mData, this);
        mLstSearch.setAdapter(mSearchAdapter);


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
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 1000, 1, "Food"));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces();
        get.execute(array);
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