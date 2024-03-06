package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.noram.model.Event;

/**
 * An activity displaying the information about an event. Depending on event's data, the layout page
 * will change.
 */
public class AttendeeEventInfo extends AppCompatActivity {
    private static final String eventIDLabel = "eventID";
    private Event event; // current event being inquired

    /**
     * Signup the user to current event in the database and display a message through a new activity
     */
    private void signup(){
        // TODO: update database
        // TODO: send to message page
    }

    private void checkedInDisplay(){
        // checked-in event page
        setContentView(R.layout.attendee_checkedin_event_info);

        // connect announcements button
        TextView announcements = findViewById(R.id.announcementText);
        announcements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventInfo.this, AttendeeAnnouncementsActivity.class);
                intent.putExtra(eventIDLabel, event.getId());
                startActivity(intent);
            }
        });
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // retrieve corresponding event in database
        int eventID = getIntent().getIntExtra(eventIDLabel,0);
        // TODO: Actually query database to get corresponding event
        event = new Event();

        // verify if user is already checked-in event: hide/show content in consequence
        // TODO: check if event is checked-in
        if(true){
            checkedInDisplay();
        }
        else{
            notCheckedInDisplay();
        }

        // get all variables from page
        ImageButton backButton = findViewById(R.id.backButton);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView organizerText = findViewById(R.id.organizerText);
        ImageView organizerImage = findViewById(R.id.organizerImage);
        TextView eventLocation = findViewById(R.id.eventLocation);
        ImageView eventImage = findViewById(R.id.eventImage);
        TextView eventDescription = findViewById(R.id.eventDescription);

        // update event info
        eventTitle.setText(event.getName());
        //organizerText.setText(); // TODO: update organizer (not implemented in event yet)
        // TODO: update organizer image
        //eventLocation.setText(); // TODO: format LocalDateTime with current API lvl
        // TODO: update event image
        eventDescription.setText(event.getDetails());

        // connect back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendeeEventInfo.this, AttendeeActivity.class));
            }
        });

    }


}
