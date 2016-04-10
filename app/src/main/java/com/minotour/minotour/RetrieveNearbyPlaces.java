package com.minotour.minotour;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.minotour.minotour.models.DistanceMatrix;
import com.minotour.minotour.models.Element;
import com.minotour.minotour.models.Place;
import com.minotour.minotour.models.Result;
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
public class RetrieveNearbyPlaces extends AsyncTask<ArrayList, Void, ArrayList<Result>> {

    private MainActivity mActivity;

    public RetrieveNearbyPlaces(MainActivity activity){
        mActivity = activity;
    }

    protected ArrayList doInBackground(ArrayList... arrayLists) {
        Place place;

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
                place = gson.fromJson(jsonData, Place.class);

                Log.i("link","https://maps.googleapis.com/maps/api/place/nearbysearch/" + nearData);

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

                Log.i("link", "https://maps.googleapis.com/maps/api/distancematrix/" + distData);

                DistanceMatrix matrix = gson.fromJson(distResponse.body().string(), DistanceMatrix.class);

                for(int i=0;i < place.results.size();i++){
                    Result result = place.results.get(i);
                    if(matrix != null && matrix.rows != null && matrix.rows.size() > 0){
                        Row row = matrix.rows.get(0);

                        if(row.elements != null && row.elements.size() > i){
                            Element element = row.elements.get(i);
                            result.distance = element.distance;
                        }

                        if(matrix.destination_addresses.size() > i){
                            result.destination_addresses = matrix.destination_addresses.get(i);
                        }
                    }
                }

                //String resultString = gson.toJson(place.results.get(0));
                //Log.i("FirstResult", resultString);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return place.results;
    }

    @Override
    protected void onPostExecute(ArrayList<Result> results) {
        super.onPostExecute(results);

        mActivity.OnRetreivedNearbyPlaces(results);
    }
}
