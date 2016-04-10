package com.minotour.minotour;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.minotour.minotour.models.Place;
import com.minotour.minotour.models.Result;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Using the google api
 */
public class RetrieveNearbyPlaces extends AsyncTask<ArrayList, Void, List> {

    protected ArrayList doInBackground(ArrayList... arrayLists) {
            try {
                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();

                Map<String, Object> nearParams = new LinkedHashMap<>();
                nearParams.put("key", BuildConfig.GoogleApiKey);
                nearParams.put("location", arrayLists[0].get(0).toString() + "," + arrayLists[0].get(1).toString());
                nearParams.put("radius", arrayLists[0].get(2).toString());
                nearParams.put("keyword", arrayLists[0].get(4).toString());
                nearParams.put("opennow", "true");

                StringBuilder nearData = new StringBuilder();
                for (Map.Entry<String, Object> param : nearParams.entrySet()) {
                    if (nearData.length() != 0) nearData.append('&');
                    nearData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    nearData.append('=');
                    nearData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                nearData.insert(0, "json?");

                Request nearRequest = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/place/nearbysearch/" + nearData)
                        .build();
                Response nearResponse = client.newCall(nearRequest).execute();

                String jsonData = nearResponse.body().string();
                Place place = gson.fromJson(jsonData, Place.class);

                StringBuilder loc = new StringBuilder();
                for (Result result : place.results){
                    if(loc.length() != 0) loc.append('|');
                    loc.append(result.geometry.location.lat);
                    loc.append(",");
                    loc.append(result.geometry.location.lng);
                }

                Map<String, Object> distParams = new LinkedHashMap<>();
                distParams.put("key", BuildConfig.GoogleApiKey);
                distParams.put("origins", (arrayLists[0].get(0)).toString() + "," + (arrayLists[0].get(1)).toString());
                distParams.put("destinations", loc.toString());

                StringBuilder distData = new StringBuilder();
                for (Map.Entry<String, Object> param : distParams.entrySet()) {
                    if (distData.length() != 0) distData.append('&');
                    distData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    distData.append('=');
                    distData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                distData.insert(0, "json?");

                Request distRequest = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/distancematrix/" + distData)
                        .build();
                Response distResponse = client.newCall(distRequest).execute();

                Log.i("response", distResponse.body().string());

                //Request request = new Request.Builder()
                //        .url("https://maps.googleapis.com/maps/api/distancematrix/" + postData)
               //         .build();
               // Response response = HTTPclient.newCall(request).execute();

                //Log.i("Output:", response.body().string());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        return null;
    }
    static class Gist {
        Map<String, GistFile> files;
    }

    static class GistFile {
        String content;
    }
}
