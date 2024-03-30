/*
This file is used to represent the organizer's activity. It contains the bottom navigation bar and the fragments that are used to navigate between the organizer's events, creating a new event, and the organizer's profile.
Outstanding Issues:
- Sometimes lags a lot when loading - see why
 */

package com.example.noram;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.example.noram.OrganizerCreateEventFragment;
import com.example.noram.OrganizerEventListFragment;
import com.example.noram.OrganizerProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Class the represents organizer functions with bottom navigation bar
 * A {@link AppCompatActivity} subclass that represents the organizer's activity
 * @maintainer Cole
 * @author Cole
 * @author Carlin
 */
public class OrganizerActivity extends AppCompatActivity {
    // Attributes
    public static final int NAV_NEW_EVENT = R.id.navbar_new_event;
    public static final int NAV_MY_EVENTS = R.id.navbar_my_events;
    public static final int NAV_PROFILE = R.id.navbar_organizer_profile;
    private final Fragment newEventFragment = OrganizerCreateEventFragment.newInstance();
    private final Fragment profileFragment = OrganizerProfileFragment.newInstance();
    private final Fragment myEventsFragment = OrganizerEventListFragment.newInstance();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;
    private TextView header;

    // Main behaviour
    /**
     * Initialized the activity
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        BottomNavigationView navBar = findViewById(R.id.organizer_activity_bottom_nav);
        FragmentContainerView fragmentContainerView = findViewById(R.id.organizer_activity_fragment_container_view);
        navBar.setSelectedItemId(NAV_MY_EVENTS);
        activeFragment = myEventsFragment;
        header = findViewById(R.id.organizer_activity_header_text);
        header.setText(R.string.organizer_fragment_event_list_header);

        // create fragments into the fragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.organizer_activity_fragment_container_view, myEventsFragment, "myEvents")
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.organizer_activity_fragment_container_view, newEventFragment, "newEvent")
                .hide(newEventFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.organizer_activity_fragment_container_view, profileFragment, "profile")
                .hide(profileFragment)
                .commit();

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

                // set the selectedFragment to the appropriate fragment
                int itemID = item.getItemId();
                int headerText;
                if (itemID == NAV_NEW_EVENT) {
                    selectedFragment = newEventFragment;
                    headerText = R.string.organizer_fragment_create_event_p1_header;
                } else if (itemID == NAV_MY_EVENTS) {
                    selectedFragment = myEventsFragment;
                    headerText = R.string.organizer_fragment_event_list_header;
                } else if (itemID == NAV_PROFILE) {
                    selectedFragment = profileFragment;
                    headerText = R.string.organizer_fragment_profile_header;
                }
                else {
                    return false;
                }

                // update the fragment container to show the selected fragment.
                fragmentManager.beginTransaction()
                        .hide(activeFragment)
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .show(selectedFragment)
                        .commitNow();
                activeFragment = selectedFragment;

                // set header and return
                header.setText(headerText);
                return true;
            }
        });

        ((ImageButton) findViewById(R.id.organizer_home_button)).setOnClickListener(v -> finish());
    }

    /**
     * Function to programmatically move back the OrganizerActivity on the myEvents fragment
     */
    public void displayMyEventsFragment(){
        Fragment selectedFragment = myEventsFragment;
        int headerText = R.string.organizer_fragment_event_list_header;

        // update the fragment container to show the myEvents fragment .
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(selectedFragment)
                .commitNow();
        activeFragment = selectedFragment;

        // set header and return
        header.setText(headerText);
    }
}