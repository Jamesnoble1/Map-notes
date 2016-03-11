package com.example.james.mapnotes;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by James on 07/03/2016.
 * Fragment to allow the user to pick a time
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    //callback interface to return result to activity
    public interface TimeChoiceFragmentListener
    {
        void onFinishedTimeEntry(String enteredTime);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        //set current time as defaults
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        setCancelable(false);
        //creates instance of timne picker and returns it
        return new TimePickerDialog(getActivity(), this, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    //listener for when time is set
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        String chosenTime;
        //Converts time picked to a string
        if(minute != 0)
        {
             chosenTime = hourOfDay + ":" + minute;
        }
        else
        {
            chosenTime = hourOfDay + ":" + minute + "0";
        }

        //callback to main activity with string
        TimeChoiceFragmentListener activity = (TimeChoiceFragmentListener)getActivity();
        activity.onFinishedTimeEntry(chosenTime);


        //dismiss fragment
        this.dismiss();
    }
}
