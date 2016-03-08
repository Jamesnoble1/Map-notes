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

    //callback interface to return result to activity
    public interface DateChoiceFragmentListener
    {
        void onFinishedDateEntry(String enteredDate);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        setCancelable(false);
        //returns instance of DatePickerDialog
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        //construct a string out of date chosen
        String chosenDate = year + "/" + (month + 1) + "/" + day;

        //callback to main activity with string
        DateChoiceFragmentListener activity = (DateChoiceFragmentListener)getActivity();
        activity.onFinishedDateEntry(chosenDate);

        //dismiss fragment
        dismiss();
    }
}
