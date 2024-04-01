/*
This file is used to display the announcements for a specific event.
Outstanding Issues:
- Has no functionality yet
 */

package com.example.noram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.NotificationArrayAdapter;
import com.example.noram.model.Event;
import com.example.noram.model.Notification;

import java.util.ArrayList;

/**
 * This class represents the activity for the attendee announcements page.
 * It displays the announcements for a specific event.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Christiaan
 * @author Christiaan
 */
public class AttendeeAnnouncementsActivity extends AppCompatActivity {
    private static final String eventIDLabel = "eventID";
    private ArrayList<Notification> NotificationList;
    private NotificationArrayAdapter NotificationAdapter;

    /**
     * This method is called when the activity is created.
     * It sets up the activity and retrieves the corresponding event from the database.
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_announcements);

        // retrieve corresponding event in database
        int eventID = getIntent().getIntExtra(eventIDLabel,0);

        MainActivity.db.getEventsRef().document(String.valueOf(eventID)).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                // displayAnnouncements(event);
            }
        });

        NotificationList = new ArrayList<>(); // TODO: get notifications from database
        
        NotificationAdapter = new NotificationArrayAdapter(this, NotificationList);

        NotificationList.add(new Notification("Announcement 1", "This is the first announcement"));

    }
}
