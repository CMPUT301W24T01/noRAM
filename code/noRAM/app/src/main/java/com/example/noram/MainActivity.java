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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventManager;
import com.example.noram.model.Attendee;
import com.example.noram.model.Database;
import com.example.noram.model.Event;
import com.example.noram.model.Organizer;
import com.example.noram.model.PushNotificationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

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
    private BottomNavigationView navBar;
    private TextView eventPosterTitle;


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

        mn = this;

        // NOTE: temporary buttons to move to each activity
        // In the future, we should evaluate whether there is a better method of navigation;
        // for now, this will give us a base to start work without clashing against each other.
        adminButton = findViewById(R.id.adminButton);
        adminButton.setVisibility(View.INVISIBLE);
        navBar = findViewById(R.id.bottom_nav);

        // hide menu until user is fully signed in and remove focus from its items
        navBar.setVisibility(View.INVISIBLE);
        navBar.setItemActiveIndicatorEnabled(false);

        // hide eventPoster's title until page is loaded
        eventPosterTitle = findViewById(R.id.eventPoster_title);
        eventPosterTitle.setVisibility(View.INVISIBLE);

        // Start admin via button.
        adminButton.setOnClickListener((v ->
                startActivity(new Intent(MainActivity.this, AdminActivity.class))
                ));

        // connect navigation bar to other pages
        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
             /**
              * Change to an activity when a navbar item is selected.
              * @param item The selected item on the navbar
              * @return true if navigation succeeds, false otherwise
              */
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 return navigateTo(item);
             }
        });

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
                                navBar.setVisibility(View.VISIBLE);
                                findViewById(R.id.progressBar).setVisibility(View.GONE);

                                // update admin access
                                updateAdminAccess(user.getUid());

                                // show the poster event and related title
                                eventPosterTitle.setVisibility(View.VISIBLE);
                                db.getEventsRef().get().addOnSuccessListener(query -> {
                                    // get random event
                                    Random random = new Random();
                                    int randIndex = random.nextInt(query.size());
                                    DocumentSnapshot randDoc = query.getDocuments().get(randIndex);

                                    // random event is set as poster
                                    Event event = new Event();
                                    event.updateWithDocument(randDoc);
                                    displayPosterEvent(event);
                                });
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

    /**
     * Navigate to the corresponding menu item
     * @param item menuItem to navigate to
     * @return true if navigation succeeded, else false
     */
    private boolean navigateTo(MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.attend_events ){
            startActivity(new Intent(MainActivity.this, AttendeeActivity.class));
        } else if(itemID == R.id.organize_events){
            startActivity(new Intent(MainActivity.this, OrganizerActivity.class));
        } else{
            return false;
        }

        // if valid item, show focus and return true
        navBar.setItemActiveIndicatorEnabled(true);
        return true;
    }

    /**
     * Promotes a random event to the main page of the app, that the user can consult.
     * The main logic is the same as with EventArrayAdapter
     * @param event The event being promoted to the main page
     */
    private void displayPosterEvent(Event event){
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.event_list_item, null);

        FrameLayout container = findViewById(R.id.poster_event);
        container.addView(view);

        // get item's fields (UI)
        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView eventLocation = view.findViewById(R.id.event_location);
        TextView eventSignUpCapacity = view.findViewById(R.id.event_signUp_capacity);
        TextView signedUpText = view.findViewById(R.id.event_signed_up_indicator);
        TextView checkedInText = view.findViewById(R.id.event_checked_in_indicator);
        TextView happeningNowText = view.findViewById(R.id.event_happening_now);

        // update fields and return view
        eventTitle.setText(event.getName());
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime endTime = event.getEndTime();
        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            eventTime.setText(String.format("%s \n%s to %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mma")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mma"))
            ));
        } else {
            // not the same date: need to include both dates
            eventTime.setText(String.format("%s at %s to \n%s at %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    endTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            ));
        }

        // if event is currently happening, display "happening now!"
        LocalDateTime current = LocalDateTime.now();
        happeningNowText.setVisibility(startTime.isBefore(current) && endTime.isAfter(current) ? View.VISIBLE : View.GONE);

        // show signups count
        eventLocation.setText(event.getLocation());
        if (event.isLimitedSignUps()) {
            eventSignUpCapacity.setText(String.format(
                    this.getString(R.string.signup_limit_format),
                    event.getSignUpCount(),
                    event.getSignUpLimit())
            );
        }
        else {
            eventSignUpCapacity.setText(String.format(
                    this.getString(R.string.signup_count_format),
                    event.getSignUpCount())
            );
        }

        // show if user is already checked-in or signed-in
        if (event.getCheckedInAttendees().contains(MainActivity.attendee.getIdentifier())) {
            checkedInText.setVisibility(View.VISIBLE);
        } else {
            checkedInText.setVisibility(View.INVISIBLE);
        }
        if (event.getSignedUpAttendees().contains(MainActivity.attendee.getIdentifier())) {
            signedUpText.setVisibility(View.VISIBLE);
        } else {
            signedUpText.setVisibility(View.INVISIBLE);
        }

        // user can go to the event by clicking on it
        container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EventManager.displayAttendeeEvent(MainActivity.this,event);
            }
        });
    }
}