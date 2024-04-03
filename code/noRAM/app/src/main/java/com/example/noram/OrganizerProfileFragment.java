/*
This file is used to display the profile of an organizer.
Outstanding Issues:
- Needs to be implemented
 */

package com.example.noram;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.noram.model.Organizer;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment displayed when viewing profile for an organizer
 * @maintainer Cole
 * @author Cole
 */
public class OrganizerProfileFragment extends Fragment {
    private ImageView imageView;
    private FloatingActionButton addPhoto;
    private FloatingActionButton deletePhoto;
    private Organizer organizer;
    private EditText displayName;
    private TextView currentSignupsText;
    private TextView currentAttendeesText;
    private TextView allTimeAttendeesText;
    private ListView briefEventList;

    /**
     * This is the default constructor for the fragment.
     */
    public OrganizerProfileFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment AttendeeProfileFragment.
     */
    public static OrganizerProfileFragment newInstance() {
        OrganizerProfileFragment fragment = new OrganizerProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
     * This method is called when the fragment is created, it initializes the
     * information and sets up listeners.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The view to be displayed with all attached listeners
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_profile, container, false);

        // Get the fields from the view
        imageView = view.findViewById(R.id.image_view);
        addPhoto = view.findViewById(R.id.add_photo);
        deletePhoto = view.findViewById(R.id.delete_photo);
        displayName = view.findViewById(R.id.edit_organizer_display_name);
        currentSignupsText = view.findViewById(R.id.organizer_current_signups);
        currentAttendeesText = view.findViewById(R.id.organizer_current_attendees);
        allTimeAttendeesText = view.findViewById(R.id.organizer_all_time_attendees);
        briefEventList = view.findViewById(R.id.organizer_brief_event_list);

        // Get the attendee from the main activity
        organizer = MainActivity.organizer;

        // Set the fields to the attendee's information
        setFields(organizer);

        // hide delete button if we are using our attendee's default photo
        if (organizer.isUsingAttendeeProfilePicture()) {
            deletePhoto.setVisibility(View.INVISIBLE);
        }

        // update the profile photo icon
        MainActivity.db.downloadPhoto(organizer.getPhotoPath(),
                t -> getActivity().runOnUiThread(() -> imageView.setImageBitmap(t)));

        // update organizer stats
        getUpdatedOrganizerStats();

        //set on click listener to add photo when pressed
        addPhoto.setOnClickListener(v -> startImagePicker());

        //set on click listener to delete photo when pressed
        deletePhoto.setOnClickListener(v -> showDeletePhotoConfirmation());

        // Save the entered information when the save button is clicked
        view.findViewById(R.id.organizer_info_save_button).setOnClickListener(v -> {
            String displayNameText = displayName.getText().toString();

            // Validate name and email fields
            Pair<Boolean, String> validateResult = OrganizerValidator.validateFromFields(displayNameText, organizer.getPhotoPath());
            if (validateResult.first) {
                organizer.setName(displayNameText);
                organizer.updateDBOrganizer();
                MainActivity.db.downloadPhoto(organizer.getPhotoPath(),
                        t -> getActivity().runOnUiThread(() -> imageView.setImageBitmap(t)));
            } else {
                Toast.makeText(getActivity(), validateResult.second, Toast.LENGTH_LONG).show();
            }
        });

