package com.example.james.mapnotes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by James on 07/03/2016.
 * Class to handle data management related to the map
 */
public class MapData implements LocationListener {
    MarkerDatabaseHelper dbHelper;
    LocationManager mLocationManager;
    GoogleApiClient mApiClient;
    Context context;

    ArrayList<UserMarker> currentMarkerInfo;
    //gets and stores the date given by the user through the main activity
    private  String mDate, mTime, mTitle, mBody;
    private LatLng mLocation;




    MapData(Context c,  GoogleApiClient apiClient)
    {
        context = c;
        dbHelper = new MarkerDatabaseHelper(c);

        mApiClient = apiClient;


        mTime = "NULL";
        mDate = "NULL";

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e("Location", location.toString());
    }


    //gets and stores the date given by the user through the main activity
    public void setDate(String date)
    {
        mDate = date;
    }

    public void setTime(String time)
    {
        mTime = time;
    }
    public void setTitle(String title)
    {
        mTitle = title;
    }

    public void setBody(String body)
    {
        mBody = body;
    }

    public void setLocation(LatLng location)
    {
        mLocation = location;
    }


    //getters for data given by user
    public String getDate()
    {
        return mDate;
    }

    public String getTime()
    {
       return mTime;
    }
    public String getTitle()
    {
        return mTitle;
    }

    public String getBody()
    {
        return mBody;
    }


    public LatLng getMarkerLocation()
    {
        return mLocation;
    }

    //saves all data to database on another thread.
    public void saveData()
    {
        UserMarker tempUMarker = new UserMarker(mLocation.latitude, mLocation.longitude, mTitle, mBody, 1, mDate, mTime);

        BackgroundWriteToDB asyncWriteDB = new BackgroundWriteToDB(context, dbHelper);
        asyncWriteDB.execute(tempUMarker);

        //sets strings to default
        mDate = "NULL";
        mTime = "NULL";
        mTitle = "";
        mBody = "";
    }

    //loads data from local DB
    public void loadData()
    {
        currentMarkerInfo = dbHelper.getAllMarkers();
    }

    //loads data from local DB
    public void loadEventData()
    {
        currentMarkerInfo = dbHelper.getAllEvents();
    }
    //loads data from local DB
    public void loadMessageData()
    {
        currentMarkerInfo = dbHelper.getAllMessages();
    }
    //loads data from remote DB
    public void loadRemoteData()
    {

    }

    //get marker data
    public ArrayList<UserMarker> getMarkers()
    {
        return currentMarkerInfo;
    }
    //gets user location
    public Location getUserLocation()
    {
        int permissionsCheckCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionsCheckFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
      // if(permissionsCheckCoarse != PackageManager.PERMISSION_DENIED || permissionsCheckFine != PackageManager.PERMISSION_DENIED )
       {
           return LocationServices.FusedLocationApi.getLastLocation(mApiClient);
       }

    // return null;
    }
}
