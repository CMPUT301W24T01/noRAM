package com.example.noram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Attendee;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;

public class ProfileEntryActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton addPhoto;
    private FloatingActionButton deletePhoto;
    private Attendee attendee;
    private EditText firstName;
    private EditText lastName;
    private EditText homePage;
    private EditText email;
    private CheckBox allowLocation;
    private Button finish_button;
    private Button continue_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        // Get the fields from the view
        imageView = findViewById(R.id.image_view);
        addPhoto = findViewById(R.id.add_photo);
        deletePhoto = findViewById(R.id.delete_photo);
        firstName = findViewById(R.id.edit_attendee_first_name);
        lastName = findViewById(R.id.edit_attendee_last_name);
        homePage = findViewById(R.id.edit_attendee_home_page);
        email = findViewById(R.id.edit_attendee_email);
        allowLocation = findViewById(R.id.edit_attendee_location_box);
        finish_button = findViewById(R.id.profile_entry_finish_button);
        continue_button = findViewById(R.id.profile_entry_continue_button);

        attendee = MainActivity.attendee;

        setFields(attendee);

        // Hide the profile picture and buttons initially
        imageView.setVisibility(View.INVISIBLE);
        addPhoto.setVisibility(View.INVISIBLE);
        deletePhoto.setVisibility(View.INVISIBLE);
        finish_button.setVisibility(View.INVISIBLE);

        //set on click listener to add photo when pressed
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImagePicker();
            }
        });

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePhotoConfirmation();
            }
        });

        // Save the entered information and switch screens when the continue button is clicked
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueForm();
            }
        });

        // Redirect to the main activity after pressing the finish button
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileEntryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    /**
     * Starts the imagepicker activity
     */
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(this)
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
        new AlertDialog.Builder(this)
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
     * Button listener to delete a photo. Removes the photo from the cloud storage and replaces
     * it with the default profile photo
     */
    private void deletePhoto(){
        String deletePhotoStr = attendee.getProfilePhotoString();
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(deletePhotoStr);
        storageReference.delete().addOnSuccessListener(
                unused -> Log.d("Firebase", "Photo successfully deleted!")
        ).addOnFailureListener(
                e -> Log.d("Firebase", "Photo unsuccessfully deleted!")
        );

        attendee.setDefaultProfilePhoto(true);
        deletePhoto.setVisibility(View.INVISIBLE);
        MainActivity.db.downloadPhoto(attendee.getProfilePhotoString(),
                t -> this.runOnUiThread(() -> imageView.setImageBitmap(t)));
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

    /**
     * Continue the form by saving the entered information and switching screens
     */
    public void continueForm() {
        String editFirstName = firstName.getText().toString();
        String editLastName = lastName.getText().toString();
        String editHomePage = homePage.getText().toString();
        String editEmail = email.getText().toString();
        Boolean editAllowLocation = allowLocation.isChecked();

        // Validate name and email fields
        if (validateAttendeeFields(editFirstName, editLastName, editEmail)) {
            // update the attendee's information
            attendee.setFirstName(editFirstName);
            attendee.setLastName(editLastName);
            attendee.setHomePage(editHomePage);
            attendee.setEmail(editEmail);
            attendee.setAllowLocation(editAllowLocation);
            attendee.generateDefaultProfilePhoto();

            // hide the rest of the fields
            firstName.setVisibility(View.INVISIBLE);
            lastName.setVisibility(View.INVISIBLE);
            homePage.setVisibility(View.INVISIBLE);
            email.setVisibility(View.INVISIBLE);
            allowLocation.setVisibility(View.INVISIBLE);
            findViewById(R.id.attendee_first_name_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.attendee_last_name_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.attendee_home_page_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.attendee_email_label).setVisibility(View.INVISIBLE);
            continue_button.setVisibility(View.INVISIBLE);

            // TODO: make this wait for the photo to be uploaded so the image shows
            // update the profile photo icon
            MainActivity.db.downloadPhoto(attendee.getProfilePhotoString(),
                    t -> ProfileEntryActivity.this.runOnUiThread(() -> imageView.setImageBitmap(t)));

            // Show the profile image
            imageView.setVisibility(View.VISIBLE);
            addPhoto.setVisibility(View.VISIBLE);
            findViewById(R.id.profile_entry_finish_button).setVisibility(View.VISIBLE);

            // show the delete button if the default photo is not being used
            if (!attendee.getDefaultProfilePhoto()) {
                deletePhoto.setVisibility(View.VISIBLE);
            }

            // update the page header
            ((TextView) findViewById(R.id.profile_entry_header)).setText("Add A Profile Picture");
        }
    }

    /**
     * Validate the fields of the attendee information, return true if valid, false otherwise.
     * If it is not valid display a message saying what is wrong
     * @param editFirstName the first name that was entered in the field
     * @param editLastName the last name that was entered in the field
     * @param editEmail the email that was entered in the field
     * @return true if all fields are valid, false otherwise
     */
    public Boolean validateAttendeeFields(String editFirstName, String editLastName, String editEmail) {
        if (editFirstName.isEmpty()) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editLastName.isEmpty()) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editEmail.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(editEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}