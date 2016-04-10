package com.minotour.minotour;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

                Log.i("link", "https://maps.googleapis.com/maps/api/place/nearbysearch/" + nearData);
                //Log.i("Response", nearResponse.body().string());
                try {
                    String jsonData = nearResponse.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    JSONArray Jarray = Jobject.getJSONArray("results");
                    Log.i("JSONObject", Jobject.toString());
                    Log.i("JSONArray", Jarray.toString());
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
                        Log.i("OtherObject", object.toString());
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }

                /*Map<String, Object> distParams = new LinkedHashMap<>();
                distParams.put("key", BuildConfig.GoogleApiKey);
                distParams.put("origins", (arrayLists[0].get(0)).toString() + "," + (arrayLists[0].get(1)).toString());
                distParams.put("destinations", p.getLatitude() + "," + p.getLongitude());

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
                Response distResponse = client.newCall(distRequest).execute();*/

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
