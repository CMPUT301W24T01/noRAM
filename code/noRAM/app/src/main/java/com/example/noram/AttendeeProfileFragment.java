/*
This file is used to display the attendee's profile information and allow for editing.
Outstanding Issues:
- The attendee's information is not saved to the database.
- The attendee's profile picture is not the correct type.
- Need to move attendee validation logic out of this class
 */

package com.example.noram;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.noram.model.Attendee;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;


/**
 * A {@link Fragment} subclass.
 * AttendeeProfileFragment is a fragment that displays the attendee's profile information
 * It allows the user to edit their information and save it to the database.
 * It also displays information about their profile stored in the database.
 * @maintainer Ethan
 * @author Ethan
 * @author Cole
 * @author Sandra
 */
public class AttendeeProfileFragment extends Fragment{
    private ImageView imageView;
    private FloatingActionButton addPhoto;
    private FloatingActionButton deletePhoto;
    private ImageButton profileImage;
    private Attendee attendee;
    private EditText firstName;
    private EditText lastName;
    private EditText homePage;
    private EditText email;
    private CheckBox allowLocation;

    /**
     * This is the default constructor for the fragment.
     */
    public AttendeeProfileFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment AttendeeProfileFragment.
     */
    public static AttendeeProfileFragment newInstance() {
        AttendeeProfileFragment fragment = new AttendeeProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_attendee_profile, container, false);

        // Get the fields from the view
        imageView = view.findViewById(R.id.image_view);
        addPhoto = view.findViewById(R.id.add_photo);
        deletePhoto = view.findViewById(R.id.delete_photo);
        firstName = view.findViewById(R.id.edit_attendee_first_name);
        lastName = view.findViewById(R.id.edit_attendee_last_name);
        homePage = view.findViewById(R.id.edit_attendee_home_page);
        email = view.findViewById(R.id.edit_attendee_email);
        allowLocation = view.findViewById(R.id.edit_attendee_location_box);

        // Get the attendee from the main activity
        attendee = MainActivity.attendee;

        // Set the fields to the attendee's information
        setFields(attendee);

        // hide delete button if we are using a default profile photo
        if (attendee.getDefaultProfilePhoto()) {
            deletePhoto.setVisibility(View.INVISIBLE);
        }

        // update the profile photo icon
        MainActivity.db.downloadPhoto(attendee.getProfilePhotoString(),
                t -> getActivity().runOnUiThread(() -> imageView.setImageBitmap(t)));

        //set on click listener to add photo when pressed
        addPhoto.setOnClickListener(v -> startImagePicker());

        //set on click listener to delete photo when pressed
        deletePhoto.setOnClickListener(v -> showDeletePhotoConfirmation());

        // Save the entered information when the save button is clicked
        view.findViewById(R.id.attendee_info_save_button).setOnClickListener(v -> {
            String editFirstName = firstName.getText().toString();
            String editLastName = lastName.getText().toString();
            String editHomePage = homePage.getText().toString();
            String editEmail = email.getText().toString();
            Boolean editAllowLocation = allowLocation.isChecked();

            // Validate name and email fields
            Pair<Boolean, String> validateResult = AttendeeValidator.validateFromFields(editFirstName, editLastName, editEmail);
            if (validateResult.first) {
                attendee.setFirstName(editFirstName);
                attendee.setLastName(editLastName);
                attendee.setHomePage(editHomePage);
                attendee.setEmail(editEmail);
                attendee.setAllowLocation(editAllowLocation);

                if (attendee.getDefaultProfilePhoto()) {
                    attendee.generateAndReturnDefaultProfilePhoto(t -> getActivity().runOnUiThread(() -> imageView.setImageBitmap(t)));
                } else {
                    attendee.generateDefaultProfilePhoto();
                }
            } else {
                Toast.makeText(getActivity(), validateResult.second, Toast.LENGTH_LONG).show();
            }
        });

        // Revert the changes when the cancel button is clicked
        view.findViewById(R.id.attendee_info_cancel_button).setOnClickListener(v -> setFields(attendee));
        return view;
    }

    /**
     * Starts the imagepicker activity
     */
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(AttendeeProfileFragment.this)
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
     * it with the default profile photo
     */
    private void deletePhoto(){
        String deletePhotoStr = attendee.getProfilePhotoString();
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(deletePhotoStr);
        storageReference.delete().addOnSuccessListener(
                unused -> Log.d("Firebase", "AdminPhoto successfully deleted!")
        ).addOnFailureListener(
                e -> Log.d("Firebase", "AdminPhoto unsuccessfully deleted!")
        );

        attendee.setDefaultProfilePhoto(true);
        deletePhoto.setVisibility(View.INVISIBLE);
        MainActivity.db.downloadPhoto(attendee.getProfilePhotoString(),
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

        String uriString = "profile_photos/" + attendee.getIdentifier() + "-upload";

        // upload file to cloud storage
        MainActivity.db.uploadPhoto(uri, uriString);

        // set imageview and update attendee information
        imageView.setImageURI(uri);
        attendee.setDefaultProfilePhoto(false);
        deletePhoto.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when the fragment is resumed.
     * It sets the fields to the attendee's information,
     * ensuring that any unsaved changes are reset.
     */
    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        attendee = MainActivity.attendee;

        if (view != null) {
            setFields(attendee);
        }
    }

    /**
     * Set the fields of the view to the attendee's information
     * @param attendee the attendee whose information is being displayed
     */
    public void setFields(Attendee attendee) {
        // Set the fields to the attendee's information
        firstName.setText(attendee.getFirstName());
        lastName.setText(attendee.getLastName());
        homePage.setText(attendee.getHomePage());
        email.setText(attendee.getEmail());
        allowLocation.setChecked(attendee.getAllowLocation());
    }
}