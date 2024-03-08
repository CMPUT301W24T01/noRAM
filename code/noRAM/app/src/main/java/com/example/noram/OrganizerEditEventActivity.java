/*
This file is used to edit the event details for the organizer and update the event in the database.
Outstanding Issues:
- Download photo from cloud storage to preview event poster
 */

package com.example.noram;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Activity for organizer to edit event
 * Allows organizer to edit event attributes
 * Allows organizer to upload event poster
 * A {@link AppCompatActivity} subclass.
 * @see DatePickerFragment
 * @see TimePickerFragment
 * @maintainer Carlin
 * @author Carlin
 * @maintainer Carlin
 */
public class OrganizerEditEventActivity extends AppCompatActivity implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

    // Attributes
    Event event = new Event();
    int startYear;
    int startMonth;
    int startDay;
    int startHour;
    int startMinute;
    int endYear;
    int endMonth;
    int endDay;
    int endHour;
    int endMinute;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private AppCompatButton applyButton;
    private AppCompatButton cancelButton;
    private TextView editName;
    private TextView editLocation;
    private AppCompatButton editStartDateTime;
    private AppCompatButton editEndDateTime;
    private TextView editDetails;
    private TextView editMilestones;
    private AppCompatButton uploadPosterButton;
    private CheckBox trackLocationCheck;

    // Main behaviour
    /**
     * Initialize the activity
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @throws RuntimeException if eventID is not provided in intent or is invalid
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_edit_event);

        // Ensure intent contains event
        Intent intent = getIntent();
        if (intent.hasExtra("event")) {
            String eventID = intent.getExtras().getString("event");
            assert(eventID != null);
            Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventID).get();
            task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                /**
                 * On successful acquisition of event attributes from database, create event
                 * Set dateTime attributes with event attributes
                 * Update text of views with event attributes
                 * @param documentSnapshot database object from which object is initialized
                 */
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    // Make event
                    event.updateWithDocument(documentSnapshot);

                    // Set startDateTime self and attributes
                    startDateTime = event.getStartTime();
                    startYear = startDateTime.getYear();
                    startMonth = startDateTime.getMonthValue();
                    startDay = startDateTime.getDayOfMonth();
                    startHour = startDateTime.getHour();
                    startMinute = startDateTime.getMinute();

                    // Set endTimeDate self and attributes
                    endDateTime = event.getEndTime();
                    endYear = endDateTime.getYear();
                    endMonth = endDateTime.getMonthValue();
                    endDay = endDateTime.getDayOfMonth();
                    endHour = endDateTime.getHour();
                    endMinute = endDateTime.getMinute();

                    // Update text displays with existing event attributes
                    editName.setText(event.getName());
                    editLocation.setText(event.getLocation());
                    editStartDateTime.setText(
                            String.format(
                                    getBaseContext().getString(R.string.organizer_fragment_create_event_p1_startTime_set),
                                    startMonth, startDay, startYear, startHour, startMinute
                            )
                    );
                    editEndDateTime.setText(
                            String.format(
                                    getBaseContext().getString(R.string.organizer_fragment_create_event_p1_endTime_set),
                                    endMonth, endDay, endYear, endHour, endMinute
                            )
                    );
                    editDetails.setText(event.getDetails());
                    editMilestones.setText(event.getMilestones().toString().replaceAll("[\\[\\]]", ""));
                    trackLocationCheck.setChecked(event.isTrackLocation());
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                /**
                 * Failure as a result of an invalid event ID
                 * @param e cause of failure
                 */
                @Override
                public void onFailure(@NonNull Exception e) {
                    throw new RuntimeException(e + ": Invalid Event ID passed");
                }
            });
        }
        // Otherwise, throw RunTime error
        else {
            throw new RuntimeException("Must pass Event with key \"event\" using intent.putExtra(\"event\", eventIDAsString);");
        }

        // Find views
        applyButton = findViewById(R.id.organizer_activity_edit_event_apply_button);
        cancelButton = findViewById(R.id.organizer_activity_edit_event_cancel_button);
        editName = findViewById(R.id.organizer_activity_edit_event_edit_name_text);
        editLocation = findViewById(R.id.organizer_activity_edit_event_edit_location_text);
        editStartDateTime = findViewById(R.id.organizer_activity_edit_event_edit_startDateTime_button);
        editEndDateTime = findViewById(R.id.organizer_activity_edit_event_edit_endDateTime_button);
        editDetails = findViewById(R.id.organizer_activity_edit_event_edit_details_text);
        editMilestones  = findViewById(R.id.organizer_activity_edit_event_edit_milestones_text);
        uploadPosterButton = findViewById(R.id.organizer_activity_edit_event_edit_upload_poster_button);
        trackLocationCheck = findViewById(R.id.organizer_activity_edit_event_edit_trackLocation_check);

        // Set on-click listeners for buttons
        editStartDateTime.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Start Data/Time button
             * Calls Date and Time picker fragments
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new DatePickerFragment(startYear, startMonth, startDay).show(getSupportFragmentManager(), "start");
            }
        });

        editEndDateTime.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for End Data/Time button
             * Calls DatePickerFragment
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new DatePickerFragment(endYear, endMonth, endDay).show(getSupportFragmentManager(), "end");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Cancel button
             * Aborts changes to events
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for apply button
             * Applies changes to events
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                // Get inputs
                String name = editName.getText().toString();
                String location = editLocation.getText().toString();
                // startTime already got
                // endTime already got
                String details = editDetails.getText().toString();
                String milestonesString = editMilestones.getText().toString();
                boolean trackLocation = trackLocationCheck.isChecked();

                // Check inputs
                String errorText = null;
                if (name.isEmpty()) {errorText = "Name";}
                else if (location.isEmpty()) {errorText = "Location";}
                else if (startDateTime == null) {errorText = "Start Time";}
                else if (endDateTime == null) {errorText = "End Time";}
                else if (!isValidMilestoneList(milestonesString)) {errorText = "Milestone";}

                // Only continue to next step of event creation if inputs are valid
                if (errorText == null) {

                    // Make milestones list
                    List<Integer> milestones;
                    if (!milestonesString.isEmpty()) {
                        milestones = Stream.of(milestonesString.split(","))
                                .mapToInt(Integer::parseInt)
                                .boxed()
                                .collect(Collectors.toList()
                                );
                    }
                    else {
                        milestones = new ArrayList<>();
                    }

                    // Update event attributes
                    event.setName(name);
                    event.setLocation(location);
                    event.setStartTime(startDateTime);
                    event.setEndTime(endDateTime);
                    event.setDetails(details);
                    event.setMilestones(new ArrayList<>(milestones));
                    event.setTrackLocation(trackLocation);

                    // Update event in database
                    event.updateDBEvent();
                    finish();
                }

                // Otherwise, show error Toast
                else {
                    Toast.makeText(getBaseContext(), String.format("%s is invalid", errorText), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO: implement poster upload

        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for poster upload button
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

            }
        });

    }

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
        if (tag.equals("start")) {

            // Set start date attributes
            startYear = year;
            startMonth = month;
            startDay = day;

            // Call timepicker
            new TimePickerFragment(startHour, startMinute).show(getSupportFragmentManager(), tag);
        }
        else {

            // Set end date attributes
            endYear = year;
            endMonth = month;
            endDay = day;

            // Call timepicker
            new TimePickerFragment(endHour, endMinute).show(getSupportFragmentManager(), tag);
        }
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
            // Set start time attributes
            startHour = hour;
            startMinute = minute;

            // Set startDateTime
            startDateTime = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);

            // Update button text with set startDateTime
            editStartDateTime.setText(
                    String.format(
                            getBaseContext().getString(R.string.organizer_fragment_create_event_p1_startTime_set),
                            startMonth, startDay, startYear, startHour, startMinute
                    )
            );
        }
        else {
            // Set end time attributes
            endHour = hour;
            endMinute = minute;

            // Set endDateTime
            endDateTime = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute);

            // Update button text with set endDateTime
            editEndDateTime.setText(
                    String.format(
                            getBaseContext().getString(R.string.organizer_fragment_create_event_p1_endTime_set),
                            endMonth, endDay, endYear, endHour, endMinute
                    )
            );
        }
    }

    // Helper functions
    /**
     * Checks whether or not inputted string is valid comma-separated numbers
     * @param milestones Inputted string to check
     * @return true if all characters in milestones is digit or comma, false otherwise
     */
    private boolean isValidMilestoneList(String milestones) {
        if (!milestones.isEmpty()) {
            for (char c : milestones.toCharArray()) {
                if (!(Character.isDigit(c) || c == ',')) {
                    return false;
                }
            }
        }
        return true;
    }

}
