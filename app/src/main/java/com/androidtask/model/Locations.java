package com.androidtask.model;

import com.google.android.gms.maps.model.LatLng;

public class Locations {
    private String name;
    private String distance;
    private String rating;
    private String imageUrl;


    private LatLng latLng;

    public Locations(String name, String distance, String rating, String imageUrl, LatLng latLng) {
        this.name = name;
        this.distance = distance;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public CharSequence getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
