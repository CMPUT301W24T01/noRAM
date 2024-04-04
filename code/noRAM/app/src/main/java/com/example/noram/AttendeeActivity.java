/*
This file is used to display the Attendee's main menu screen, from where other subpages can be accessed.
Outstanding Issues:
- Sometimes lags a lot when loading - see why
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.RemoteMessage;

/**
 * The AttendeeActivity class is the main activity for the Attendee user type.
 * It contains the navigation bar, header, and fragment container to display
 * all of the information available to the attendee and allow for navigation.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Cole
 * @author Cole
 * @author Ethan
 */
public class AttendeeActivity extends AppCompatActivity implements GoToEventListener {

    // Attributes
    public static final int NAV_SCAN = R.id.navbar_scan;
    public static final int NAV_EVENTS = R.id.navbar_events;
    public static final int NAV_PROFILE = R.id.navbar_profile;
    private final Fragment qrFragment = QrScanFragment.newInstance();
    private final Fragment profileFragment = AttendeeProfileFragment.newInstance();
    private final Fragment eventsFragment = AttendeeEventListFragment.newInstance();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;
    private BottomNavigationView navBar;
    private TextView headerText;
    public static AttendeeActivity sn;

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);
        ImageButton homeButton = findViewById(R.id.attendee_home_button);
        headerText = findViewById(R.id.attendee_header_text);
        navBar = findViewById(R.id.bottom_nav);
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragment_container_view);
        navBar.setSelectedItemId(NAV_SCAN);
        activeFragment = qrFragment;

        // Set the attendee activity to this instance for use in other classes
        sn = this;

        // create fragments into the fragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, eventsFragment, "events")
                .hide(eventsFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, profileFragment, "profile")
                .hide(profileFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, qrFragment, "qr")
                .commit();

        // Set the initial header text
        headerText.setText(R.string.scan_qr_code_title);

        // create button listener so home button goes back to main page.
        homeButton.setOnClickListener(v -> finish());

        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            /**
             * Updates the Fragment shown in the FragmentContainerView when a navbar
             * item is selected.
             * @param item The selected item on the navbar
             * @return true if navigation succeeds, false otherwise
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigateTo(item);
            }
        });
    }

    /**
     * Navigate to the corresponding menu item
     * @param item menuItem to navigate to
     * @return true if navigation succeeded, else false
     */
    private boolean navigateTo(MenuItem item) {
        Fragment selectedFragment = null;
        String headerString = "";

        // set the selectedFragment to the appropriate fragment
        int itemID = item.getItemId();
        if (itemID == NAV_SCAN) {
            selectedFragment = qrFragment;
            headerString = getString(R.string.scan_qr_code_title);
        } else if (itemID == NAV_EVENTS) {
            selectedFragment = eventsFragment;
            headerString = getString(R.string.attendee_events_title);
        } else if (itemID == NAV_PROFILE) {
            selectedFragment = profileFragment;
            headerString = getString(R.string.attendee_profile_title);
        }

        if (selectedFragment == null) {
            return false;
        } else {
            // update the header text
            headerText.setText(headerString);
            // update the fragment container to show the selected fragment.
            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(selectedFragment)
                    .commitNow();
            activeFragment = selectedFragment;
            return true;
        }
    }

    /**
     * Go to the event passed by event by navigating to it
     * @param eventId id of the event to go to
     */
    @Override
    public void goToEvent(String eventId) {
        // Navigate the navbar to the events page, then go to the confetti page
        Intent intent = new Intent(this, CheckInConfettiActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);

        // get the event name from the DB so we can display it on the confetti page.
        // We do this here so we never see the placeholder text on the confetti page.
        MainActivity.db.getEventsRef().document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            String eventName = documentSnapshot.getString("name");
            bundle.putString("eventName", eventName);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        navBar.setSelectedItemId(NAV_EVENTS);
    }

    public static void showNotification(RemoteMessage remoteMessage) {
        // Display the notification with an alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(remoteMessage.getNotification().getTitle());
        builder.setMessage(remoteMessage.getNotification().getBody());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}