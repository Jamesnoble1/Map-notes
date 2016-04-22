package com.example.james.mapnotes;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;



public class MapDisplay extends FragmentActivity
        implements View.OnClickListener,
        OnMapReadyCallback,
        TimePickerFragment.TimeChoiceFragmentListener,
        DatePickerFragment.DateChoiceFragmentListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
        private InfoWindow customInfoWindow;
    private GoogleMap mMap;

    MapData mapData;
    boolean isEvent;
    GoogleApiClient mApiClient;
    Button displayType;
    RemoteDatabaseHelper remoteHelper = new RemoteDatabaseHelper();
    ConnectivityManager mConnectivityManager;

    TextView dateDisplay;
    TextView timeDisplay;

    Boolean wifiOnly = false;
    Boolean downloadAll = true;
    Boolean smsSend = false;

    int interval = 10000;
    int fastestInterval = 1000;


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

        dateDisplay = (TextView)findViewById(R.id.dateTextDisplay);
        timeDisplay = (TextView)findViewById(R.id.timeDisplay);

        //turns views invisible until they're used
        timeDisplay.setVisibility(View.INVISIBLE);
        dateDisplay.setVisibility(View.INVISIBLE);

        customInfoWindow = new InfoWindow(this);

        //set user options
        SharedPreferences prefs = getSharedPreferences("User prefs", 0);

        wifiOnly = prefs.getBoolean("WIFI", false);
        downloadAll = prefs.getBoolean("ALL_MARKERS", true);
        smsSend = prefs.getBoolean("SMS", true);

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


        //get connectivity manager and check permissions
        permissionsCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        if(permissionsCheck == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, //asks user if we can get location permissions
                    0);


        }
        mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mapData = new MapData(getApplicationContext(),  this);

        if(mapData.lastUpdated == 0) {
            mapData.setCurrentTime();
        }

        //load data on create if data is fresh or no suitable connection.
        // Similar check exists on connect as we need GPS location for local markers
        if(mapData.isDataFresh() || !isValidConnection() )
        {
           mapData.loadData();
        }
        else if(downloadAll)
        {
            remoteHelper.getAllMarkers(this);
        }

        isEvent = false;
    }


    //mainly used to store map data
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        //get and unpack variables that can't be saved directly
        double lat =  mapData.getMarkerLocation().latitude;
        double lng = mapData.getMarkerLocation().longitude;

        //save these variables
        savedInstanceState.putString("Date", mapData.getDate());
        savedInstanceState.putString("Time", mapData.getTime());
        savedInstanceState.putString("Title", mapData.getTitle());
        savedInstanceState.putString("Snippet", mapData.getBody());
        savedInstanceState.putDouble("Lat", lat);
        savedInstanceState.putDouble("Lng", lng);


        super.onSaveInstanceState(savedInstanceState);
    }

    //set mapData on restore. Everything else is set by default, onCreate or loaded when needed
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mapData.setDate(savedInstanceState.getString("Date"));
        mapData.setTime(savedInstanceState.getString("Time"));
        mapData.setTitle(savedInstanceState.getString("Title"));
        mapData.setBody(savedInstanceState.getString("Snippet"));

        double lat = savedInstanceState.getDouble("Lat");
        double lng = savedInstanceState.getDouble("Lng");

        mapData.setLocation(new LatLng(lat, lng));


        super.onRestoreInstanceState(savedInstanceState);
    }

    //makes sure we connect to API client when activity starts
    protected void onStart()
    {
        mApiClient.connect();
        super.onStart();
    }

    //make sure we disconnect on stop
    protected void onStop()
    {
        mApiClient.disconnect();
        super.onStop();
    }

    //these listeners used for handling date and time views
    public void onMapClick(LatLng pos)
    {
        //set views invisible
        dateDisplay.setVisibility(View.INVISIBLE);
        timeDisplay.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        //set views invisible
        dateDisplay.setVisibility(View.INVISIBLE);
        timeDisplay.setVisibility(View.INVISIBLE);
        UserMarker temp = mapData.findEvent(marker.getPosition());

        if(temp != null)
        {

           //set time string
            String tempString = "Time: " + temp.Time;
            timeDisplay.setText(tempString);
            //set date String
            tempString = "Date: "+ temp.Date;
            dateDisplay.setText(tempString);

            //set views visible
            dateDisplay.setVisibility(View.VISIBLE);
            timeDisplay.setVisibility(View.VISIBLE);
        }

        return false;
    }

    //what to do when connected to API client
    @Override
    public void onConnected(Bundle bundle)
    {

        //set up location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        //make sure we have permissions
        int permissionsCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int otherCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        //if we have permission get updates as per locationRequest object
        if(permissionsCheck == PackageManager.PERMISSION_GRANTED || otherCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, mapData);
        }

        //set initial camera location if location works
        if(permissionsCheck == PackageManager.PERMISSION_GRANTED || otherCheck == PackageManager.PERMISSION_GRANTED) {

            Location tempLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            mapData.currentLocation = tempLocation;


            //if a location exists
            if(tempLocation != null) {

                LatLng cameraMove = new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraMove, 12.0f));

                //get local markers if not downloading all
                if(!downloadAll && !mapData.isDataFresh() && isValidConnection())
                {
                    remoteHelper.getLocalMarkers(this, tempLocation.getLatitude(),tempLocation.getLongitude() );
                }

            }
            else
            {
                //if no location and not downloading all display error toast and display local DB markers
                Toast.makeText(getApplicationContext(), "No Location, Showing all available markers", Toast.LENGTH_LONG).show();
                if(!downloadAll && !mapData.isDataFresh() && isValidConnection())
                {
                    remoteHelper.getAllMarkers(this);
                }
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
    //listens for long click so can add an event to map
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
            //inflate choice so we can choose which message types to show
            chooseFilter();
        }
    }

    //returns true if a connection we can use
    public Boolean isValidConnection()
    {
        Boolean isValid;
        NetworkInfo currentNetwork = mConnectivityManager.getActiveNetworkInfo();

        isValid = (currentNetwork != null && currentNetwork.isConnectedOrConnecting());

        if(wifiOnly && currentNetwork != null)
        {
            isValid = (currentNetwork.getType() == ConnectivityManager.TYPE_WIFI);
            Log.e("test", "Should be false");
        }

        return isValid;

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



    //adds the marker to the map at long click location. then saves data to db
    private void spawnMarker()
    {



        mMap.addMarker(new MarkerOptions().position(mapData.getMarkerLocation()).snippet(mapData.getBody()).title(mapData.getTitle()));
        mapData.saveData();

    }

    //loads markers saved in DB
    public void loadMarkers()
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
        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapClickListener(this);
        mMap.setInfoWindowAdapter(customInfoWindow);



        //loads markers when map is ready
        loadMarkers();
    }


    /* all functions below here are layout inflaters. These are quite lengthy will refactor when I have time to reduce reused code*/
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

    //pops up saying date is incorrect
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


    //allows user to filter between displaying all, event, messages
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
}
