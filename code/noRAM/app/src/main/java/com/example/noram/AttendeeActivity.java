package com.example.noram;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


/**
 * The AttendeeActivity class is the main activity for the Attendee user type.
 * It contains the navigation bar, header, and fragment container to display
 * all of the information available to the attendee and allow for navigation.
 */
public class AttendeeActivity extends AppCompatActivity {

    public static final int NAV_SCAN = R.id.navbar_scan;
    public static final int NAV_EVENTS = R.id.navbar_events;
    public static final int NAV_PROFILE = R.id.navbar_profile;

    private final Fragment qrFragment = QrScanFragment.newInstance();
    private final Fragment profileFragment = AttendeeProfileFragment.newInstance();
    private final Fragment eventsFragment = AttendeeEventListFragment.newInstance();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;

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
        TextView headerText = findViewById(R.id.attendee_header_text);
        ImageButton homeButton = findViewById(R.id.home_button);
        BottomNavigationView navBar = findViewById(R.id.bottom_nav);
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragment_container_view);
        navBar.setSelectedItemId(NAV_SCAN);
        activeFragment = qrFragment;

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
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            /**
             * Updates the Fragment shown in the FragmentContainerView when a navbar
             * item is selected.
             * @param item The selected item on the navbar
             * @return true if navigation succeeds, false otherwise
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String headerString = "";

                // set the selectedFragment to the appropriate fragment
                int itemID = item.getItemId();
                if (itemID == R.id.navbar_scan) {
                    selectedFragment = qrFragment;
                    headerString = getString(R.string.scan_qr_code_title);
                } else if (itemID == R.id.navbar_events) {
                    selectedFragment = eventsFragment;
                    headerString = getString(R.string.attendee_events_title);
                } else if (itemID == R.id.navbar_profile) {
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
                            .show(selectedFragment)
                            .commit();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container_view, selectedFragment)
                            .commit();
                    return true;
                }
            }
        });
    }
}