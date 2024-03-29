/*
This file is used to create the main activity of the application. This activity is the first activity that is launched
when the application is opened. It is responsible for signing in the user and redirecting them to the appropriate activity
Outstanding Issues:
- Need to move the logic for getting the attendee from the database out of this class
 */

package com.example.noram;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.noram.model.Attendee;
import com.example.noram.model.Database;
import com.example.noram.model.Organizer;
import com.example.noram.model.PushNotificationService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

/**
 * The main activity of the application. This activity is the first activity that is launched
 * when the application is opened. It is responsible for signing in the user and redirecting
 * them to the appropriate activity based on their role.
 * A {@link AppCompatActivity} subclass.
 * @author noRAM
 */
public class MainActivity extends AppCompatActivity {

    public static Database db = new Database();
    public static final ShareHelper shareHelper = new ShareHelper();
    public static Attendee attendee = null;
    public static Organizer organizer = null;
    private Button adminButton;
    public static PushNotificationService pushService = new PushNotificationService();
    public static MainActivity mn;


    /**
     * Create and setup the main activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the main activity to this instance for use in other classes
        mn = this;

        // NOTE: temporary buttons to move to each activity
        // In the future, we should evaluate whether there is a better method of navigation;
        // for now, this will give us a base to start work without clashing against each other.
        adminButton = findViewById(R.id.adminButton);
        adminButton.setVisibility(View.INVISIBLE);
        Button organizerButton = findViewById(R.id.organizerButton);
        Button attendeeButton = findViewById(R.id.attendeeButton);

        // Start each activity via an intent.
        adminButton.setOnClickListener((v ->
                startActivity(new Intent(MainActivity.this, AdminActivity.class))
                ));

        organizerButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, OrganizerActivity.class))
        );

        attendeeButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AttendeeActivity.class))
        );

        // hide buttons until the user is fully signed in
        organizerButton.setVisibility(View.INVISIBLE);
        attendeeButton.setVisibility(View.INVISIBLE);

        // ask for camera permission
        // Reference: https://stackoverflow.com/a/66751594, Kfir Ram, "How to get camera permission on android", accessed feb 19 2024
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
        }

        // ask for notification permission
        // Reference: https://developer.android.com/training/notify-user/permissions#request-permission, accessed feb 19 2024
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 0);
        }
    }

    /**
     * Start the main activity. This signs in the user using firebase authentication
     */
    @Override
    public void onStart() {
        super.onStart();
        db.getmAuth().addAuthStateListener(auth -> signInFirebase());
    }

    /**
     * Signs in the user using firebase authentication and gets the
     * attendee associated with the UID.
     */
    private void signInFirebase() {

        // https://firebase.google.com/docs/auth/android/anonymous-auth?authuser=1#java
        db.getmAuth().signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = db.getmAuth().getCurrentUser();
                        Log.d(TAG, "signInAnonymously:success");
                        Log.d(TAG, "UID: " + user.getUid());

                        // Get the user's data from the database
                        db.getAttendeeRef().document(user.getUid()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document = task1.getResult();

                                if (document.exists()) {
                                    // If the user exists, get the user's information
                                    String firstname = document.getString("firstName");
                                    String lastname = document.getString("lastName");
                                    String homepage = document.getString("homePage");
                                    String email = document.getString("email");
                                    Boolean allowLocation = document.getBoolean("allowLocation");
                                    Boolean defaultPhoto = document.getBoolean("defaultProfilePhoto");
                                    List<String> eventsCheckedInto = (List<String>) document.get("eventsCheckedInto");
                                    attendee = new Attendee(user.getUid(), firstname, lastname, homepage, email, allowLocation, defaultPhoto, eventsCheckedInto);
                                    attendee.generateAttendeeFCMToken();
                                    attendee.updateDBAttendee();

                                    // get the organizer object from the database
                                    db.getOrganizerRef().document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> organizer = documentSnapshot.toObject(Organizer.class));
                                } else {
                                    // create new attendee
                                    attendee = new Attendee(user.getUid());
                                    attendee.generateAttendeeFCMToken();
                                    attendee.updateDBAttendee();

                                    // create new organizer, and sync it with the new attendee for now.
                                    organizer = new Organizer();
                                    organizer.syncWithAttendee(attendee);
                                    organizer.updateDBOrganizer();
                                }
                                // Show the buttons after the user is signed in and remove progress bar
                                findViewById(R.id.organizerButton).setVisibility(View.VISIBLE);
                                findViewById(R.id.attendeeButton).setVisibility(View.VISIBLE);
                                updateAdminAccess(user.getUid());
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                            } else {
                                Log.d(TAG, "get failed with ", task1.getException());
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed. Please Restart your App", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Updates the admin access for the application given a user ID
     * @param uid user ID to check for admin privileges.
     */
    private void updateAdminAccess(String uid) {
        DocumentReference adminRef = db.getAdminRef().document(uid);
        adminRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                // if user is found in the admin collection, show the admin button
                if (document.exists()) {
                    Log.d("AdminAccess", "User granted admin privileges");
                    adminButton.setVisibility(View.VISIBLE);
                } else {
                    Log.d("AdminAccess", "User does not have admin privileges");
                }
            }
        });
    }
}