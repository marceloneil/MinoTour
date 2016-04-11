package com.minotour.minotour;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.minotour.minotour.models.DistanceMatrix;
import com.minotour.minotour.models.Element;
import com.minotour.minotour.models.Place;
import com.minotour.minotour.models.PlaceResult;
import com.minotour.minotour.models.Row;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Using the google api
 */
public class RetrieveNearbyPlaces extends AsyncTask<ArrayList, Void, ArrayList<PlaceResult>> {

    private MainActivity mActivity;

    public RetrieveNearbyPlaces(MainActivity activity) {
        mActivity = activity;
    }

    protected ArrayList<PlaceResult> doInBackground(ArrayList... arrayLists) {
        Place place;

        try {
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            Map<String, Object> nearParams = new LinkedHashMap<>();
            nearParams.put("key", BuildConfig.GoogleApiKey);
            nearParams.put("location", arrayLists[0].get(0).toString() + "," + arrayLists[0].get(1).toString());
            if (arrayLists[0].get(2) != null) {
                nearParams.put("keyword", arrayLists[0].get(2).toString());
            }
            if (arrayLists[0].get(3) != null) {
                nearParams.put("type", arrayLists[0].get(3).toString());
            }
            if (arrayLists[0].get(2) == null && arrayLists[0].get(3) == null) {
                nearParams.put("type", "cafe");
            }
            nearParams.put("rankby", "distance");
            nearParams.put("opennow", "true");

            StringBuilder nearData = new StringBuilder();
            for (Map.Entry<String, Object> param : nearParams.entrySet()) {
                if (nearData.length() != 0) nearData.append('&');
                nearData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                nearData.append('=');
                nearData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            Request nearRequest = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + nearData)
                    .build();
            Response nearResponse = client.newCall(nearRequest).execute();

            Log.i("LinkNearby", "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + nearData);

            String jsonData = nearResponse.body().string();
            place = gson.fromJson(jsonData, Place.class);

            for (int i = 0; i < place.results.size(); i++) {
                PlaceResult result = place.results.get(i);
                if (result.photos != null) {
                    if (result.photos.get(0).photo_reference != null && result.photos.get(0).width != null) {
                        Map<String, Object> photoParams = new LinkedHashMap<>();
                        photoParams.put("key", BuildConfig.GoogleApiKey);
                        photoParams.put("maxwidth", result.photos.get(0).width);
                        photoParams.put("photoreference", result.photos.get(0).photo_reference);

                        StringBuilder photoData = new StringBuilder();
                        for (Map.Entry<String, Object> param : photoParams.entrySet()) {
                            if (photoData.length() != 0) photoData.append("&");
                            photoData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                            photoData.append("=");
                            photoData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                        }
                        result.photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" + photoData.toString();
                    }
                }
            }

            StringBuilder loc = new StringBuilder();
            for (PlaceResult result : place.results) {
                if (loc.length() != 0) loc.append('|');
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

            Request distRequest = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/distancematrix/json?" + distData)
                    .build();
            Response distResponse = client.newCall(distRequest).execute();

            Log.i("linkDistance", "https://maps.googleapis.com/maps/api/distancematrix/json?" + distData);

            DistanceMatrix matrix = gson.fromJson(distResponse.body().string(), DistanceMatrix.class);

            for (int i = 0; i < place.results.size(); i++) {
                PlaceResult result = place.results.get(i);
                if (matrix != null && matrix.rows != null && matrix.rows.size() > 0) {
                    Row row = matrix.rows.get(0);

                    if (row.elements != null && row.elements.size() > i) {
                        Element element = row.elements.get(i);
                        result.distance = element.distance;
                    }

                    if (matrix.destination_addresses.size() > i) {
                        result.destination_addresses = matrix.destination_addresses.get(i);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return place.results;
    }

    @Override
    protected void onPostExecute(ArrayList<PlaceResult> results) {
        super.onPostExecute(results);

        mActivity.OnRetrievedNearbyPlaces(results);
    }
}
