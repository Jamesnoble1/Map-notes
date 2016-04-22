package com.example.james.mapnotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings_Screen extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    Button mainMenu;
    Button clearDatabase;
    Switch smsOn;
    Switch wifiOnly;
    Switch downloadAll;

    Boolean smsVal = true;
    Boolean wifiVal = false;
    Boolean allMarkersVal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__screen);


        //sets values for options
        SharedPreferences prefs = getSharedPreferences("User prefs", 0);
        smsVal = prefs.getBoolean("SMS", true);
        wifiVal = prefs.getBoolean("WIFI", false);
        allMarkersVal = prefs.getBoolean("ALL_MARKERS", true);

        //sets objects to the correct view
        mainMenu = (Button)findViewById(R.id.mainMenuButton);
        clearDatabase = (Button)findViewById(R.id.clearCache);
        smsOn = (Switch)findViewById(R.id.smsSwitch);
        wifiOnly = (Switch)findViewById(R.id.wifiOnlyButton);
        downloadAll = (Switch)findViewById(R.id.downloadAllSwitch);

        //sets listner for buttons
        mainMenu.setOnClickListener(this);
        clearDatabase.setOnClickListener(this);
        smsOn.setOnCheckedChangeListener(this);
        wifiOnly.setOnCheckedChangeListener(this);
        downloadAll.setOnCheckedChangeListener(this);



        //sets defaults for buttons
        smsOn.setChecked(smsVal);
        wifiOnly.setChecked(wifiVal);
        downloadAll.setChecked(allMarkersVal);
    }

    @Override
    public void onClick(View v)
    {
        //returns user to main menu
        if(v == mainMenu)
        {
            saveSettings();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        //button to clear all local db entries
        if(v == clearDatabase)
        {
            //clear all entries in local db
            BackgroundClearDB clearDB = new BackgroundClearDB(getApplicationContext());
            clearDB.execute();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        //turns sending SMS messages on and off
        if(buttonView == smsOn)
        {

                smsVal = isChecked;
        }

        //if on only allows downloads over wifi
        if(buttonView == wifiOnly)
        {

                wifiVal = isChecked;
        }

        //if download all markers
        if(buttonView == downloadAll)
        {
            allMarkersVal = isChecked;
        }
    }

    private void saveSettings()
    {
        //get preferances
        SharedPreferences prefs = getSharedPreferences("User prefs", 0);

        //get editor
        SharedPreferences.Editor editor = prefs.edit();

        //store values
        editor.putBoolean("SMS", smsVal);
        editor.putBoolean("WIFI", wifiVal);
        editor.putBoolean("ALL_MARKERS", allMarkersVal);

        //save changes. using apply to save the need for starting a non-ui thread
        editor.apply();
    }
}
