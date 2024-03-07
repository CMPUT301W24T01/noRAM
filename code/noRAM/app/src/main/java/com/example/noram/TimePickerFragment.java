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
 * Fragment for time picker
 * Allows user to select time
 * Passes selected time to parent activity/fragment
 * A {@link DialogFragment} subclass.
 * @see TimePickerDialog
 * @maintainer Carlin
 * @author Carlin
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    // Attributes
    int hour;
    int minute;

    // Constructors
    /**
     * Default constructor
     */
    public TimePickerFragment() {
        setDefaults();
    }

    /**
     * Constructor with inputted date
     * Values can be negative to represent not being set
     * Inputted times are used as default display
     * @param hour number of hour
     * @param minute number of minute
     */
    public TimePickerFragment (int hour, int minute) {
        if (hour < 0 || minute < 0) {
            setDefaults();
        }
        else {
            this.hour = hour;
            this.minute = minute;
        }
    }

    /**
     * Sets the default displayed values to current time
     */
    private void setDefaults() {
        // Set default time
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
    }

    // Listener functionality
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

    // Main behaviour
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
     * @return the dialog to be displayed
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, hour, minute,
//                DateFormat.is24HourFormat(getActivity())
                true);
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
