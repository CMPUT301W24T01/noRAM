package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Activity for adding an event
 */
public class AddEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

    // Attributes
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int year;
    private int month;
    private int day;

    /**
     * Establishes behaviour of activity
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: we should either a) put this into a fragment or b) improve the integration of this with the OrganizerActivity

        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_organizer_create_event);

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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get inputs
                String name = editName.getText().toString();
                String location = editLocation.getText().toString();
                // startTime
                // endTime
                String details = editDetails.getText().toString();
                String milestonesString = editMilestones.getText().toString();
                boolean trackLocation = trackLocationCheck.isChecked();

                // Check inputs
                String errorText = null;
                if (name.isEmpty()) {errorText = "Name";}
                else if (location.isEmpty()) {errorText = "Location";}
                else if (!milestonesString.isEmpty() || !isValidMilestoneList(milestonesString)) {errorText = "Milestone";}

                // Only continue if inputs are valid
                if (errorText == null) {
                    Intent intent = new Intent(AddEventActivity.this, AddEventQROptionsActivity.class);
                    Bundle bundle = new Bundle();
                    List<Integer> milestones = Stream.of(
                            milestonesString.split(","))
                            .mapToInt(Integer::parseInt)
                            .boxed()
                            .collect(Collectors.toList()
                            );

                    // Put all relevant fields into a bundle. The next activity will construct the
                    // event from the bundle, and push it to the DB.
                    bundle.putString("name", name);
                    bundle.putString("location", location);
                    bundle.putString("details", details);
                    bundle.putIntegerArrayList("milestones", new ArrayList<>(milestones));
                    bundle.putSerializable("startTime", startTime);
                    bundle.putSerializable("endTime", endTime);
                    bundle.putBoolean("trackLocation", trackLocation);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                // Otherwise, show error Toast
                else {
                    Toast.makeText(getBaseContext(), String.format("%s is invalid", errorText), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO: implement poster upload
        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // Listeners

    /**
     * Implemented function to extract data from DatePickerFragment
     * Calls TimePickerFragment
     * @param year integer representing year
     * @param month integer representing month
     * @param day integer representing day
     * @param tag denotes whether setting start or end datetime
     */
    @Override
    public void pushDate(int year, int month, int day, String tag) {
        this.year = year;
        this.month = month;
        this.day = day;
        new TimePickerFragment().show(getSupportFragmentManager(), tag);
    }

    /**
     * Implemented function to extract data from
     * @param hour integer representing hour
     * @param minute integer representing minute
     * @param tag denotes whether setting start or end datetime
     */
    @Override
    public void pushTime(int hour, int minute, String tag) {
        if (tag.equals("start")) {
            startTime = LocalDateTime.of(year, month, day, hour, minute);
        }
        else {
            endTime = LocalDateTime.of(year, month, day, hour, minute);
        }
    }

    // Helper functions

    /**
     * Checks whether or not inputted string is valid comma-separated numbers
     * @param milestones Inputted string to check
     * @return true if all characters in milestones is digit or comma, false otherwise
     */
    private boolean isValidMilestoneList(String milestones) {
        for (char c : milestones.toCharArray()) {
            if (!(Character.isDigit(c) || c != ',')) {
                return false;
            }
        }
        return true;
    }
}


