package com.minotour.minotour;

import android.app.Application;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;

/**
 * Created by Marcel O'Neil on 09/04/16.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {

        FlybitsOptions builder = new FlybitsOptions.Builder(this)
                //Additional Options Can Be Added Here
                .build();

        //Initialize the FlybitsOptions
        Flybits.include(this).initialize(builder);

        super.onCreate();
    }
}
