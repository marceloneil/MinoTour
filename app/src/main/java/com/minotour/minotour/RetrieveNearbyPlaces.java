package com.minotour.minotour;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
        }
        return null;
    }

}
