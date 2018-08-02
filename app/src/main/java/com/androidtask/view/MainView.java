package com.androidtask.view;

import com.androidtask.model.Locations;

import java.util.ArrayList;

/**
 * Created by Lenovo on 01/08/2018.
 */

public interface MainView {
    void showAlert(String message);
    void showProgress();
    void hideProgress();
    void onSuccess(ArrayList<Locations> locationResponse);
}
