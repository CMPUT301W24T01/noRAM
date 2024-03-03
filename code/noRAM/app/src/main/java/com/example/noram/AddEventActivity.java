package com.example.noram;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.noram.model.Event;
import com.example.noram.model.Photo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Activity for adding an event
 */
public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

    private Event event;

    // Attributes of Event
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Attributes of date
    int year;
    int month;
    int day;
    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: we should either a) put this into a fragment or b) improve the integration of this with the OrganizerActivity

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
        Button nextButton = findViewById(R.id.event_add_p1_next_button);

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

        // TODO: this button should only appear/work if fields are valid
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEventActivity.this, AddEventQROptionsActivity.class);
                Bundle bundle = new Bundle();
                List<Integer> milestones = Stream.of(
                        editMilestones.getText().toString().split(","))
                        .mapToInt(Integer::parseInt)
                        .boxed()
                        .collect(Collectors.toList()
                );

                // Put all relevant fields into a bundle. The next activity will construct the
                // event from the bundle, and push it to the DB.
                bundle.putString("name", editName.getText().toString());
                bundle.putString("location", editLocation.getText().toString());
                bundle.putString("details", editDetails.getText().toString());
                bundle.putIntegerArrayList("milestones", new ArrayList<>(milestones));
                bundle.putSerializable("startTime", startTime);
                bundle.putSerializable("endTime", endTime);
                bundle.putBoolean("trackLocation", trackLocationCheck.isChecked());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

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
}
