package com.minotour.minotour;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Using the google api
 */
public class RetrieveNearbyPlaces extends AsyncTask<ArrayList, Void, List> {

    protected ArrayList doInBackground(ArrayList... arrayLists) {
        GooglePlaces client = new GooglePlaces(BuildConfig.GoogleApiKey);
        List<Place> places = client.getNearbyPlaces((double) arrayLists[0].get(0),(double) arrayLists[0].get(1),(int) arrayLists[0].get(2),(int) arrayLists[0].get(3));
        for (Place p : places) {
            Log.i("Locations", p.getName());
            try {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("key", BuildConfig.GoogleApiKey);
                params.put("origins", (arrayLists[0].get(0)).toString() + "," + (arrayLists[0].get(1)).toString());
                params.put("destinations", p.getLatitude() + "," + p.getLongitude());

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                postData.insert(0, "json?");

                OkHttpClient HTTPclient = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/distancematrix/" + postData)
                        .build();
                Response response = HTTPclient.newCall(request).execute();

                Log.i("Output:", response.body().string());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
