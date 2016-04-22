package com.example.james.mapnotes;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;


/**
 * Created by James on 07/03/2016.
 * Class to handle data management related to the map
 */
public class MapData implements LocationListener {
    MarkerDatabaseHelper dbHelper;
    MapDisplay parentActivity;
    Context context;

    //used to hold the markers from the database
    ArrayList<UserMarker> currentMarkerInfo;

    double maxDistance = 0.5;
    double freshTimer = 60000;

    //gets and stores the date given by the user through the main activity
    private  String mDate, mTime, mTitle, mBody;
    private LatLng mLocation;
    Location currentLocation;

    long lastUpdated = 0;



    MapData(Context c,  MapDisplay activity)
    {
        context = c;
        dbHelper = new MarkerDatabaseHelper(c);
        parentActivity = activity;

        //load values from shard preferenaces
        SharedPreferences perfs = context.getSharedPreferences("Databse Data", 0);
        lastUpdated = perfs.getLong("LAST_TIME", 0);

        //set defaults
        mTime = "NULL";
        mDate = "NULL";

    }


    //listener for when user location changes. When user has moved far enough new set of markers are downloaded from database
    @Override
    public void onLocationChanged(Location location)
    {
            if (Math.abs(currentLocation.getLatitude() - location.getLatitude()) > maxDistance || Math.abs(currentLocation.getLongitude() - location.getLongitude()) > maxDistance) {
                currentLocation = location;
                //download new local markers if moved enough
                if (!parentActivity.downloadAll && !parentActivity.wifiOnly && parentActivity.isValidConnection())
                    parentActivity.remoteHelper.getLocalMarkers(parentActivity, location.getLatitude(), location.getLongitude());

            }
    }

    //finds if the marker at the position passed in is an event or not. Returns the UserMarker for that marker
    public UserMarker findEvent(LatLng position)
    {
        if(currentMarkerInfo != null)
        {
            for (UserMarker u: currentMarkerInfo)
            {
                if(u.Longitude == position.longitude && u.Latitude == position.latitude && !u.Date.equals("NULL"))
                {

                    return u;
                }
            }

        }



        return null;
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

    //sets current time
    public void setCurrentTime()
    {
        lastUpdated = System.currentTimeMillis();

        SharedPreferences perfs = context.getSharedPreferences("Databse Data", 0);

       SharedPreferences.Editor editor = perfs.edit();
        editor.putLong("LAST_TIME", lastUpdated);

        editor.apply();
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


    //get marker data
    public ArrayList<UserMarker> getMarkers()
    {
        return currentMarkerInfo;
    }


    //if data is more than a minute old download new data
    public Boolean isDataFresh()
    {
        return (System.currentTimeMillis() - lastUpdated < freshTimer);
    }
}

