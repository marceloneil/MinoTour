package com.minotour.minotour;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestGeneralCallback;
import com.flybits.core.api.interfaces.IRequestPaginationCallback;
import com.flybits.core.api.models.Pagination;
import com.flybits.core.api.models.Zone;
import com.flybits.core.api.models.ZoneMoment;
import com.flybits.core.api.models.v1_5.internal.Result;
import com.flybits.core.api.utils.http.GetRequest;
import com.google.gson.Gson;
import com.minotour.minotour.adapters.SearchAdapter;
import com.minotour.minotour.models.KeyValuePayload;
import com.minotour.minotour.models.PlaceResult;
import com.minotour.minotour.models.Weather;

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
    private ArrayList<PlaceResult> mData = new ArrayList<PlaceResult>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<KeyValuePayload> zMoments;
    private String keyword = "Tourism";

    public void onCreate(Bundle savedInstanceState) throws SecurityException, IllegalArgumentException{

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        zMoments = new ArrayList<>();

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

        /*mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());
        mData.add(new TestModel());*/

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLstSearch.setLayoutManager(mLayoutManager);

        mSearchAdapter = new SearchAdapter(mData, this, this);
        mLstSearch.setAdapter(mSearchAdapter);

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 100000, keyword));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces(MainActivity.this);
        get.execute(array);

        ArrayList<Object> arrayW = new ArrayList<Object>(Arrays.asList(lat,lng,"Toronto,ON"));
        RetrieveWeather getW = new RetrieveWeather(MainActivity.this);
        getW.execute(arrayW);

        getZone();
    }

    public void getZone(){
        Log.i("MainActivity", "Getting Zone");
        String zoneId = "F9E7A523-AB28-4C75-9CD3-878EFF5B9C75";
        Flybits.include(MainActivity.this).getZone(zoneId, new IRequestCallback<Zone>() {
            @Override
            public void onSuccess(Zone zone) {
                Log.i("MainActivity", "Successfully found zone: " + zone.getName());

                getMoments(zone);
            }

            @Override
            public void onException(Exception e) {
                Log.e("MainActivity", "Failed to get Zone: " + e.toString());
            }

            @Override
            public void onFailed(String s) {
                Log.e("MainActivity", "Failed to get Zone: " + s);

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void getMoments(Zone zone){
        Log.i("MainActivity", "Getting Moments");
        Flybits.include(MainActivity.this).getZoneMomentsForZone(zone.id, new IRequestPaginationCallback<ArrayList<ZoneMoment>>() {
            @Override
            public void onSuccess(ArrayList<ZoneMoment> zoneMoments, Pagination pagination) {
                Log.i("MainActivity", "Successfully received Moments");
                /*if(zoneMoments != null && zoneMoments.size() > 0) {
                    for(ZoneMoment zz: zoneMoments) {
                        authenticateMoment(zz);
                    }
                }*/
                Log.i("MainActivity", "zonemoments size: " + zoneMoments.size());
                authenticateMoment(zoneMoments.get(0));
                //authenticateMoment(zoneMoments.get(1));

                /*for(ZoneMoment zm : zoneMoments){
                    authenticateMoment(zm);
                }*/
            }

            @Override
            public void onException(Exception e) {
                Log.e("MainActivity", "Failed to get Moment: " + e.toString());
            }

            @Override
            public void onFailed(String s) {
                Log.e("MainActivity", "Failed to get Moment: " + s);
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void authenticateMoment(final ZoneMoment moment){
        Log.i("MainActivity", "Authenticating Moment");
        Flybits.include(MainActivity.this).authenticateZoneMomentUsingJWT(moment, new IRequestGeneralCallback() {
            @Override
            public void onSuccess() {
                Log.i("MainActivity", "Successfuly authenticated Moment");
                getMomentData(moment);
            }

            @Override
            public void onException(Exception e) {
                Log.e("MainActivity", "Failed to authenitcate moment: " + e.toString());
            }

            @Override
            public void onFailed(String s) {
                Log.e("MainActivity", "Failed to authenitcate moment: " + s);

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void getMomentData(ZoneMoment moment){
        Log.i("MainActivity", "Getting Moment Data");

        new GetMomentDataTask().execute(moment);

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
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng, 100000, keyword));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces(MainActivity.this);
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

    //@Override
    public void zoneClick(PlaceResult model) {


        Intent myIntent = new Intent(MainActivity.this, expand_card.class);
        myIntent.putExtra("query_name", model.name);
        myIntent.putExtra("query_address", model.vicinity);
        myIntent.putExtra("query_rating", Double.toString(model.rating));
        myIntent.putExtra("query_distance", model.distance.text);

        Integer price_int = model.price_level;
        String price_string;
        if(price_int == null) {

            price_string = "Not Available";
        }else if(price_int == 0) {
            price_string = "Free";
        } else if(price_int == 1){
            price_string = "Inexpensive";
        } else if (price_int == 2){
            price_string = "Moderate";
        } else if (price_int == 3){
            price_string = "Expensive";
        } else{
            price_string = "Very Expensive";
        }



        myIntent.putExtra("query_price", price_string);
        if(model.photoUrl != null) {
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
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(lat,lng,100000,keyword));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces(MainActivity.this);
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

    public void OnRetrievedNearbyPlaces(ArrayList<PlaceResult> results){
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
        for(PlaceResult result: results){
            mData.add(result);
        }
        //this will update the search queries


        //mData.add(new TestModel());
        mSearchAdapter = new SearchAdapter(mData, this, this);
        mLstSearch.setAdapter(mSearchAdapter);
    }

    public void OnRetrievedWeather(ArrayList<Weather> results){
       String w = results.get(0).main.toString();
       if(w.equals("Rain") || w.equals("Snow") || w.equals("Extreme") || w.equals("Clouds")){
           keyword = "Museum";
           //refreshItems();
       } else {
           keyword = "Park";
       }
        refreshItems();

        /*if(zMoments.get(0).localizedKeyValuePairs.en.root.language.equals("english")){
            keyword = "English"
        }*/
       Log.i("Weather", w);
    }


    class GetMomentDataTask extends AsyncTask<ZoneMoment, Void, KeyValuePayload>{

        @Override
        protected KeyValuePayload doInBackground(ZoneMoment... params) {
            ZoneMoment moment = params[0];
            String url = moment.launchURL + "KeyValuePairs/AsMetadata";

            Log.i("MainActivity", "Http Get: " + url);

            Result result = null;
            KeyValuePayload kvp = null;

            try {
                result = new GetRequest(MainActivity.this, url, null).getResponse();
                Log.i("MainActivity", "Status Code: " + result.status);

                if(result.status >= 200 && result.status < 300) {
                    Log.i("MainActivity", "Http good status code: " + result.status);
                    //Log.i("MainActivity", "RESULT: " + result.response);
                    Gson gson = new Gson();
                    kvp = gson.fromJson(result.response, KeyValuePayload.class);


                    //The list Of available web pages are now stored in the Locales object.
                } else {
                    // Something went wrong with your Request.
                    Log.i("MainActivity", "Http bad status code: " + result.status);
                }

            } catch (Exception e) {
                Log.e("MainActivity", e.toString());
            }

            return kvp;
        }

        @Override
        protected void onPostExecute(KeyValuePayload kvp) {
            super.onPostExecute(kvp);

            Gson gson = new Gson();
            String str = gson.toJson(kvp);
            Log.i("MainActivity", "Downloaded Moment Data: " + str);

            zMoments.add(kvp);

            for(KeyValuePayload k: zMoments){
                /*if(k.localizedKeyValuePairs.en.root.tired != null) {
                    Log.i("MainActivity", "MOMENT: " + k.localizedKeyValuePairs.en.root.tired);
                }
                if(k.localizedKeyValuePairs.en.root.test != null) {
                    Log.i("MainActivity", "MOMENT: " + k.localizedKeyValuePairs.en.root.test);
                }*/
                Log.i("MainActivity", "MOMENT: " + k.localizedKeyValuePairs.en.root.language);
                Log.i("MainActivity", "MOMENT: " + k.localizedKeyValuePairs.en.root.age);
                Log.i("MainActivity", "MOMENT: " + k.localizedKeyValuePairs.en.root.tired);
            }
        }
    }
}
