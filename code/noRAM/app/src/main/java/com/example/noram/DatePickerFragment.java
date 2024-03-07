package com.example.noram;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Fragment for selecting date
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Attributes
    int year;
    int month;
    int day;

    // Constructors
    /**
     * Default constructor
     */
    public DatePickerFragment () {
        setDefaults();
    }

    /**
     * Constructor with inputted date
     * Values can be negative to represent not being set
     * Inputted dates are used as default display
     * @param year number of year
     * @param month number of month
     * @param day number of day
     */
    public DatePickerFragment (int year, int month, int day) {
        if (year < 0 || month < 0 || day < 0) {
            setDefaults();
        }
        else {
            this.year = year;
            this.month = month - 1;
            this.day = day;
        }
    }

    /**
     * Set the default displayed values to current date
     */
    private void setDefaults() {
        Calendar c = Calendar.getInstance();
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH);
        this.day = c.get(Calendar.DAY_OF_MONTH);
    }

    // Listener functionality
    /**
     * Interface to be implemented by parent activity/fragment
     */
    public interface DatePickerDialogListener {
        /**
         * Pass date information to parent
         * @param year number of year selected
         * @param month number of month selected
         * @param day number of dat selected
         * @param tag denotes which date is being set
         */
        void pushDate(int year, int month, int day, String tag);
    }
    private DatePickerDialogListener listener;

    // Main behaviour
    /**
     * Set listener upon attachment to context
     * @param context parent activity of fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialogListener) {
            listener = (DatePickerDialogListener) context;
        }
        else if (getParentFragment() != null) {
            listener = (DatePickerFragment.DatePickerDialogListener) getParentFragment();
        }
        else {
            throw new RuntimeException(context + " must implement DatePickerDialogListener");
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
        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    /**
     * Upon date being set, pass data to parent
     * @param view the picker associated with the dialog
     * @param year the selected year
     * @param month the selected month (0-11 for compatibility with
     *              {@link Calendar#MONTH})
     * @param day the selected day of the month (1-31, depending on
     *                   month)
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date the user picks.
        listener.pushDate(year, month + 1, day, getTag());
    }
}

