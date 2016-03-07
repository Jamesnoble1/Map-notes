package com.example.james.mapnotes;

/**
 * Created by James on 06/03/2016.
 * Object to hold data about user markers
 */
public class UserMarker {
    /*"Marker Number", "Latitude", "Longitude", "Title", "Snippet", "Icon", "Date", "Time"
      " INT, "          " REAL,      " REAL, " " TEXT, " " TEXT, ""  INT,   " TEXT, " TEXT*/
    public String Title; //Marker title
    public String Snippet; //MArker Snippet
    public String Date; //Event date if applicable
    public String Time; //Event time if applicable
    public int markerNumber; //Marker identifier
    public int Icon; //user selected icon
    public float Latitude; //Marker lat
    public float Longitude; //marker long

    public UserMarker(String title, String snippet, String date, String time, int mNumber, int icon, float lat, float longitude )
    {
        Title = title;
        Snippet = snippet;
        Date = date;
        Time = time;
        markerNumber = mNumber;
        Icon = icon;
        Latitude = lat;
        Longitude = longitude;
    }
}
