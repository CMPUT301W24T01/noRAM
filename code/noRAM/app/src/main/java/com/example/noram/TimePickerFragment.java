package com.example.noram;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Fragment for selecting time
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    /**
     * Interface to be implemented by parent activity/fragment
     */
    public interface TimePickerDialogListener {
        /**
         * Pass time information to parent
         * @param hour number of hour selected
         * @param minute number of minute selected
         * @param tag denotes which date is being set
         */
        void pushTime(int hour, int minute, String tag);
    }
    private TimePickerFragment.TimePickerDialogListener listener;

    /**
     * Set listener upon attachment to context
     * @param context parent activity of fragment
     */
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

    /**
     * Instantiates and returns a Dialog class for fragment
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     * @return
     */
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

    /**
     * Upon time being set, pass data to parent
     * @param view the view associated with this listener
     * @param hour the hour that was set
     * @param minute the minute that was set
     */
    public void onTimeSet(TimePicker view, int hour, int minute) {
        // Do something with the time the user picks.
        listener.pushTime(hour, minute, getTag());

    }
}
