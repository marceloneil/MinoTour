package com.minotour.minotour;

import android.app.Application;


//flybits api
import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;

import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;


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


        GooglePlaces client = new GooglePlaces(BuildConfig.GoogleApiKey);
        List<Place> places = client.getNearbyPlaces(200, 500, 2000, GooglePlaces.MAXIMUM_RESULTS);
        for(Place p: places){
            System.out.println(p);
        }


    }
}
