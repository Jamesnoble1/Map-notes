package com.example.james.mapnotes;

import android.content.Context;

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

}
