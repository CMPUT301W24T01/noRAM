package com.example.noram;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Button;

import com.example.noram.model.Attendee;
import com.example.noram.model.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static Database db = new Database();

    public static Attendee sharedAttendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // NOTE: temporary buttons to move to each activity
        // In the future, we should evaluate whether there is a better method of navigation;
        // for now, this will give us a base to start work without clashing against each other.
        Button adminButton = findViewById(R.id.adminButton);
        Button organizerButton = findViewById(R.id.organizerButton);
        Button attendeeButton = findViewById(R.id.attendeeButton);

        // Start each activity via an intent.
        /**adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TESTTTT", "This is a debug message");
                Log.i("TESTTTT", "This is an informational message");
                Log.e("TESTTTT", "This is an error message");
                Log.w("TESTTTT", "This is a warning message");
            }
        });**/
        adminButton.setOnClickListener((v ->
                startActivity(new Intent(MainActivity.this, AdminActivity.class))
                ));

        organizerButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, OrganizerActivity.class))
        );

        attendeeButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AttendeeActivity.class))
        );

        // ask for camera permission
        // Reference: https://stackoverflow.com/a/66751594, Kfir Ram, "How to get camera permission on android", accessed feb 19 2024
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = db.getmAuth().getCurrentUser();

        // https://firebase.google.com/docs/auth/android/anonymous-auth?authuser=1#java
        db.getmAuth().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = db.getmAuth().getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }



    /*
    @Override
    public void uploadProfilePicture(Uri imageUri) {
        Object profileRef = db.collection("profilePicture");
        String filename = "profilePicture.jpg";
        String storageRef = profileRef.addPhoto().ref("/images/" + filename);
        String message = "data:image/jpg;base64," + imageUri;
        storageRef.set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firebase", "ProfilePicture successfully written!");
                    }

                    ;
                });
    }*/
}