package com.example.james.mapnotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__screen);

        //sets objects to the correct view
        mainMenu = (Button)findViewById(R.id.mainMenuButton);
        clearDatabase = (Button)findViewById(R.id.clearCache);
        smsOn = (Switch)findViewById(R.id.smsSwitch);
        wifiOnly = (Switch)findViewById(R.id.wifiOnlyButton);

        //sets listner for buttons
        mainMenu.setOnClickListener(this);
        clearDatabase.setOnClickListener(this);
        smsOn.setOnCheckedChangeListener(this);
        wifiOnly.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v)
    {
        //returns user to main menu
        if(v == mainMenu)
        {
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
            if(isChecked)
            {
                //do something
            }
            else
            {
                //do something else
            }
        }

        //if on only allows downloads over wifi
        if(buttonView == wifiOnly)
        {
            if(isChecked)
            {
                //do something
            }
            else
            {
                //do something else
            }
        }
    }
}
