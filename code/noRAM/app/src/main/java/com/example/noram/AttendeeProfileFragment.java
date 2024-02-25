package com.example.noram;

import static com.example.noram.MainActivity.sharedAttendee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.noram.model.Database;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeProfileFragment extends Fragment{


    // TODO: Rename and change types of parameters
    private ImageView imageView;
    private FloatingActionButton addPhoto;

    private FloatingActionButton deletePhoto;



    public AttendeeProfileFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AttendeeProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendeeProfileFragment newInstance() {
        AttendeeProfileFragment fragment = new AttendeeProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //check if db already has a profile pic for this user.
        //if yes
            //upload the picture to populate imageView
        //if no
            //upload a auto fill photo
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendee_profile, container, false);

        imageView = rootView.findViewById(R.id.image_view);
        addPhoto = rootView.findViewById(R.id.add_photo);
        deletePhoto = rootView.findViewById(R.id.delete_photo);

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
        String deletePhotoStr = MainActivity.sharedAttendee.getProfilePic();
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
        MainActivity.sharedAttendee.setProfilePic(uriString);
        //manuallty set dimensions of the photo? allow only 1x1 box croping
    }

}