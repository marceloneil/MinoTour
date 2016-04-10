package com.minotour.minotour.models;

import java.util.ArrayList;

/**
 * Created by marcel on 10/04/16.
 */
public class Result {
    public Geometry geometry;
    public String name;
    public String vicinity;
    public Double rating;
    public ArrayList<Photos> photos;
    public Integer price_level;
    public ArrayList<String> types;
    public Distance distance;
    public String destination_addresses;
}
