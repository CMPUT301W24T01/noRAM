/*
This file is used to create the first fragment of the organizer event creation process.
Outstanding Issues:
- Should collapse the eventphoto box until one is uploaded, move event validation out
 */

package com.example.noram;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerCreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @maintainer Carlin
 * @author Carlin
 * @author Sandra
 * @author Cole
 * @author ethan
 */
public class OrganizerCreateEventFragment extends Fragment implements DatePickerFragment.DatePickerDialogListener, TimePickerFragment.TimePickerDialogListener {
    // Attributes
    private int startYear = -1;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endYear = -1;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;
    private boolean locationIsRealLocation = false;
    private GeoPoint locationGeopoint;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private AppCompatButton editStartDateTime;
    private AppCompatButton editEndDateTime;
    private View createdView;
    private Uri imageUri;
    private FloatingActionButton addPhoto;
    private FloatingActionButton deletePhoto;
    private TextView editName;
    private EditText editLocation;
    private TextView editDetails;
    private TextView editMilestones;
    private CheckBox trackLocationCheck;
    private CheckBox limitSignUpsCheck;
    private TextView editLimitSignUps;
    private ImageView imageView;
    private Button nextButton;
    private ScrollView scroll;
    private ImageView locationPickerButton;

    // Constructors
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

    // Main behaviour
    /**
     * This method is called when the fragment is created.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return inflater.inflate(R.layout.organizer_fragment_create_event_p1, container, false);
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
        createdView = view;

        // Find views
        editName = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_name_text);
        editLocation = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_location_text);
        editStartDateTime = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_startDateTime_button);
        editEndDateTime = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_endDateTime_button);
        editDetails = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_details_text);
        editMilestones  = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_milestones_text);
        trackLocationCheck = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_trackLocation_check);
        limitSignUpsCheck = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_limitSignUps_check);
        editLimitSignUps = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_signUpLimit_text);
        nextButton = view.findViewById(R.id.organizer_fragment_create_event_p1_edit_next_button);
        imageView = view.findViewById(R.id.image_view);
        deletePhoto = view.findViewById(R.id.delete_photo);
        deletePhoto.setVisibility(View.INVISIBLE);
        addPhoto = view.findViewById(R.id.add_photo);
        scroll = view.findViewById(R.id.fragment_organizer_create_event);
        locationPickerButton = view.findViewById(R.id.location_picker_button);

        // set editing the location textbox to change the locationIsRealLocation status to false.
        editLocation.addTextChangedListener(new TextWatcher() {
            /**
             * Unused
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * Unused
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            /**
             * If we manually edit the text in the location box, no longer treat it as a real location
             * @param s unused
             */
            @Override
            public void afterTextChanged(Editable s) {
                locationGeopoint = null;
                locationIsRealLocation = false;
            }
        });

        // Set on-click listeners for buttons
        editStartDateTime.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Start Data/Time button
             * Calls Date and Time picker fragments
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new DatePickerFragment(startYear, startMonth, startDay).show(getChildFragmentManager(), "start");
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
                new DatePickerFragment(endYear, endMonth, endDay).show(getChildFragmentManager(), "end");
            }
        });

        limitSignUpsCheck.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener to display/hide input box for sign-up limit
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if (editLimitSignUps.getVisibility() == View.GONE) {
                    editLimitSignUps.setVisibility(View.VISIBLE);
                }
                else {
                    editLimitSignUps.setVisibility(View.GONE);
                }
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
                // startTime already got
                // endTime already got
                String details = editDetails.getText().toString();
                String milestonesString = editMilestones.getText().toString();
                boolean trackLocation = trackLocationCheck.isChecked();
                Long signUpLimit = -1L;
                String signUpLimitString = editLimitSignUps.getText().toString();
                if (limitSignUpsCheck.isChecked()) {
                    if (!signUpLimitString.isEmpty()) {
                        signUpLimit = Long.parseLong(signUpLimitString);
                    }
                }

                // Check inputs
                Pair<Boolean, String> validateResult = EventValidator.validateFromFields(name, location, startDateTime, endDateTime, milestonesString, signUpLimit, 0);

                // Only continue to next step of event creation if inputs are valid
                if (validateResult.first) {

                    Intent intent = new Intent(getActivity(), AddEventQROptionsActivity.class);
                    Bundle bundle = new Bundle();
                    List<Integer> milestones;
                    if (!milestonesString.isEmpty()) {
                        milestones = Stream.of(milestonesString.split(","))
                                .map(value -> value.replaceAll("\\s+", ""))
                                .filter(value -> !value.isEmpty())
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
                    bundle.putSerializable("startTime", startDateTime);
                    bundle.putSerializable("endTime", endDateTime);
                    bundle.putBoolean("trackLocation", trackLocation);
                    bundle.putParcelable("imageUri", imageUri);
                    bundle.putLong("signUpLimit", signUpLimit);
                    bundle.putLong("lastMilestone", -1L);
                    bundle.putBoolean("locationIsRealLocation", locationIsRealLocation);

                    if (locationGeopoint != null) {
                        bundle.putDouble("lon", locationGeopoint.getLongitude());
                        bundle.putDouble("lat", locationGeopoint.getLatitude());
                    }
                    intent.putExtras(bundle);

                    // move back organizer activity to my_events fragment before launching the
                    // new activity
                    startActivity(intent);
                    ((OrganizerActivity) getActivity()).displayMyEventsFragment();
                    clearFields();
                }
                // Otherwise, show error Toast
                else {
                    Toast.makeText(getContext(), validateResult.second, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set on click listener to add photo when pressed
        addPhoto.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for addPhoto button
             * Calls startImagePicker
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                startImagePicker();
            }
        });

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for deletePhoto button
             * Calls showDeletePhotoConfirmation()
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                showDeletePhotoConfirmation();
            }
        });

        // create a ActivityResultLauncher to get the location from the LocationPickerActivity
        // when we start it
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        String newLocation = bundle.getString("location");
                        double lon = bundle.getDouble("lon");
                        double lat = bundle.getDouble("lat");
                        editLocation.setText(newLocation);
                        locationIsRealLocation = true;
                        locationGeopoint = new GeoPoint(lat, lon);
                    }
                });

        // location picker button to start location picker
        locationPickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
            launcher.launch(intent);
        });

    }

    /**
     * Clears all fields in the fragment and the time info to reset the page
     */
    public void clearFields() {
        editName.setText("");
        editLocation.setText("");
        editStartDateTime.setText("Set Start Date/Time");
        startDateTime = null;
        editEndDateTime.setText("Set End Date/Time");
        endDateTime = null;
        editDetails.setText("");
        editMilestones.setText("");
        trackLocationCheck.setChecked(false);
        limitSignUpsCheck.setChecked(false);
        editLimitSignUps.setText("");
        editLimitSignUps.setVisibility(View.GONE);
        scroll.fullScroll(ScrollView.FOCUS_UP);
        deletePhoto();
    }

    /**
     * Starts the imagepicker activity
     */
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(OrganizerCreateEventFragment.this)
                .crop()                                 // Crop image(Optional), Check Customization for more option
                .compress(1024)                 // Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    /**
     * ActivityComplete result listener that is called when the photo add activity closes.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get image uri and file name from it
        imageUri =  data.getData();
        // if there's no uri, we didn't get a new photo, so return.
        if (imageUri == null) {
            return;
        }
        // set imageview and update organizer image preview
        //remove the background image
        imageView.setBackground(null);
        imageView.setImageURI(imageUri);
        deletePhoto.setVisibility(View.VISIBLE);
    }

    /**
     * Shows a confirmation when a user clicks the delete photo button.
     */
    private void showDeletePhotoConfirmation() {
        // show a confirmation dialog
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete your photo?")
                .setPositiveButton("Confirm", (dialog, which) -> deletePhoto())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Button listener to delete a photo. Removes the photo from the imageView, sets uri to null,
     * and sets the default photo to grey.
     */
    private void deletePhoto(){
        imageUri = null;
        imageView.setImageURI(imageUri);
        //put the background imgage back
        Drawable myIcon = getDrawable(getActivity().getApplicationContext(),
               R.drawable.baseline_add_photo_alternate_24);
        imageView.setBackground(myIcon);
        deletePhoto.setVisibility(View.INVISIBLE);
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
            new TimePickerFragment(startHour, startMinute).show(getChildFragmentManager(), tag);
        }
        else {

            // Set end date attributes
            endYear = year;
            endMonth = month;
            endDay = day;

            // Call timepicker
            new TimePickerFragment(endHour, endMinute).show(getChildFragmentManager(), tag);
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
                            getContext().getString(R.string.organizer_fragment_create_event_p1_startTime_set),
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
                            getContext().getString(R.string.organizer_fragment_create_event_p1_endTime_set),
                            endMonth, endDay, endYear, endHour, endMinute
                    )
            );
        }
    }
}