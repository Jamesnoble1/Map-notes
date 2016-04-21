package com.example.james.mapnotes;

import android.app.AlertDialog;
import android.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.jar.Manifest;


public class MapDisplay extends FragmentActivity
        implements View.OnClickListener, OnMapReadyCallback, TimePickerFragment.TimeChoiceFragmentListener, DatePickerFragment.DateChoiceFragmentListener, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private InfoWindow customInfoWindow;
    private GoogleMap mMap;

    MapData mapData;
    boolean isEvent;
    GoogleApiClient mApiClient;
    Button displayType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        displayType = (Button)findViewById(R.id.displayTypebtn);
        displayType.setOnClickListener(this);

        customInfoWindow = new InfoWindow(this);

        if(mApiClient == null)
        {
            mApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();

        }


        //check permissions
        int permissionsCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionsCheck == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission_group.LOCATION}, //asks user if we can get location permissions
                    0);


        }

        mapData = new MapData(getApplicationContext(),  mApiClient);
        mapData.loadData();

        isEvent = false;
    }


    protected void onStart()
    {
        mApiClient.connect();
        super.onStart();
    }

    protected void onStop()
    {
        mApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle bundle)
    {

        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permissionsCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int otherCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionsCheck == PackageManager.PERMISSION_GRANTED || otherCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, mapData);
        }

        //set initial camera location if location works
        if(permissionsCheck == PackageManager.PERMISSION_GRANTED || otherCheck == PackageManager.PERMISSION_GRANTED) {

            Location tempLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            ;


            if(tempLocation != null) {

                LatLng cameraMove = new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraMove, 12.0f));

                //get local markers if not downloading all
            }
            else
            {
                //if no location and not downloading all display error toast and display local markers
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {

    }
    //listens for click so can add an event to map
    @Override
    public void onMapLongClick(LatLng point)
    {

        mapData.setLocation(point);
        chooseIfEvent();
    }

    @Override
    public void onClick(View v)
    {
        if(v == displayType)
        {
            //inflate choice
            chooseFilter();
        }
    }
    //These get results from the fragments that take date/time entry
    @Override
    public void onFinishedTimeEntry(String selectedTime) {
       if(!selectedTime.equals("INVALID")) {
           mapData.setTime(selectedTime);
           getMessageBody(true);
       }


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

    //Main function for getting user input regarding a new message. calls the first function needed for message type
    private void createMessage()
    {

        //if event get date and time
        if(isEvent)
        {
            displayDatePicker();
        }
        else
        {
            getMessageBody(true);
        }
    }

    //alows user to filter between displaying all, event, messages
    private void chooseFilter()
    {
        //create view from pup_up_message xml
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View eventChoice = inflater.inflate(R.layout.pop_up_message, null);
        final CharSequence[] choices = {"Events", "Messages", "All"};
        //build alert
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(eventChoice);
        alertBuilder.setCancelable(true);

        alertBuilder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mMap.clear();
                switch (which) {
                    case 0:
                        mapData.loadEventData();
                        dialog.dismiss();
                        break;
                    case 1:
                        mapData.loadMessageData();
                        dialog.dismiss();
                        break;
                    case 2:
                        mapData.loadData();
                        dialog.dismiss();
                        break;
                }

                loadMarkers();
            }
        });


        //get text view from R.id
        TextView titleText, messageText;
        titleText = (TextView)eventChoice.findViewById(R.id.titleText);
        messageText = (TextView)eventChoice.findViewById(R.id.messageText);

        //set text correctly
        titleText.setText("Display");
        messageText.setText("Events, Message or Both");

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
    //brings up an alert dialogue to choose if a message or an event. Sets isEvent
    private void chooseIfEvent()
    {
        //create view from pup_up_message xml
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View eventChoice = inflater.inflate(R.layout.pop_up_message, null);

        //build alert
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(eventChoice);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //shows new date picker fragment
                isEvent = true;
                createMessage();
            }
        });

        //logic for negativ button press
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isEvent = false;
                createMessage();
            }
        });
        //get text view from R.id
        TextView titleText, messageText;
        titleText = (TextView)eventChoice.findViewById(R.id.titleText);
        messageText = (TextView)eventChoice.findViewById(R.id.messageText);

        //set text correctly
        titleText.setText("Event?");
        messageText.setText("Is this an event?");

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }

    //gets message body and title. if true will call its self after it closes to get message body
    private void getMessageBody(final boolean isTitle)
    {
        //create view from pup_up_message xml
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View userMessage = inflater.inflate(R.layout.get_user_message, null);

        final EditText textInput = (EditText)userMessage.findViewById(R.id.messageEntryTextField);

        //build alert
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(userMessage);
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isTitle) {
                    //if string is not empty take message else error and take new input
                    if (!textInput.getText().toString().isEmpty()) {
                        mapData.setTitle(textInput.getText().toString());
                        getMessageBody(false);
                    } else {
                        incorrectInputPopUp(true);
                    }
                } else {
                    //if string is not empty take message else error and take new input
                    if (!textInput.getText().toString().isEmpty()) {
                        //sets the user message in mapData
                        mapData.setBody(textInput.getText().toString());
                        //tells map data to write to database
                        spawnMarker();
                    } else {
                        incorrectInputPopUp(false);
                    }
                }
            }
        });

        //get text view from R.id
        TextView titleText = (TextView)userMessage.findViewById(R.id.messegeEntryTitle);

        if(isTitle)
        {
            titleText.setText("Enter Title");
        }
        else
        {
            titleText.setText("Enter Message");
        }


        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();


    }

    //adds the marker to the map at long click location. then saves data to db
    private void spawnMarker()
    {



        mMap.addMarker(new MarkerOptions().position(mapData.getMarkerLocation()).snippet(mapData.getBody()).title(mapData.getTitle()));
        mapData.saveData();

    }

    //loads markers saved in DB
    private void loadMarkers()
    {
        ArrayList<UserMarker> tempOptions = mapData.getMarkers();

        //gets marker options from mapData. loops through the array list until all markers are displayed
        if(tempOptions != null)
        {
            for (UserMarker options: tempOptions) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(options.Latitude, options.Longitude)).snippet(options.Snippet).title(options.Title));


            }
        };

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

    private void incorrectInputPopUp(final boolean isTitle)
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
                //recalls message box when ok is pressed
                getMessageBody(isTitle);
            }
        });


        //get text view from R.id
        TextView titleText, messageText;
        titleText = (TextView)incorrectDate.findViewById(R.id.titleText);
        messageText = (TextView)incorrectDate.findViewById(R.id.messageText);

        //set text correctly
        titleText.setText("No Text");
        messageText.setText("You need to enter some text");

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
        mMap.setOnMapLongClickListener(this);
        mMap.setInfoWindowAdapter(customInfoWindow);


        //if download all markers download all
        loadMarkers();

    }
}
