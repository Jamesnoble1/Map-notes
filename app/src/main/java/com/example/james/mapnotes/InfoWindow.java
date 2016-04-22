package com.example.james.mapnotes;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by James on 04/03/2016.
 * Sets up and displays custom view for user messages
 * to be displayed on google maps
 */
public class InfoWindow implements GoogleMap.InfoWindowAdapter{
    private View markerView;

    InfoWindow(Activity mapActivity)
    {
        markerView = mapActivity.getLayoutInflater().inflate(R.layout.custom_info_window, null);
    }

    //sets views on marker info window
    public View getInfoWindow(Marker marker)
    {
        TextView messageTitle = (TextView) markerView.findViewById(R.id.titleBar);
        TextView  messageContent = (TextView) markerView.findViewById(R.id.userMessage);

        messageTitle.setText(marker.getTitle());
        messageContent.setText(marker.getSnippet());
        return markerView;
    }

    //always return null as returning a custom view
    public View getInfoContents(Marker marker)
    {
        return null;
    }


}
