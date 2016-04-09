package com.minotour.minotour;

import android.app.Application;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;

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
    }
}
