/*
This file is used to display the attendee's profile information and allow for editing.
Outstanding Issues:
- The attendee's information is not saved to the database.
- The attendee's profile picture is not the correct type.
 */

package com.example.noram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noram.model.Attendee;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;


/**
 * A {@link Fragment} subclass that displays and allows editing of attendee information.
 * Use the {@link AttendeeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeProfileFragment extends Fragment{

    // TODO: Rename and change types of parameters
    private ImageView imageView;
    private FloatingActionButton addPhoto;

    private FloatingActionButton deletePhoto;

    private Attendee attendee;

    /**
     * This is the default constructor for the fragment.
     */
    public AttendeeProfileFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
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
        View rootView = inflater.inflate(R.layout.fragment_attendee_profile, container, false);

        imageView = rootView.findViewById(R.id.image_view);
        addPhoto = rootView.findViewById(R.id.add_photo);
        deletePhoto = rootView.findViewById(R.id.delete_photo);

        // Get the fields from the view
        EditText firstName = rootView.findViewById(R.id.edit_attendee_first_name);
        EditText lastName = rootView.findViewById(R.id.edit_attendee_last_name);
        EditText homePage = rootView.findViewById(R.id.edit_attendee_home_page);
        EditText phone = rootView.findViewById(R.id.edit_attendee_phone);
        CheckBox allowLocation = rootView.findViewById(R.id.edit_attendee_location_box);

        attendee = MainActivity.attendee;

        // Set the fields to the attendee's information
        firstName.setText(attendee.getFirstName());
        lastName.setText(attendee.getLastName());
        homePage.setText(attendee.getHomePage());
        phone.setText(attendee.getPhoneNumber());
        allowLocation.setChecked(attendee.getAllowLocation());

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
                deletePhoto();
            }
        });

        // Save the entered information when the save button is clicked
        rootView.findViewById(R.id.attendee_info_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendee.setFirstName(firstName.getText().toString());
                attendee.setLastName(lastName.getText().toString());
                attendee.setHomePage(homePage.getText().toString());
                attendee.setPhoneNumber(phone.getText().toString());
                attendee.setAllowLocation(allowLocation.isChecked());
                attendee.profilePhotoGenerator();
            }
        });

        // Revert the changes when the cancel button is clicked
        rootView.findViewById(R.id.attendee_info_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName.setText(attendee.getFirstName());
                lastName.setText(attendee.getLastName());
                homePage.setText(attendee.getHomePage());
                phone.setText(attendee.getPhoneNumber());
                allowLocation.setChecked(attendee.getAllowLocation());
            }
        });

        return rootView;
    }
    private void startImagePicker() {
        //From Dhaval2404/ImagePicker GitHub accessed Feb 23 2024 by Sandra
        //https://www.youtube.com/watch?v=v6YvUxpgSYQ
        ImagePicker.with(AttendeeProfileFragment.this)
                .crop(1,1)                                 // Crop image(Optional), Check Customization for more option
                .compress(1024)                 // Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    private void deletePhoto(){
        String deletePhotoStr = attendee.getProfilePicture();
        //DocumentReference photoRef = MainActivity.db.getAttendeeRef().document("test");
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(deletePhotoStr);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Firebase", "Photo successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firebase", "Photo unsuccessfully deleted!");
            }
        });

        imageView.setImageURI(null);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri =  data.getData();
        String uriString = String.valueOf("profile_photos/"+uri.getLastPathSegment());

//        int docInt = sharedAttendee.getIdentifier();
//        String docStr = String.valueOf(docInt);

        //future; photoRef will be handeled by Attendee later
        DocumentReference photoRef = MainActivity.db.getAttendeeRef().document("test");
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(uriString);

        storageReference.putFile(uri);
        photoRef.update("profilePhoto", uriString)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                              Log.d("Firebase", "Photo successfully added!");
                                          }
                                      });
        Log.d("TestStatement", "inside");
        Log.d("Image URI:", uriString);
        //check if the imageView already has an image
        //imageView.getImageURI()
        imageView.setImageURI(uri);
        attendee.setProfilePicture(uriString);
        //manuallty set dimensions of the photo? allow only 1x1 box croping
    }

}