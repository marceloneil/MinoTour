package com.minotour.minotour;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

/**
 * Created by Marcel O'Neil on 09/04/16.
 * Main Activity
 */
public class MainActivity extends FragmentActivity {

    protected MainApplication app;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.application_main);

        // Get the application instance
        app = (MainApplication) getApplication();
    }
}
