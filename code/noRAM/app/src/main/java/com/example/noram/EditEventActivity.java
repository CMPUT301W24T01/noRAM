package com.example.noram;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Event;
import java.time.LocalDateTime;

public class EditEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

    // Attributes
    private Event event;
    private int year;
    private int month;
    private int day;

    // Constructor
    EditEventActivity(Event event) {
        this.event = event;
    }

    // Listeners
    @Override
    public void pushDate(int year, int month, int day, String tag) {
        this.year = year;
        this.month = month;
        this.day = day;
        new TimePickerFragment().show(getSupportFragmentManager(), tag);
    }

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
