package com.example.noram;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrganizerEventInfo extends AppCompatActivity {

    private Event event;
    private TextView eventTitle;
    private TextView organizerText;
    private ImageView organizerImage;
    private TextView eventLocation;
    private ImageView eventImage;
    private TextView eventDescription;

    /**
     * Update page's event ("event") with database's info
     * @param eventId the id of the event to be updated
     *                (the event must be in the database)
     */
    private void baseSetup(String eventId) {
        // Get event from database
        event = new Event();
        Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventId).get();
        task.addOnSuccessListener(documentSnapshot -> {
            // update event
            event.updateWithDocument(documentSnapshot);
            // update page's info
            eventTitle.setText(event.getName());
            eventDescription.setText(event.getDetails());
            LocalDateTime startTime = event.getStartTime();
            eventLocation.setText(String.format("%s from %s - %s @ %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("HH:mma")),
                    event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mma")),
                    event.getLocation()
            ));
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_info);

        // retrieve corresponding event in database
        //int eventID = getIntent().getIntExtra(AttendeeEventListFragment.eventIDLabel,0);
        String eventID = getIntent().getExtras().getString("event");
        baseSetup(eventID);

        // get all variables from page
        ImageButton backButton = findViewById(R.id.organizer_event_back_button);
        eventTitle = findViewById(R.id.organizer_event_title);
        organizerText = findViewById(R.id.organizer_event_organizer_text);
        organizerImage = findViewById(R.id.organizer_event_organizer_image);
        eventLocation = findViewById(R.id.organizer_event_location);
        eventImage = findViewById(R.id.organizer_event_image);
        eventDescription = findViewById(R.id.organizer_event_description);

        // connect back button
        backButton.setOnClickListener(v -> {finish();});

    }
}