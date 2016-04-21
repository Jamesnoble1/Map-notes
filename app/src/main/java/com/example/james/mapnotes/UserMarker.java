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
    public int Icon; //user selected icon
    public double Latitude; //Marker lat
    public double Longitude; //marker long

    public UserMarker( double lat, double longitude ,String title, String snippet, int icon, String date, String time )
    {
        Title = title;
        Snippet = snippet;
        Date = date;
        Time = time;

        Icon = icon;
        Latitude = lat;
        Longitude = longitude;
    }
}
