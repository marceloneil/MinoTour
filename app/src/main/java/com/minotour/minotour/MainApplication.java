package com.minotour.minotour;

import android.app.Application;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;

import java.util.ArrayList;
import java.util.Arrays;

import se.walkercrou.places.GooglePlaces;

//flybits api

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Application Backend
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        FlybitsOptions builder = new FlybitsOptions.Builder(this)
                //Additional Options Can Be Added Here
                .build();

        //Initialize the FlybitsOptions
        Flybits.include(this).initialize(builder);
        ArrayList<Object> array = new ArrayList<Object>(Arrays.asList(43.653226, -79.383184, 200, GooglePlaces.MAXIMUM_RESULTS));
        RetrieveNearbyPlaces get = new RetrieveNearbyPlaces();
        get.execute(array);

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
