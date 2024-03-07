package com.example.noram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.noram.model.Attendee;
import com.example.noram.model.Organizer;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    private Uri imageUri;

    private AppCompatButton editStartTime;
    private AppCompatButton editEndTime;
    private FloatingActionButton addPhoto;
    private FloatingActionButton deletePhoto;

    private TextView editName;
    private TextView editLocation;
    private TextView editDetails;
    private TextView editMilestones;
    private CheckBox trackLocationCheck;
    private ImageView imageView;
    private Button nextButton;


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

    /**
     * Initialize fragment upon complete creation of view hierarchy
     * view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.

     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_con_layout_organizer_create_event, container, false);

        // Initialize fragment upon display
        super.onViewCreated(view, savedInstanceState);

        // Find views
        editStartTime = view.findViewById(R.id.event_startTime_button);
        editEndTime = view.findViewById(R.id.event_endTime_button);
        addPhoto = view.findViewById(R.id.add_photo);
        deletePhoto = view.findViewById(R.id.delete_photo);
        //make delete photo button invisible in the beginning
        deletePhoto.setVisibility(View.INVISIBLE);

        editName = view.findViewById(R.id.event_name);
        editLocation = view.findViewById(R.id.event_location);
        editDetails = view.findViewById(R.id.event_details);
        editMilestones  = view.findViewById(R.id.event_milestones_text);
        trackLocationCheck = view.findViewById(R.id.event_trackLocation_check);
        imageView = view.findViewById(R.id.image_view);
        nextButton = view.findViewById(R.id.event_next_button);

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
                    bundle.putParcelable("imageUri", imageUri);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                // Otherwise, show error Toast
                else {
                    Toast.makeText(getContext(), String.format("%s is invalid", errorText), Toast.LENGTH_SHORT).show();
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
        return view;
    };
    /**
     * Starts the imagepicker activity
     */
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(OrganizerCreateEventFragment.this)
                .crop(1,1)                                 // Crop image(Optional), Check Customization for more option
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
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePhoto();
                    }
                })
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