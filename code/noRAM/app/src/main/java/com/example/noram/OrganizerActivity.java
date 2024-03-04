package com.example.noram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDateTime;

public class OrganizerActivity extends AppCompatActivity {

    // Attributes
    private int year;
    private int month;
    private int day;
    public static final int NAV_NEW_EVENT = R.id.navbar_new_event;
    public static final int NAV_MY_EVENTS = R.id.navbar_my_events;
    public static final int NAV_PROFILE = R.id.navbar_organizer_profile;

    private final Fragment newEventFragment = OrganizerCreateEventFragment.newInstance();
    private final Fragment profileFragment = OrganizerProfileFragment.newInstance();
    private final Fragment myEventsFragment = OrganizerEventListFragment.newInstance();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        BottomNavigationView navBar = findViewById(R.id.organizer_bottom_nav);
        FragmentContainerView fragmentContainerView = findViewById(R.id.organizer_fragment_container_view);
        navBar.setSelectedItemId(NAV_MY_EVENTS);
        activeFragment = myEventsFragment;

        // create fragments into the fragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.organizer_fragment_container_view, myEventsFragment, "myEvents")
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.organizer_fragment_container_view, newEventFragment, "newEvent")
                .hide(newEventFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.organizer_fragment_container_view, profileFragment, "profile")
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
                if (itemID == NAV_NEW_EVENT) {
                    selectedFragment = newEventFragment;
                } else if (itemID == NAV_MY_EVENTS) {
                    selectedFragment = myEventsFragment;
                } else if (itemID == NAV_PROFILE) {
                    selectedFragment = profileFragment;
                }

                if (selectedFragment == null) {
                    return false;
                } else {
                    // update the fragment container to show the selected fragment.
                    fragmentManager.beginTransaction()
                            .hide(activeFragment)
                            .show(selectedFragment)
                            .commit();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.organizer_fragment_container_view, selectedFragment)
                            .commit();
                    return true;
                }
            }
        });
    }
}