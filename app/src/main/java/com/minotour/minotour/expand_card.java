package com.minotour.minotour;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Curtis on 2016-04-10.
 */
public class expand_card extends Activity{
    public void onCreate(Bundle savedInstanceState) throws SecurityException, IllegalArgumentException {
        super.onCreate(savedInstanceState);


        String address = (String) getIntent().getSerializableExtra("query_address");
        String title = (String) getIntent().getSerializableExtra("query_name");
        String distance = (String) getIntent().getSerializableExtra("query_distance");
        String desc = (String) getIntent().getSerializableExtra("query_desc");

        setContentView(R.layout.expand_card);

        TextView titleId = (TextView) findViewById(R.id.item_search_txtTitle);
        titleId.setText(title);
        TextView addressId = (TextView) findViewById(R.id.item_search_txtAddress);
        addressId.setText(address);
        TextView distanceId = (TextView) findViewById(R.id.item_search_txtDistance);
        distanceId.setText(distance);
        TextView descId = (TextView) findViewById(R.id.item_search_txtDesc);
        descId.setText(desc);





    }
}
