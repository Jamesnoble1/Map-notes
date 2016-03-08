package com.example.james.mapnotes;

import android.app.DialogFragment;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapDisplay extends FragmentActivity
        implements OnMapReadyCallback, View.OnClickListener, TimePickerFragment.TimeChoiceFragmentListener, DatePickerFragment.DateChoiceFragmentListener  {
        private InfoWindow customInfoWindow;
    private GoogleMap mMap;
    Button newMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        newMarker = (Button)findViewById(R.id.mapButton);

        //sets onclick listener for buttons
        newMarker.setOnClickListener(this);

        customInfoWindow = new InfoWindow(this);

    }

    @Override
    public void onClick(View v)
    {
        if(v == newMarker)
        {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "choosedate");
        }
    }

    //These get results from the fragments that take date/time entry
    @Override
    public void onFinishedTimeEntry(String selectedTime)
    {
        Log.i("time is:", selectedTime);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "choosedate");
    }

    @Override
    public void onFinishedDateEntry(String selectedDate)
    {
        Log.i("date is:", selectedDate);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(customInfoWindow);

        LatLng test = new LatLng(56.4640, -2.97);
        mMap.addMarker(new MarkerOptions().position(test).title("This place is the bestest. \n TESTINGTITLE ").snippet("Does this work like I expect it to? \n Whats the limit to this?"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(test, 16.0f));


    }
}
