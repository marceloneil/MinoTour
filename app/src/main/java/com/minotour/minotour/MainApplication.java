package com.minotour.minotour;

import android.app.Application;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;
import com.flybits.core.api.context.FlybitsContext;
import com.flybits.core.api.context.contracts.ContextContract;
import com.flybits.core.api.context.plugins.AvailablePlugins;
import com.flybits.core.api.exceptions.FeatureNotSupportedException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;

import java.util.ArrayList;

//flybits api

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Application Backend
 */
public class MainApplication extends Application {

    private ArrayList<FlybitsContext> activateContext() {

        ArrayList<FlybitsContext> listOfContext = new ArrayList<FlybitsContext>();


        FlybitsContext obj10 = new FlybitsContext(AvailablePlugins.BATTERY, 60000, ContextContract.Priority.LOW, null);
        listOfContext.add(obj10);

        //Flybits.include(this).activateContextPlugin(BATTERY);
        return listOfContext;
    }

    @Override
    public void onCreate(){

        super.onCreate();

        ArrayList<String> listOfLanguages = new ArrayList<>();
        listOfLanguages.add("en");
        FlybitsOptions builder = new FlybitsOptions.Builder(MainApplication.this)
                .setDebug(true)
                .setLocalization(listOfLanguages)
                .enableContextUploading(1, ContextContract.Priority.HIGH)//1 -> Time in minutes between uploads
                .build();

        //Initialize the FlybitsOptions
        Flybits.include(MainApplication.this).initialize(builder);
        Log.i("MainApplication", "Loggin in");

        Flybits.include(this).isUserLoggedIn(true, new IRequestLoggedIn() {
            @Override
            public void onLoggedIn(User user) {
                try {
                    Flybits.include(MainApplication.this).activateContext(null, activateContext());
                    System.out.println("Login success");
                } catch (FeatureNotSupportedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNotLoggedIn() {
                LoginOptions options = new LoginOptions.Builder(getApplicationContext())
                        .loginAnonymously()
                        .setDeviceOSVersion() //Optional
                        .setRememberMeToken() //Optional
                        .build();

                Flybits.include(getApplicationContext()).login(options, new IRequestCallback<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Log.i("MainApplication", "Login successful");
                        //Login Successful


                        try {
                            Flybits.include(MainApplication.this).activateContext(null, activateContext());
                            System.out.println("Login success");
                        } catch (FeatureNotSupportedException e) {
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onFailed(String reason) {
                        //Unsuccessful Login Make UI Action
                        Log.e("MainApplication", "Failed to login: " + reason);
                    }

                    @Override
                    public void onException(Exception exception) {
                        //Unsuccessful Login Make UI Action
                        Log.e("MainApplication", "Failed to login: " + exception.toString());
                    }

                    @Override
                    public void onCompleted() {
                        //Clean up method
                    }
                });
            }
        });

        //ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(49, -79.383184, 200, GooglePlaces.MAXIMUM_RESULTS));
        //RetrieveNearbyPlaces get = new RetrieveNearbyPlaces();
        //get.execute(array);

        /*try {
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("key", BuildConfig.GoogleApiKey);
            params.put("location", "43.653226,-79.383184");
            params.put("radius", "500");
            params.put("type", "point_of_interest");

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            for (int c; (c = in.read()) >= 0; )
                System.out.print((char) c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        GooglePlaces client = new GooglePlaces(BuildConfig.GoogleApiKey);
        //List<Place> places = client.getNearbyPlaces(43.653226, -79.383184, 200, 1);
        //List<Place> places = client.getPlacesByQuery("Empire State Building", GooglePlaces.MAXIMUM_RESULTS);
        for(Place p: places){
            Log.i("Locations", p.getName());
        }*/


    }
}
