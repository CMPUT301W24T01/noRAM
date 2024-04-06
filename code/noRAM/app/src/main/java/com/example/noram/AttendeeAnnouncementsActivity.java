/*
This file is used to display the announcements for a specific event.
Outstanding Issues:
-
 */

package com.example.noram;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.NotificationArrayAdapter;
import com.example.noram.model.Event;
import com.example.noram.model.Notification;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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
    private ArrayList<Notification> NotificationDataList;
    private NotificationArrayAdapter NotificationAdapter;
    private ListView NotificationList;
    private Event event;

    /**
     * This method is called when the activity is created.
     * It sets up the activity and retrieves the corresponding event from the database.
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_announcements);


        // get eventIDLabel and also eventID from intent
        String eventID = getIntent().getStringExtra(eventIDLabel);
        assert eventID != null;

        Log.d("event ID before db", eventID);

        MainActivity.db.getEventsRef().document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                event.updateWithDocument(documentSnapshot);
            }
        });

        Log.d("event ID after db", event.getId());

        Log.d("notification list", String.valueOf(event.getNotifications()));

        ListView notificationList = findViewById(R.id.notification_list);

        ArrayList<Notification> notificationDataList = new ArrayList<>();

        NotificationArrayAdapter notificationAdapter = new NotificationArrayAdapter(this, notificationDataList);

        notificationList.setAdapter(notificationAdapter);

        notificationDataList.addAll(event.getNotifications());

        notificationAdapter.notifyDataSetChanged();

        NotificationDataList = (ArrayList<Notification>) event.getNotifications();

        NotificationAdapter.addAll(NotificationDataList);

        NotificationAdapter.notifyDataSetChanged();

    }
}
