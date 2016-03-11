package com.example.james.mapnotes;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by James on 07/03/2016.
 * Class to handle data management related to the map
 */
public class MapData {
    MarkerDatabaseHelper dbHelper;

    MapData(Context c)
    {
        dbHelper = new MarkerDatabaseHelper(c);

    }

    //gets and stores the date given by the user through the main activity
    public void setDate(String date)
    {

    }

    public void setTime(String time)
    {

    }
}
