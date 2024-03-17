package com.example.noram;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * An activity displaying the information about an event. Depending on event's data, the layout page
 * will change.
 */
public class AttendeeEventInfo extends AppCompatActivity {
    private Event event; // current event being inquired
    private TextView eventTitle;
    private TextView organizerText;
    private ImageView organizerImage;
    private TextView eventLocation;
    private ImageView eventImage;
    private TextView eventDescription;
    /**
     * Signup the user to current event in the database and display a message through a new activity
     */
    private void signup(){
        // TODO: update database
        // TODO: send to message page
    }

    /**
     * Set the layout of an event which has been checked in
     */
    private void checkedInDisplay(){
        // checked-in event page
        setContentView(R.layout.attendee_checkedin_event_info);

        // connect announcements button
        TextView announcements = findViewById(R.id.announcementText);
        announcements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventInfo.this, AttendeeAnnouncementsActivity.class);
                intent.putExtra(AttendeeEventListFragment.eventIDLabel, event.getId());
                startActivity(intent);
            }
        });
    }

    /**
     * Set the layout of an event not yet checked-in
     */
    private void notCheckedInDisplay(){
        // unchecked event page
        setContentView(R.layout.attendee_event_info);

        // connect signup button
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    /**
     * Update page's event ("event") with database's info
     */
    private void baseSetup(String eventId){
        // Get event from database
        event = new Event();
        Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventId).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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

                //download the event image from db and populate the screen
                eventImage = findViewById(R.id.eventImage);
                String findImage = event.getId();
                // set imageview and update organizer image preview
                MainActivity.db.downloadPhoto("event_banners/"+findImage+"-upload",
                        t -> runOnUiThread(() -> eventImage.setImageBitmap(t)));
                //remove purple background, and android icon in xml
                //if you want image to format nicely.
                //use android:scaleType="fitCenter"
                // TODO: allow non square photos to be uploaded from camera

                //Log.d("Uploaded photo", findImage);
                //Log.d("EventInfo", event.getName());
                //Log.d("EventInfo", event.getDetails());
                //Log.d("EventInfo", event.getLocation());
                //organizerText.setText(); // TODO: update organizer (not implemented in event yet)
                // TODO: update organizer image
                //eventLocation.setText(); // TODO: format LocalDateTime with current API lvl
                // TODO: update event image
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // retrieve corresponding event in database
        //int eventID = getIntent().getIntExtra(AttendeeEventListFragment.eventIDLabel,0);
        String eventID = getIntent().getExtras().getString(AttendeeEventListFragment.eventIDLabel);
        baseSetup(eventID);

        // verify if user is already checked-in event: hide/show content in consequence
        // TODO: check if event is checked-in
        if(false){
            checkedInDisplay();
        }
        else{
            notCheckedInDisplay();
        }

        // get all variables from page
        ImageButton backButton = findViewById(R.id.backButton);
        eventTitle = findViewById(R.id.eventTitle);
        organizerText = findViewById(R.id.organizerText);
        organizerImage = findViewById(R.id.organizerImage);
        eventLocation = findViewById(R.id.eventLocation);
        eventImage = findViewById(R.id.eventImage);
        eventDescription = findViewById(R.id.eventDescription);

        // connect back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

    }


}
