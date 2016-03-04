package com.example.james.mapnotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings_Screen extends Activity implements View.OnClickListener{

    Button mainMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__screen);

        //sets objects to the correct view
        mainMenu = (Button)findViewById(R.id.mainMenuButton);

        //sets listner for buttons
        mainMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v == mainMenu)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
