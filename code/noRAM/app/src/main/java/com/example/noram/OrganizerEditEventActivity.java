package com.example.noram;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Event;
import java.time.LocalDateTime;

/**
 * Activity for editing a pre-existing event
 */
public class OrganizerEditEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {
    // TODO finish implementation

    // Attributes
    private Event event;
    private int year;
    private int month;
    private int day;

    // Constructor
    /**
     * Constructor
     * @param event Event object to be edited
     */
    OrganizerEditEventActivity(Event event) {
        this.event = event;
    }

    // Listeners
    /**
     * Function from interface of DatePickerFragment
     * Receives date information from DatePickerFragment
     * Calls TimePickerFragment
     * @param year number of selected year
     * @param month number of selected month
     * @param day number of selected day
     * @param tag either "start" or "end" to indicate which datetime to set
     */
    @Override
    public void pushDate(int year, int month, int day, String tag) {
        this.year = year;
        this.month = month;
        this.day = day;
        new TimePickerFragment().show(getSupportFragmentManager(), tag);
    }

    /**
     * Function from interface of TimePickerFragment
     * Receives date information from TimePickerFragment
     * Combines with previous data from DatePickerFragment to make LocalDateTime
     * @param hour number of selected hour
     * @param minute number of selected minute
     * @param tag either "start" or "end" to indicate which datetime to set
     */
    @Override
    public void pushTime(int hour, int minute, String tag) {
        if (tag.equals("start")) {
            event.setStartTime(LocalDateTime.of(year, month, day, hour, minute));
        }
        else {
            event.setEndTime(LocalDateTime.of(year, month, day, hour, minute));
        }
    }

}
