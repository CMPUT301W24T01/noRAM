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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

/**
 * The OrganizerEventNotificationsActivity class allows event organizers to send locations for their event.
 * A {@link AppCompatActivity} subclass.
 * @Maintainer Christiaan
 * @Author Christiaan
 */
public class OrganizerEventNotificationsActivity extends AppCompatActivity {

    // Attributes
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
            Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventID).get();
            task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                /**
                 * On successful acquisition of event attributes from database, create event
                 * Set dateTime attributes with event attributes
                 * Update text of views with event attributes
                 *
                 * @param documentSnapshot database object from which object is initialized
                 */
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    // Make event
                    event.updateWithDocument(documentSnapshot);

                    return null;
                }
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
            String title = editTitle.getText().toString();
            String content = editContent.getText().toString();
            MainActivity.pushService.sendNotification(title, content, event, false);
            finish();
        });

        cancelButton.setOnClickListener(v -> finish());

    }
}