/*
This file is used for the OrganizerEventNotificationsActivity class. This class is used to allow event organizers to send notifications for their event.
Outstanding Issues:
- None
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.noram.model.Event;
import com.example.noram.model.Notification;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

/**
 * The OrganizerEventNotificationsActivity class allows event organizers to send locations for their event.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Christiaan
 * @author Christiaan
 */
public class OrganizerEventNotificationsActivity extends AppCompatActivity {

    // Attributes
    private List<Notification> listNotification;
    private Event event = new Event();

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *    previously being shut down then this Bundle contains the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_notifications);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Ensure intent contains event
        Intent intent = getIntent();
        if (intent.hasExtra("event")) {
            String eventID = Objects.requireNonNull(intent.getExtras()).getString("event");
            assert (eventID != null);

            Task<DocumentSnapshot> eventTask = MainActivity.db.getEventsRef().document(eventID).get();
            eventTask.addOnSuccessListener(documentSnapshot -> {
                event.updateWithDocument(documentSnapshot);
                listNotification = event.getNotifications();
            });
        }

        // Set up views
        TextView editTitle = findViewById(R.id.organizer_notifications_edit_title_text);
        TextView editContent = findViewById(R.id.organizer_notifications_edit_content_text);
        AppCompatButton sendButton = findViewById(R.id.organizer_notifications_send_button);
        AppCompatButton cancelButton = findViewById(R.id.organizer_notifications_cancel_button);
        ImageButton backButton = findViewById(R.id.organizer_notifications_back_button);

        // Set up buttons

        backButton.setOnClickListener(v -> finish());

        sendButton.setOnClickListener(v -> {
            // Send notification

            // send toast saying notification sent
            Toast.makeText(getApplicationContext(), "Notification Sent!", Toast.LENGTH_SHORT).show();
            String title = editTitle.getText().toString();
            String content = editContent.getText().toString();
            MainActivity.pushService.sendNotification(title, content, event, false);
            finish();
        });

        cancelButton.setOnClickListener(v -> finish());

    }
}
