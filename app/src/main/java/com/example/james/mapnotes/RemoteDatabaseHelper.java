package com.example.james.mapnotes;

/**
 * Created by James on 20/04/2016.
 */
public class RemoteDatabaseHelper {

    //url strings
    private final String mainURL = "http://http://mayar.abertay.ac.uk/~1203172/";
    private final String insertURL = mainURL + "insert.php";
    private final String getAllURL = mainURL + "getfulllist.php";

    public RemoteDatabaseHelper()
    {

    }

    //intended to be used in background write to DB or other ASYNC task
    public boolean insertRow(UserMarker marker)
    {

        return true;
    }
}
