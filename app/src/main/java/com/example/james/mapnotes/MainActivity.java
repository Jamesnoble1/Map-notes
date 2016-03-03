package com.example.james.mapnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button viewMap, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assigns views to objects in code
        viewMap = (Button)findViewById(R.id.viewMapButton);
        settingsButton = (Button)findViewById(R.id.settingsButton);

        //setting on click listeners
        viewMap.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        //changes current activity to settings screen
        if(v == settingsButton)
        {
            Intent intent = new Intent((getApplicationContext()), Settings_Screen.class);
            startActivity(intent);
        }

        //changes current activity to viewing the map
        if(v == viewMap)
        {
            //do something
        }
    }
}
