package com.example.james.mapnotes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.Calendar;

/**
 * Created by James on 07/03/2016.
 * Fragment for letting user select a date
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int currentYear;
    int currentMonth;
    int currentDay;
    final Calendar c = Calendar.getInstance();
    //callback interface to return result to activity
    public interface DateChoiceFragmentListener
    {
        void onFinishedDateEntry(String enteredDate);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        setCancelable(false);

        //returns instance of DatePickerDialog
        return new DatePickerDialog(getActivity(), this, currentYear, currentMonth, currentDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        String chosenDate;

        //create a calendar instance to check if chosen date is before current day
        Calendar userSet = Calendar.getInstance();
        userSet.set(year, month, day);

        if(c.before(userSet))
        {
            //construct a string out of date chosen
            chosenDate = year + "/" + (month + 1) + "/" + day;
        }
        else
        {
            chosenDate = "INVALID";
        }

        //callback to main activity with string
        DateChoiceFragmentListener activity = (DateChoiceFragmentListener)getActivity();
        activity.onFinishedDateEntry(chosenDate);

        //dismiss fragment
        dismiss();
    }
}
