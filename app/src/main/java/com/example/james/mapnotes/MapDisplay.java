package com.example.james.mapnotes;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


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
    MapData mapData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
        mapData = new MapData(getApplicationContext());
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
            createMessage();

        }
    }

    //These get results from the fragments that take date/time entry
    @Override
    public void onFinishedTimeEntry(String selectedTime) {
        mapData.setTime(selectedTime);


    }

    @Override
    public void onFinishedDateEntry(String selectedDate) {
       if(!selectedDate.equals("INVALID"))
       {

           mapData.setDate(selectedDate);
           displayTimePicker();
       }
       else
       {
           //display error
           incorrectDatePopUp();
       }
    }

    //Main function for getting user input regarding a new message
    private void createMessage()
    {
        //get if this is a message or an event

        //if event get date and time
        if(chooseIfEvent())
        {
            displayDatePicker();
        }
        //get title of message

        //get body of message

        //construct marker and add to map

        //tell data class to write data to DB
    }

    //brings up an alert dialogue to choose if a message or an event
    private boolean chooseIfEvent()
    {

        return true;
    }

    //gets message body
    private void getMessageBody()
    {
        //create view from pup_up_message xml
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View userMessage = inflater.inflate(R.layout.get_user_message, null);

        final EditText nameInput = (EditText)userMessage.findViewById(R.id.messageEntryTextField);

        //build alert
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(userMessage);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //get text view from R.id



        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();


    }
    //displays date picker fragment
    private void displayDatePicker()
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "choosedate");
    }

    //displays time picker fragment
    private void displayTimePicker()
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "choose time");
    }

    //pop up if incorrect date given by user
    private void incorrectDatePopUp()
    {
        //create view from pup_up_message xml
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View incorrectDate = inflater.inflate(R.layout.pop_up_message, null);

        //build alert
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(incorrectDate);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //shows new date picker fragment
                displayDatePicker();
            }
        });

        //get text view from R.id
        TextView titleText, messageText;
        titleText = (TextView)incorrectDate.findViewById(R.id.titleText);
        messageText = (TextView)incorrectDate.findViewById(R.id.messageText);

        //set text correctly
        titleText.setText("Incorrect Date");
        messageText.setText("The date needs to be today or later");

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

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
