package com.example.james.mapnotes;

/**
 * Created by James on 19/04/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;



/**
 * Created by James on 19/04/2016.
 */
public class BackgroundWriteToDB extends AsyncTask<UserMarker, Void, String> {

    Context context;
    MarkerDatabaseHelper mDBHelper;
    RemoteDatabaseHelper mRemoteHelper;

    public BackgroundWriteToDB(Context c, MarkerDatabaseHelper dbHelper)
    {
        context = c;
        mDBHelper = dbHelper;
        mRemoteHelper = new RemoteDatabaseHelper();
    }

    @Override
    protected String doInBackground(UserMarker... params)
    {
        mRemoteHelper.insertRow(params[0]);

        if(mDBHelper.addMarker(params[0]))
        {
            return "Saved";
        }
        else
        {
            return "Failed";
        }

    }

    @Override
    protected void onPostExecute(String result)
    {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}
