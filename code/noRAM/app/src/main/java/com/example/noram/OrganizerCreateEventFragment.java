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
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerCreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerCreateEventFragment extends Fragment implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {

    // Attributes
    int year;
    int month;
    int day;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    public OrganizerCreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrganizerCreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerCreateEventFragment newInstance() {
        OrganizerCreateEventFragment fragment = new OrganizerCreateEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Initialize fragment
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_organizer_create_event, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_organizer_create_event, container, false);
        return root;
    }

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
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getChildFragmentManager(), "start");
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getChildFragmentManager(), "end");
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
                else if (startTime == null) {errorText = "Start Time";}
                else if (endTime == null) {errorText = "End Time";}
                else if (!isValidMilestoneList(milestonesString)) {errorText = "Milestone";}

                // Only continue if inputs are valid
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
            @Override
            public void onClick(View v) {

            }
        });
    }

    // Listeners
    @Override
    public void pushDate(int year, int month, int day, String tag) {
        this.year = year;
        this.month = month;
        this.day = day;
        new TimePickerFragment().show(getChildFragmentManager(), tag);
    }

    @Override
    public void pushTime(int hour, int minute, String tag) {
        LocalDateTime dateTime = LocalDateTime.of(this.year, this.month, this.day, hour, minute);
        if (tag.equals("start")) {
            startTime = dateTime;
        }
        else {
            endTime = dateTime;
        }
    }

    // Helper functions
    /**
     * Checks whether or not inputted string is valid comma-separated numbers
     * @param milestones Inputted string to check
     * @return true if all characters in milestones is digit or comma, false otherwise
     */
    private boolean isValidMilestoneList(String milestones) {
        if (milestones.isEmpty()) {
            return true;
        }
        else {
            for (char c : milestones.toCharArray()) {
                if (!(Character.isDigit(c) || c == ',')) {
                    return false;
                }
            }
            return true;
        }
    }


}