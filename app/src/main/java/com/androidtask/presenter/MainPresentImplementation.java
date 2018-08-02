package com.androidtask.presenter;


import com.androidtask.model.Locations;
import com.androidtask.view.MainView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Lenovo on 01/08/2018.
 */

public class MainPresentImplementation implements MainPresenter {
    MainView view;

    public MainPresentImplementation(MainView loginView) {
        this.view = loginView;
    }

    public void getLocationRecords() {
        Locations locations;
        ArrayList<Locations> locationList = new ArrayList<>();

        locations = new Locations("No Limit Loung", "0.2", "4.5", "",new LatLng(12.900137,77.588352));
        locationList.add(locations);
        locations = new Locations("Test Record 2", "0.5", "4.5", "",new LatLng(12.898622,77.570897));
        locationList.add(locations);
        locations = new Locations("Test Record 3", "0.6", "4.5", "",new LatLng(12.922637,77.617444));
        locationList.add(locations);
        locations = new Locations("Test Record 4", "0.7", "5.0", "",new LatLng(12.956947,77.599270));
        locationList.add(locations);
        locations = new Locations("Test Record 5", "0.9", "5.0", "",new LatLng(12.839939,77.677003));
        locationList.add(locations);

        view.onSuccess(locationList);
    }


    @Override
    public void onDestroy() {

    }
}
