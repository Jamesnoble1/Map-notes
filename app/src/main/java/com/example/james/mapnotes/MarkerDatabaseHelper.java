package com.example.james.mapnotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;

/**
 * Created by James on 06/03/2016.
 * Database helper to handle communication with local database
 */
public class MarkerDatabaseHelper extends SQLiteOpenHelper {
    //Class constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Marker Database";
    private static final String MARKERS_TABLE_NAME = "markers";
    private static final String[] COLUMN_NAMES = {"Marker Number", "Latitude", "Longitude", "Title", "Snippet", "Icon", "Date", "Time"};

    //create table string
    private static final String CREATE_MARKER_TABLE =
            "CREATE TABLE " + MARKERS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " INT, " +
                    COLUMN_NAMES[1] + " REAL, " +
                    COLUMN_NAMES[2] + " REAL, " +
                    COLUMN_NAMES[3] + " TEXT, " +
                    COLUMN_NAMES[4] + " TEXT, " +
                    COLUMN_NAMES[5] + " INT, " +
                    COLUMN_NAMES[6] + " TEXT, " +
                    COLUMN_NAMES[7] + " TEXT);";

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
}
