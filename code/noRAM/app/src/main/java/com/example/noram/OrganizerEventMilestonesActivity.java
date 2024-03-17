package com.example.noram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The OrganizerEventMilestonesActivity class displays the milestones for an event for the organizer.
 * A {@link AppCompatActivity} subclass.
 */
public class OrganizerEventMilestonesActivity extends AppCompatActivity {

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *    previously being shut down then this Bundle contains the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_milestones);
    }
}