        // Revert the changes when the cancel button is clicked
        view.findViewById(R.id.organizer_info_cancel_button).setOnClickListener(v -> setFields(organizer));
        return view;
    }

    /**
     * Starts the imagepicker activity
     */
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(OrganizerProfileFragment.this)
                .crop(1,1)                                 // Crop image(Optional), Check Customization for more option
                .compress(1024)                 // Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080(Optional)
                .start();
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

    // TODO: make sure the most recent default profile shows up if the user changes their name while
    //  using a real picture then deletes it. This is a minor issue, but it would be nice to fix.
    /**
     * Button listener to delete a photo. Removes the photo from the cloud storage and replaces
     * it with the current attendee's profile photo.
     */
    private void deletePhoto(){
        String deletePhotoStr = organizer.getPhotoPath();
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(deletePhotoStr);
        storageReference.delete();

        organizer.setPhotoPath(MainActivity.attendee.getProfilePhotoString());
        organizer.setUsingAttendeeProfilePicture(true);
        deletePhoto.setVisibility(View.INVISIBLE);
        MainActivity.db.downloadPhoto(organizer.getPhotoPath(),
                t -> getActivity().runOnUiThread(() -> imageView.setImageBitmap(t)));
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
        Uri uri =  data.getData();

        // if there's no uri, we didn't get a new photo, so return.
        if (uri == null) {
            return;
        }

        String uriString = "profile_photos/" + organizer.getIdentifier() + "-organizer";

        // upload file to cloud storage
        MainActivity.db.uploadPhoto(uri, uriString);

        // set imageview and update attendee information
        imageView.setImageURI(uri);
        organizer.setPhotoPath(uriString);
        organizer.setUsingAttendeeProfilePicture(false);
        organizer.updateDBOrganizer();
        deletePhoto.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when the fragment is resumed.
     * It sets the fields to the organizer's information,
     * ensuring that any unsaved changes are reset.
     */
    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        organizer = MainActivity.organizer;

        if (view != null) {
            setFields(organizer);
            getUpdatedOrganizerStats();
        }
    }

    /**
     * Set the fields of the view to the organizer's information
     * @param organizer the attendee whose information is being displayed
     */
    public void setFields(Organizer organizer) {
        // Set the fields to the attendee's information
        displayName.setText(organizer.getName());
    }

    /**
     * Updates the stats that appear on the page for the organizer
     */
    private void getUpdatedOrganizerStats() {
        MainActivity.db.getEventsRef().whereEqualTo("organizerID", MainActivity.organizer.getIdentifier())
            .addSnapshotListener((querySnapshots, error) -> {
                // if error, log it and return
                if(error != null){
                    Log.e("Firestore", error.toString());
                    return;
                }

                // if querySnapshots is null, nothing to update.
                if(querySnapshots == null) return;

                int currentSignups = 0;
                int currentCheckins = 0;
                int totalCheckins = 0;
                for(QueryDocumentSnapshot doc: querySnapshots) {
                    List<String> signUps = (List<String>) doc.get("signedUpAttendees");
                    List<String> checkIns = (List<String>) doc.get("checkedInAttendees");

                    // only count unique sign ups or check ins
                    Set<String> signUpSet = new HashSet<>(signUps);
                    Set<String> checkInsSet = new HashSet<>(checkIns);
                    LocalDateTime endTime = LocalDateTime.parse(doc.getString("endTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    // for each event, count the number of attendees
                    totalCheckins += checkInsSet.size();

                    // for every event that has not ended, get current signups + attendees
                    if (LocalDateTime.now().isBefore(endTime)) {
                        currentSignups += signUpSet.size();
                        currentCheckins += checkInsSet.size();
                    }
                }

                // update the UI once we have gone through all documents.
                updateOrganizerStatsUI(currentSignups, currentCheckins, totalCheckins);
            });
    }

    /**
     * Update the UI that displays the organizer stats
     * @param currentSignups current number of signups
     * @param currentCheckins current number of checkins
     * @param totalCheckins all time number of checkins
     */
    private void updateOrganizerStatsUI(int currentSignups, int currentCheckins, int totalCheckins) {
        currentSignupsText.setText(String.format("Total Current \n Signups:\n%d", currentSignups));
        currentAttendeesText.setText(String.format("Total Current \nAttendees:\n%d", currentCheckins));
        allTimeAttendeesText.setText(String.format("All Time \nAttendees:\n%d", totalCheckins));
    }

}