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
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noram.controller.ImageCarouselAdapter;
import com.example.noram.model.Attendee;
import com.example.noram.model.Database;
import com.example.noram.model.Event;
import com.example.noram.model.Organizer;
import com.example.noram.model.PushNotificationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
    private BottomNavigationView navBar;
    private TextView eventPosterTitle;
    private ImageCarouselAdapter adapter;
    private ArrayList<Event> carouselEvents;
    private TextView carouselEmptyText;
    private int currentCarouselIdx;
    private Handler timerHandler;
    private Runnable timerRunnable;

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

        RecyclerView recyclerView = findViewById(R.id.carousel_recycler);
        carouselEvents = new ArrayList<>();
        adapter = new ImageCarouselAdapter(MainActivity.this, carouselEvents);
        recyclerView.setAdapter(adapter);
        carouselEmptyText = findViewById(R.id.main_activity_no_events_text);
        CarouselLayoutManager manager = new CarouselLayoutManager(new HeroCarouselStrategy());
        recyclerView.setLayoutManager(manager);

        // if someone starts scrolling through the recycler view, we stop auto scrolling
        recyclerView.setOnTouchListener((v, event) -> {
            timerHandler.removeCallbacks(timerRunnable);
            return false;
        });

        // setup timer to auto scroll
        timerHandler = new Handler();
        timerRunnable = () -> {
            // update carousel index, scroll to that index, and start another callback timer
            currentCarouselIdx = ++currentCarouselIdx % carouselEvents.size();
            recyclerView.smoothScrollToPosition(currentCarouselIdx);
            timerHandler.postDelayed(timerRunnable, 3000);
        };

        adminButton = findViewById(R.id.adminButton);
        adminButton.setVisibility(View.INVISIBLE);
        navBar = findViewById(R.id.bottom_nav);

        // hide menu until user is fully signed in and remove focus from its items
        navBar.setVisibility(View.INVISIBLE);
        navBar.setItemActiveIndicatorEnabled(false);
        navBar.setSelectedItemId(R.id.invisible);

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

        // set nothing to selected
        navBar.setSelectedItemId(R.id.invisible);
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
                                    attendee.generateDefaultProfilePhoto();
                                    attendee.generateDefaultName();
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
                                populateCarousel();
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
     * Populates the carousel with random items that have event banners
     */
    private void populateCarousel() {
        // NOTE: i'm tired and this is probably not the best way to do this, but it works.
        // Get events that have event photos by getting all photo paths and seeing if the event
        // has a photo at the path we expect.
        db.getStorage().getReference().child("event_banners").listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();
            List<String> names = new ArrayList<>();
            // get every photo in cloud storage
            for (StorageReference i : items) {
                names.add(i.getPath());
            }

            db.getEventsRef().get().addOnSuccessListener(queryDocumentSnapshots -> {
                // randomly shuffle the items so we have some randomness in what we display
                ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                Collections.shuffle(docs);

                // look for up to 5 events.
                int found = 0;
                carouselEvents.clear();
                for (DocumentSnapshot doc : docs) {
                    if (found == 5) break;
                    // if the photo path we expect is found in cloud storage, we add this event to the
                    // adapter
                    String eventId = doc.getString("id");
                    String photoPath = "/event_banners/"+eventId+"-upload";
                    LocalDateTime eventEndTime = LocalDateTime.parse(doc.getString("endTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    // make sure the event has a photo and hasn't already ended
                    if (names.contains(photoPath) && LocalDateTime.now().isBefore(eventEndTime)) {
                        found++;
                        Event event = new Event();
                        event.updateWithDocument(doc);
                        carouselEvents.add(event);
                    }
                }

                if (carouselEvents.isEmpty()) {
                    carouselEmptyText.setVisibility(View.VISIBLE);
                } else {
                    carouselEmptyText.setVisibility(View.INVISIBLE);
                }

                adapter.notifyDataSetChanged();
                timerHandler.postDelayed(timerRunnable, 3000);
                currentCarouselIdx = 0;
            });
        });
    }

    /**
     * When we pause the activity, pause the auto-scroll runnable
     */
    @Override
    protected void onPause() {
        super.onPause();
        // stop timer
        timerHandler.removeCallbacks(timerRunnable);
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
        if(itemID == R.id.bottom_nav_attend_events){
            startActivity(new Intent(MainActivity.this, AttendeeActivity.class));
        } else if(itemID == R.id.bottom_nav_organize_events){
            startActivity(new Intent(MainActivity.this, OrganizerActivity.class));
        }

        // always return true even if we nav to the invisible item, since otherwise the UI doesn't update
        return true;
    }
}