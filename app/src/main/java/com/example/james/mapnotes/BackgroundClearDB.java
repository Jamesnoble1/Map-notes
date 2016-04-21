package com.example.james.mapnotes;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by James on 19/04/2016.
 */
public class BackgroundClearDB extends AsyncTask<Void, Void, String> {

    private MarkerDatabaseHelper dbHelper;
    Context context;
    public BackgroundClearDB(Context c)
    {
        context = c;
        dbHelper = new MarkerDatabaseHelper(context);
    }

    @Override
    protected String doInBackground(Void... params)
    {
        return Integer.toString(dbHelper.clearDB());
    }

    @Override
    protected void onPostExecute(String result)
    {
        Toast.makeText(context, result + " Markers Cleared", Toast.LENGTH_LONG).show();
    }
}
