package com.example.noram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The OrganizerEventNotificationsActivity class allows event organizers to send locations for their event.
 * A {@link AppCompatActivity} subclass.
 */
public class OrganizerEventNotificationsActivity extends AppCompatActivity {

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *    previously being shut down then this Bundle contains the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_notifications);
    }
}