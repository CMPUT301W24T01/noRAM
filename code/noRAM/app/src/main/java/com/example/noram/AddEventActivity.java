package com.example.noram;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.noram.model.Photo;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

//    @Override
//    public void pushTimeAndDate(int year, int month, int day, int hour, int minute)  {
//        startTime = LocalDateTime.of(year, month, day, hour, minute);
//    }

    // Listeners
    @Override
    public void pushDate(int year, int month, int day, String tag) {
        this.year = year;
        this.month = month;
        this.day = day;
        new TimePickerFragment().show(getSupportFragmentManager(), tag);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void pushTime(int hour, int minute, String tag) {
        if (tag.equals("start")) {
            startTime = LocalDateTime.of(year, month, day, hour, minute);
            Log.d("Start Time", String.format("%d %d %d %d %d", year, month, day, hour, minute));
        }
        else {
            endTime = LocalDateTime.of(year, month, day, hour, minute);
            Log.d("End Time", String.format("%d %d %d %d %d", year, month, day, hour, minute));
        }
    }

    // Attributes of Event
    private int id;
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;
    private ArrayList<Integer> milestones;
    private Photo poster;
    private Photo checkInQR;
    private Photo promoQR;
    private boolean trackLocation;

    // Attributes of date
    int year;
    int month;
    int day;
    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add_p1);

        // Find views
        TextView editName = findViewById(R.id.event_add_p1_edit_name_text);
        TextView editLocation = findViewById(R.id.event_add_p1_edit_location_text);
        AppCompatButton editStartTime = findViewById(R.id.event_add_p1_edit_startTime_button);
        AppCompatButton editEndTime = findViewById(R.id.event_add_p1_edit_endTime_button);
        TextView editDetails = findViewById(R.id.event_add_p1_edit_details_text);
        TextView editMilestones  = findViewById(R.id.event_add_p1_edit_milestones_text);
        AppCompatButton uploadPosterButton = findViewById(R.id.event_add_p1_upload_poster_button);
        CheckBox trackLocationCheck = findViewById(R.id.event_add_p1_trackLocation_check);

        // Set on-click listeners for buttons
        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getSupportFragmentManager(), "start");
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getSupportFragmentManager(), "end");
            }
        });

    }
}
