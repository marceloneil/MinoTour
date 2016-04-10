package com.minotour.minotour;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Curtis on 2016-04-10.
 */
public class expand_card extends Activity{
    public void onCreate(Bundle savedInstanceState) throws SecurityException, IllegalArgumentException {
        super.onCreate(savedInstanceState);


        String address = (String) getIntent().getSerializableExtra("query_address");
        String title = (String) getIntent().getSerializableExtra("query_name");
        String distance = (String) getIntent().getSerializableExtra("query_distance");
        String image = (String) getIntent().getSerializableExtra("query_image");


        setContentView(R.layout.expand_card);

        TextView titleId = (TextView) findViewById(R.id.item_search_txtTitle);
        titleId.setText(title);
        TextView addressId = (TextView) findViewById(R.id.item_search_txtAddress);
        addressId.setText(address);
        TextView distanceId = (TextView) findViewById(R.id.item_search_txtDistance);
        distanceId.setText(distance);

        ImageView locationImage;
        locationImage = (ImageView) findViewById(R.id.expand_image_location);

        if(image != null) {
            Picasso.with(this).load(image).into(locationImage);
        }
    }
}
