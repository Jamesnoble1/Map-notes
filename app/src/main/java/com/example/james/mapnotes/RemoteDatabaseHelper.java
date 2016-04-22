package com.example.james.mapnotes;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by James on 20/04/2016.
 * Based on helper class example provided by Andrei Boiko for CE0942A
 */
public class RemoteDatabaseHelper {

    //url strings
    private final String mainURL = "http://mayar.abertay.ac.uk/~1203172/";
    private final String insertURL = mainURL + "insert.php";
    private final String getAllURL = mainURL + "getfulllist";
    private final String getLocalURL = mainURL + "get_close_markers.php";

    private HttpClient httpClient = new DefaultHttpClient();

    public RemoteDatabaseHelper()
    {

    }

    public void getAllMarkers(MapDisplay activity)
    {
        //if download all markers download all
        getAllRemoteMarkers getMarkers = new getAllRemoteMarkers(activity);
        getMarkers.execute();
    }

    public void getLocalMarkers(MapDisplay activity, double lat, double lng)
    {
        //if download all markers download all
        getAllCloseMarkers getMarkers = new getAllCloseMarkers(activity, lat, lng);
        getMarkers.execute();
    }
    //intended to be used in background write to DB or other ASYNC task
    public boolean insertRow(UserMarker marker)
    {
        //create objects needed
        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>(7);
        HttpPost httpPost = new HttpPost(insertURL);

        data.add(new BasicNameValuePair("Latitude", Double.toString(marker.Latitude)));
        data.add(new BasicNameValuePair("Longitude", Double.toString(marker.Longitude)));
        data.add(new BasicNameValuePair("Title", marker.Title));
        data.add(new BasicNameValuePair("Snippet", marker.Snippet));
        data.add(new BasicNameValuePair("Icon", Integer.toString(marker.Icon)));
        data.add(new BasicNameValuePair("Date", marker.Date));
        data.add(new BasicNameValuePair("Time", marker.Time));

        //catch exception if one is thrown. Unlikly but IDE complains
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(data));
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        try
        {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

        }
        catch(java.io.IOException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private class getAllRemoteMarkers extends AsyncTask<Void, Void, Void>
    {
        private MapDisplay activityRef;
        int test = 0;

        getAllRemoteMarkers(MapDisplay activity)
        {
            //gets activity ref so onPostExecute can tell the activity it's done
            activityRef = activity;
        }

        @Override
        public Void doInBackground(Void... params)
        {

            //Set up sending request to server
            //create objects needed
            HttpPost httpPost = new HttpPost(getAllURL);
            String responseString = "";
            MarkerDatabaseHelper db = new MarkerDatabaseHelper(activityRef.getApplicationContext());

            //clear local db
            db.clearDB();

            try
            {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, "UTF-8");
                Log.e("Response:", responseString);
            }
            catch(java.io.IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                JSONArray markerArray = new JSONArray(responseString);


                int arraySize = markerArray.length();
                test = arraySize;
                Log.i("Date", Integer.toString(arraySize));
                for(int i = 0; i < arraySize; i++)
                {

                    //fill in a temp marker
                    JSONObject marker = markerArray.getJSONObject(i);
                    Double Lat = marker.getDouble("Latitude");
                    Double Long = marker.getDouble("Longitude");
                    String Title = marker.getString("Title");
                    String Snippet = marker.getString("Snippet");
                    int Icon = marker.getInt("Icon");
                    String Date = marker.getString("Date");
                    String Time = marker.getString("Time");

                    UserMarker tempMarker = new UserMarker(Lat, Long, Title, Snippet, Icon, Date, Time);

                    //add to DB
                    db.addMarker(tempMarker);
                }
            }
            catch(org.json.JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void v)
        {
            activityRef.mapData.setCurrentTime();
            activityRef.mapData.loadData();
            activityRef.loadMarkers();
        }
    }

    private class getAllCloseMarkers extends AsyncTask<Void, Void, Void>
    {
        private MapDisplay activityRef;
        double latitude = 0;
        double longitude = 0;

        getAllCloseMarkers(MapDisplay activity, Double lat, Double lng)
        {
            //gets activity ref so onPostExecute can tell the activity it's done
            activityRef = activity;

            //set latitude and longitude
            latitude = lat;
            longitude = lng;
        }

        @Override
        public Void doInBackground(Void... params)
        {

            //Set up sending request to server
            //create objects needed
            //create objects needed
            ArrayList<NameValuePair> data = new ArrayList<NameValuePair>(2);
            HttpPost httpPost = new HttpPost(getLocalURL);
            String responseString = "";
            MarkerDatabaseHelper db = new MarkerDatabaseHelper(activityRef.getApplicationContext());
            data.add(new BasicNameValuePair("Latitude", Double.toString(latitude)));
            data.add(new BasicNameValuePair("Longitude", Double.toString(longitude)));
            //clear local db
            db.clearDB();

            //catch exception if one is thrown. Unlikly but IDE complains
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(data));
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            try
            {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, "UTF-8");
                Log.e("Response:", responseString);
            }
            catch(java.io.IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                JSONArray markerArray = new JSONArray(responseString);


                int arraySize = markerArray.length();

                Log.i("Date", Integer.toString(arraySize));
                for(int i = 0; i < arraySize; i++)
                {

                    //fill in a temp marker
                    JSONObject marker = markerArray.getJSONObject(i);
                    Double Lat = marker.getDouble("Latitude");
                    Double Long = marker.getDouble("Longitude");
                    String Title = marker.getString("Title");
                    String Snippet = marker.getString("Snippet");
                    int Icon = marker.getInt("Icon");
                    String Date = marker.getString("Date");
                    String Time = marker.getString("Time");

                    UserMarker tempMarker = new UserMarker(Lat, Long, Title, Snippet, Icon, Date, Time);

                    //add to DB
                    db.addMarker(tempMarker);
                }
            }
            catch(org.json.JSONException e)
            {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        public void onPostExecute(Void v)
        {
            activityRef.mapData.setCurrentTime();
            activityRef.mapData.loadData();
            activityRef.loadMarkers();
        }
    }
}
