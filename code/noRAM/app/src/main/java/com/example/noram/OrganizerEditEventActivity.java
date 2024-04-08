/*
This file is used to edit the event details for the organizer and update the event in the database.
Outstanding Issues:
None
 */

package com.example.noram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;

import com.example.noram.model.Event;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.util.GeoPoint;

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
 * @author Sandra
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
    private CheckBox trackLocationCheck;
    private TextView editLimitSignUps;
    private CheckBox limitSignUpsCheck;
    private ImageView locationPickerButton;
    private boolean locationIsRealLocation;
    private GeoPoint locationGeopoint;
    private FloatingActionButton addPhoto;
    private FloatingActionButton deletePhoto;
    private ImageView imageView;
    private Uri imageUri;
    private String eventID;

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
            assert (eventID != null);
            Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventID).get();
            task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                /**
                 * On successful acquisition of event attributes from database, create event
                 * Set dateTime attributes with event attributes
                 * Update text of views with event attributes
                 *
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
                    limitSignUpsCheck.setChecked(event.isLimitedSignUps());
                    editLimitSignUps.setText(event.isLimitedSignUps() ? Long.toString(event.getSignUpLimit()) : "");
                    if (event.isLimitedSignUps()) {
                        editLimitSignUps.setVisibility(View.VISIBLE);
                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                /**
                 * Failure as a result of an invalid event ID
                 *
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
        editMilestones = findViewById(R.id.organizer_activity_edit_event_edit_milestones_text);
        trackLocationCheck = findViewById(R.id.organizer_activity_edit_event_edit_trackLocation_check);
        limitSignUpsCheck = findViewById(R.id.organizer_activity_edit_event_edit_limitSignUps_check);
        editLimitSignUps = findViewById(R.id.organizer_activity_edit_event_signUpLimit_text);
        locationPickerButton = findViewById(R.id.edit_location_picker_button);

        editLocation.addTextChangedListener(new TextWatcher() {
            /**
             * Unused
             *
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * Unused
             *
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            /**
             * If we manually edit the text in the location box, no longer treat it as a real location
             *
             * @param s unused
             */
            @Override
            public void afterTextChanged(Editable s) {
                locationGeopoint = null;
                locationIsRealLocation = false;
            }
        });

        imageView = findViewById(R.id.image_view);
        deletePhoto = findViewById(R.id.delete_photo);
        addPhoto = findViewById(R.id.add_photo);

        //populate the imageview onCreate
        eventID = intent.getExtras().getString("event");
        String eventImagePath = "event_banners/" + eventID + "-upload";
        //if the db pulls an image
        if (eventImagePath != null) {
            //set the imageBitmap of the view
            deletePhoto.setVisibility(View.VISIBLE);
            imageView.setBackground(null);
            MainActivity.db.downloadPhoto(eventImagePath,
                    t -> runOnUiThread(() -> {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(t);
                    }));
        } else {
            //remove the delete button if there is not a photo
            deletePhoto.setVisibility(View.INVISIBLE);
        }

        // Set on-click listeners for buttons
        editStartDateTime.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Start Data/Time button
             * Calls Date and Time picker fragments
             *
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
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new DatePickerFragment(endYear, endMonth, endDay).show(getSupportFragmentManager(), "end");
            }
        });

        limitSignUpsCheck.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener to display/hide input box for sign-up limit
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if (editLimitSignUps.getVisibility() == View.GONE) {
                    editLimitSignUps.setVisibility(View.VISIBLE);
                } else {
                    editLimitSignUps.setVisibility(View.GONE);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Cancel button
             * Aborts changes to events
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // back button exits the page
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for Cancel button
             * Aborts changes to events
             *
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
             *
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
                if (limitSignUpsCheck.isChecked()) {
                    signUpLimit = Long.parseLong(editLimitSignUps.getText().toString());
                }

                Pair<Boolean, String> validateResult = EventValidator.validateFromFields(name, location, startDateTime, endDateTime, milestonesString, signUpLimit, event.getSignUpCount());

                // Only continue to next step of event creation if inputs are valid
                if (validateResult.first) {

                    // Make milestones list
                    List<Integer> milestones;
                    if (!milestonesString.isEmpty()) {
                        milestones = Stream.of(milestonesString.split(","))
                                .mapToInt(value -> Integer.parseInt(value.replaceAll("\\s+", "")))
                                .boxed()
                                .collect(Collectors.toList()
                                );
                    } else {
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
                    event.setSignUpLimit(signUpLimit);
                    event.setLocationIsRealLocation(locationIsRealLocation);
                    event.setLocationCoordinates(locationGeopoint);

                    // Update event in database
                    event.updateDBEvent();
                    finish();
                }

                // Otherwise, show error Toast
                else {
                    Toast.makeText(getBaseContext(), validateResult.second, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set on click listener to add photo when pressed
        addPhoto.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for addPhoto button
             * Calls startImagePicker
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                startImagePicker();
            }
        });
        //set on click listener to delete photo when pressed
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            /**
             * On-click listener for deletePhoto button
             * Calls showDeletePhotoConfirmation()
             *
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
            Intent pickerIntent = new Intent(this, LocationPickerActivity.class);
            launcher.launch(pickerIntent);
        });
    }

    /**
     * Starts the imagepicker activity
     */
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(OrganizerEditEventActivity.this)
                .crop()                                 // Crop image(Optional), Check Customization for more option
                .compress(1024)                 // Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    /**
     * Shows a confirmation when a user clicks the delete photo button.
     */
    private void showDeletePhotoConfirmation() {
        // show a confirmation dialog
        new AlertDialog.Builder(this)
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
        //delete the photo from the db. Get the photo location
        String deletePhotoStr = "event_banners/"+eventID+"-upload";
        //delete the photo
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(deletePhotoStr);
        storageReference.delete().addOnSuccessListener(
                unused -> Log.d("Firebase", "AdminPhoto successfully deleted!")
        ).addOnFailureListener(
                e -> Log.d("Firebase", "AdminPhoto unsuccessfully deleted!")
        );

        imageUri = null;
        //remove the image preview in the view
        imageView.setImageBitmap(null);
        //put the background image back
        Drawable myIcon = getDrawable(R.drawable.baseline_add_photo_alternate_24);
        imageView.setBackground(myIcon);
        deletePhoto.setVisibility(View.INVISIBLE);
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
        // get image uri and file name from the onActivityResult
        imageUri =  data.getData();
        // if there's no uri, we didn't get a new photo, so return.
        if (imageUri == null) {
            return;
        }
        // get the location of where we will add this new photo
        String uriString = "event_banners/"+eventID+"-upload";
        // upload file to cloud storage
        MainActivity.db.uploadPhoto(imageUri, uriString);
        // set imageview and update organizer image preview
        //remove the background image
        imageView.setBackground(null);
        imageView.setImageURI(imageUri);
        // show the delete photo button again
        deletePhoto.setVisibility(View.VISIBLE);
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
        } else {
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
}
