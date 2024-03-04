package com.example.noram;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    // Listener interface
    public interface TimePickerDialogListener {
        void pushTime(int hour, int minute, String tag);
    }
    private TimePickerFragment.TimePickerDialogListener listener;

    // Constructors
//    TimePickerFragment () {}
//    TimePickerFragment(Fragment fragment) {
//        listener = (TimePickerFragment.TimePickerDialogListener) fragment;
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TimePickerFragment.TimePickerDialogListener) {
            listener = (TimePickerFragment.TimePickerDialogListener) context;
        }
        else if (getParentFragment() != null) {
            listener = (TimePickerFragment.TimePickerDialogListener) getParentFragment();
        }
        else {
            throw new RuntimeException(context + " must implement TimePickerDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        // Do something with the time the user picks.
        listener.pushTime(hour, minute, getTag());

    }
}
