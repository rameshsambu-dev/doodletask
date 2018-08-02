package com.androidtask.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidtask.BuildConfig;
import com.androidtask.R;
import com.androidtask.adapter.LocationAdapter;
import com.androidtask.helper.DiscreteScrollViewOptions;
import com.androidtask.helper.Utitlites;
import com.androidtask.interfaces.LocationEnableInterface;
import com.androidtask.model.Locations;
import com.androidtask.presenter.MainPresentImplementation;
import com.androidtask.view.MainView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements MainView, OnMapReadyCallback, LocationListener,
        DiscreteScrollView.OnItemChangedListener, LocationEnableInterface {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    //Views
    @BindView(R.id.rv_locations)
    DiscreteScrollView rv_locations;

    //Adapter
    private InfiniteScrollAdapter mInfiniteAdapter;
    private ArrayList<Locations> mLocationList;
    //Presenter implementation
    MainPresentImplementation presentImplementation;
    GoogleMap mGoogleMap;
    //Location Properties
    private FusedLocationProviderClient mFusedLocationClient;
    protected LatLng mLastLocation;
    private boolean mIsLocationEnabled;
    private int mCardSelectedPos = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presentImplementation = new MainPresentImplementation(this);
        presentImplementation.getLocationRecords();

        rv_locations.addOnItemChangedListener(this);


        //Map Loading using Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setBackgroundColor(Color.BLACK);

        //Get last known location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Setting DARK THEME T MAP
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
        } catch (Resources.NotFoundException e) {
        }
        mGoogleMap = googleMap;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Location updates will get here
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();

    }


    @Override
    public void showAlert(String message) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onSuccess(ArrayList<Locations> locationResponse) {
        this.mLocationList = locationResponse;
        mInfiniteAdapter = InfiniteScrollAdapter.wrap(new LocationAdapter(getActivity(), locationResponse));
        rv_locations.setAdapter(mInfiniteAdapter);
        rv_locations.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
        rv_locations.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());


    }


    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        int positionInDataSet = mInfiniteAdapter.getRealPosition(adapterPosition);
        mCardSelectedPos = positionInDataSet;
        if (mLocationList != null) {
            drawLine(mLocationList.get(positionInDataSet).getLatLng(), mLocationList.get(positionInDataSet));
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackBar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    /**
     * Location Permission request
     */
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //todo 2
                getLastLocation();
            } else {

                showSnackBar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            //todo 3
            getLastLocation();
        }
    }

    /**
     * Show Snackbar
     *
     * @param mainTextStringId
     * @param actionStringId
     * @param listener
     */
    private void showSnackBar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
        if (provider.equals("gps")) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.app_name)
                    .content(R.string.alert_gps_enable)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.disagree)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

                            if (!provider.contains("gps")) { //if gps is disabled
                                final Intent poke = new Intent();
                                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                                poke.setData(Uri.parse("3"));
                                getActivity().sendBroadcast(poke);
                            }

                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .show();
        }

    }


    void getLocation() {
        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                            drawLine(mLastLocation, null);
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    /**
     * Draw Marker and line between locations
     *
     * @param latLng
     * @param locations
     */
    private void drawLine(LatLng latLng, Locations locations) {
        mGoogleMap.clear();
        try {
            mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
        } catch (Resources.NotFoundException e) {
        }

        //Condition 1 Where current location & Local data not null draw lines
        if (locations != null && mLastLocation != null && mIsLocationEnabled) {
            createMarker(latLng, locations.getName(), "", R.drawable.map_marker_circle);
            createMarker(mLastLocation, locations.getName(), "", R.drawable.ic_location_enabled);
            mGoogleMap.addPolygon(new PolygonOptions()
                    .clickable(true).strokeColor(Color.YELLOW).strokeJointType(JointType.ROUND)
                    .add(latLng, mLastLocation));
            loadCameraZoom(latLng);
        } else {
            //Condition 2 Where current location loading first time
            if (mLastLocation != null && mIsLocationEnabled)
                createMarker(mLastLocation, "Current Location", "", R.drawable.ic_location_enabled);
            //Condition 3 Where current location is disabled and local data is not empty load list item data
            if (!mIsLocationEnabled && mLocationList != null) {
                createMarker(mLocationList.get(mCardSelectedPos).getLatLng(), mLocationList.get(mCardSelectedPos).getName(), "", R.drawable.ic_location_disabled);
            }

            if (latLng != null)
                loadCameraZoom(latLng);
        }

    }

    private void loadCameraZoom(LatLng latLng) {
        CameraPosition camPos = new CameraPosition.Builder().target(latLng).zoom(13).bearing(145).tilt(0).build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mGoogleMap.animateCamera(camUpd3);
    }

    /**
     * Creating marker
     *
     * @param lat
     * @param title
     * @param snippet
     * @param iconResID
     * @return
     */
    protected Marker createMarker(LatLng lat, String title, String snippet, int iconResID) {
        return mGoogleMap.addMarker(new MarkerOptions()
                .position(lat)
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(Utitlites.bitmapDescriptorFromVector(getActivity(), R.drawable.map_marker_circle)));
    }

    @Override
    public void updateLocation(boolean isEnabled) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_newItem:
                if (mIsLocationEnabled) {
                    mIsLocationEnabled = false;
                    Toast.makeText(getActivity(), R.string.location_disabled, Toast.LENGTH_SHORT).show();
                    item.setIcon(getResources().getDrawable(R.drawable.ic_location_disabled));
                } else {
                    mIsLocationEnabled = true;
                    Toast.makeText(getActivity(), R.string.location_enabled, Toast.LENGTH_SHORT).show();
                    item.setIcon(getResources().getDrawable(R.drawable.ic_location_enabled));
                }
                drawLine(mLastLocation, null);

                return false;

            default:
                break;
        }

        return false;
    }
}
