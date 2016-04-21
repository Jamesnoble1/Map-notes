package com.example.james.mapnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;

import java.util.ArrayList;

/**
 * Created by James on 06/03/2016.
 * Database helper to handle communication with local database
 */
public class MarkerDatabaseHelper extends SQLiteOpenHelper {
    //Class constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Marker Database";
    private static final String MARKERS_TABLE_NAME = "markers";
    private static final String[] COLUMN_NAMES = { "Latitude", "Longitude", "Title", "Snippet", "Icon", "Date", "Time"};
    private static final String WHERE_EVENTS = "Date != 'NULL' AND Time != 'NULL'";
    private static final String WHERE_MESSAGES = " Date = 'NULL' AND Time = 'NULL'";

    //create table string
    private static final String CREATE_MARKER_TABLE =
            "CREATE TABLE " + MARKERS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " REAL, " +
                    COLUMN_NAMES[1] + " REAL, " +
                    COLUMN_NAMES[2] + " TEXT, " +
                    COLUMN_NAMES[3] + " TEXT, " +
                    COLUMN_NAMES[4] + " INT, " +
                    COLUMN_NAMES[5] + " TEXT, " +
                    COLUMN_NAMES[6] + " TEXT);";

    MarkerDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        //create table if it doesn't exist
        db.execSQL(CREATE_MARKER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVerstion, int newVersion)
    {

    }


    //adds passed in marker to DB
    public boolean addMarker(UserMarker marker)
    {
        //set up a content value
        ContentValues row = new ContentValues();
        row.put(COLUMN_NAMES[0], marker.Latitude);
        row.put(COLUMN_NAMES[1], marker.Longitude);
        row.put(COLUMN_NAMES[2], marker.Title);
        row.put(COLUMN_NAMES[3], marker.Snippet);
        row.put(COLUMN_NAMES[4], marker.Icon);
        row.put(COLUMN_NAMES[5], marker.Date);
        row.put(COLUMN_NAMES[6], marker.Time);

        //write to DB then close
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(MARKERS_TABLE_NAME, null, row);
        db.close();

        return result >= 0;

    }

    //gets all markers stored in local DB
    public ArrayList<UserMarker> getAllMarkers()
    {
        //temp array list to hold markers
        ArrayList<UserMarker> markerEntries = new ArrayList<UserMarker>();

        //get database
        SQLiteDatabase db = getWritableDatabase();

        //query db for all markers
        Cursor results = db.query(MARKERS_TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);

        for(int i = 0; i < results.getCount(); i++)
        {
            results.moveToPosition(i);
            //create a UserMarker from row pointed at by cursor and add to markerEntries
            markerEntries.add(new UserMarker(results.getDouble(0), results.getDouble(1), results.getString(2), results.getString(3), results.getInt(4), results.getString(5), results.getString(6)));
        }

        //closes cursor and db
        results.close();
        db.close();

        //returns all markers
        return markerEntries;
    }

    //gets all events from db
    public ArrayList<UserMarker> getAllEvents()
    {
        //temp array list to hold markers
        ArrayList<UserMarker> markerEntries = new ArrayList<UserMarker>();

        //get database
        SQLiteDatabase db = getWritableDatabase();

        //query db for all markers
        Cursor results = db.query(MARKERS_TABLE_NAME, COLUMN_NAMES, WHERE_EVENTS, null, null, null, null, null);

        for(int i = 0; i < results.getCount(); i++)
        {
            results.moveToPosition(i);
            //create a UserMarker from row pointed at by cursor and add to markerEntries
            markerEntries.add(new UserMarker(results.getFloat(0), results.getFloat(1), results.getString(2), results.getString(3), results.getInt(4), results.getString(5), results.getString(6)));
        }

        //closes cursor and db
        results.close();
        db.close();

        //returns all markers
        return markerEntries;
    }

    //gets all messages from db
    public ArrayList<UserMarker> getAllMessages()
    {
        //temp array list to hold markers
        ArrayList<UserMarker> markerEntries = new ArrayList<UserMarker>();

        //get database
        SQLiteDatabase db = getWritableDatabase();

        //query db for all markers
        Cursor results = db.query(MARKERS_TABLE_NAME, COLUMN_NAMES, WHERE_MESSAGES, null, null, null, null, null);

        for(int i = 0; i < results.getCount(); i++)
        {
            results.moveToPosition(i);
            //create a UserMarker from row pointed at by cursor and add to markerEntries
            markerEntries.add(new UserMarker(results.getFloat(0), results.getFloat(1), results.getString(2), results.getString(3), results.getInt(4), results.getString(5), results.getString(6)));
        }

        //closes cursor and db
        results.close();
        db.close();

        //returns all markers
        return markerEntries;
    }

    //clears db and returns how many entries deleted
    public int clearDB()
    {

        //get db
        SQLiteDatabase db = getWritableDatabase();

        //delete all rows
        int rowsDeleted = db.delete(MARKERS_TABLE_NAME, "1", null);

        //close db
        db.close();

        return rowsDeleted;
    }
}
