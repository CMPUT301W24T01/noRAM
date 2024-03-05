package com.example.noram;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fragment for creating event as organizer
 */
public class OrganizerCreateEventFragment extends Fragment implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

    // Attributes
    int startYear;
    int startMonth;
    int startDay;
    int endYear;
    int endMonth;
    int endDay;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Default constructor
     */
    public OrganizerCreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment OrganizerCreateEventFragment.
     */
    public static OrganizerCreateEventFragment newInstance() {
        return new OrganizerCreateEventFragment();
    }

    /**
     * Instantiate user interface view of fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return The root View of the inflated hierarchy
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer_create_event, container, false);
    }

    /**
     * Initialize fragment upon complete creation of view hierarchy
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initialize fragment upon display
        super.onViewCreated(view, savedInstanceState);

        // Set header
        TextView header = view.findViewById(R.id.event_add_p1_header);
        header.setText(R.string.event_add_p1_header_text);

        // Find views
        TextView editName = view.findViewById(R.id.event_add_p1_edit_name_text);
        TextView editLocation = view.findViewById(R.id.event_add_p1_edit_location_text);
        AppCompatButton editStartTime = view.findViewById(R.id.event_add_p1_edit_startTime_button);
        AppCompatButton editEndTime = view.findViewById(R.id.event_add_p1_edit_endTime_button);
        TextView editDetails = view.findViewById(R.id.event_add_p1_edit_details_text);
        TextView editMilestones  = view.findViewById(R.id.event_add_p1_edit_milestones_text);
        AppCompatButton uploadPosterButton = view.findViewById(R.id.event_add_p1_upload_poster_button);
        CheckBox trackLocationCheck = view.findViewById(R.id.event_add_p1_trackLocation_check);
        Button nextButton = view.findViewById(R.id.event_add_p1_next_button);

        // Set on-click listeners for buttons
        editStartTime.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Start Data/Time button
             * Calls Date and Time picker fragments
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getChildFragmentManager(), "start");
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for End Data/Time button
             * Calls DatePickerFragment
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getChildFragmentManager(), "end");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for next button
             * Checks inputted values for proper inputs before proceeding with event creation
             * @param v The view that was clicked.
             */
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
                else if (startTime == null) {errorText = "Start Time";}
                else if (endTime == null) {errorText = "End Time";}
                else if (!isValidMilestoneList(milestonesString)) {errorText = "Milestone";}

                // Only continue to next step of event creation if inputs are valid
                if (errorText == null) {
                    Intent intent = new Intent(getActivity(), AddEventQROptionsActivity.class);
                    Bundle bundle = new Bundle();
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
                    Toast.makeText(getContext(), String.format("%s is invalid", errorText), Toast.LENGTH_SHORT).show();
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
        if (tag.equals("start")) {
            startYear = year;
            startMonth = month;
            startDay = day;
        }
        else {
            endYear = year;
            endMonth = month;
            endDay = day;
        }
        new TimePickerFragment().show(getChildFragmentManager(), tag);
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
            startTime = LocalDateTime.of(startYear, startMonth, startDay, hour, minute);
        }
        else {
            endTime = LocalDateTime.of(endYear, endMonth, endDay, hour, minute);
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