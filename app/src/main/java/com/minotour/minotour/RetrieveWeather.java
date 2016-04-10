package com.minotour.minotour;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.minotour.minotour.models.Weather;
import com.minotour.minotour.models.WeatherResult;
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
public class RetrieveWeather extends AsyncTask<ArrayList, Void, ArrayList<Weather>> {
    WeatherResult weatherResult;

    private MainActivity mActivity;

    public RetrieveWeather(MainActivity activity){
        mActivity = activity;
    }

    protected ArrayList doInBackground(ArrayList... arrayLists) {
            try {
                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("APPID", "aa8f02cf5084844920b2640fd9f6871e");
                params.put("lat", arrayLists[0].get(0).toString());
                params.put("lon", arrayLists[0].get(1).toString());
                params.put("units", arrayLists[0].get(2).toString());

                StringBuilder data = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (data.length() != 0) data.append('&');
                    data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    data.append('=');
                    data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }

                Request request = new Request.Builder()
                        .url("http://api.openweathermap.org/data/2.5/weather?" + data)
                        .build();

                Response response = client.newCall(request).execute();

                String jsonData = response.body().string();
                weatherResult = gson.fromJson(jsonData, WeatherResult.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return weatherResult.weather;
    }

    @Override
    protected void onPostExecute(ArrayList<Weather> results) {
        super.onPostExecute(results);

        mActivity.OnRetrievedWeather(results);
    }
}
