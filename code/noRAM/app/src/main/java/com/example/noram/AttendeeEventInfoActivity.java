/*
This file is used to display the information about an event. Depending on event's data, the layout page will change.
Outstanding Issues:
- Not all fields on xml page are filled, needs to get organizer content and event posters
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * An activity displaying the information about an event. Depending on event's data, the layout page
 * will change.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class AttendeeEventInfoActivity extends EventInfoActivityTemplate {
    /**
     * Signup the user to current event in the database and display a message through a new activity
     */
    private void signup(){
        // TODO: update database to add signed-in attendees to event
        // TODO: send to message page: should send to signed-in page instead of checked-in page
        // Check sign-up limit
        if (!event.isLimitedSignUps() || event.getSignUpCount() < event.getSignUpLimit()) {
            // sign-up to the event and display sign-up message
            EventManager.signUpForEvent(event.getId());
            Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
            event.addSignedUpAttendee(MainActivity.attendee.getIdentifier());
            // Update sign ups display
            updateSignUpText();
        }
        else {
            Toast.makeText(this, "Sign-ups are currently full for this event", Toast.LENGTH_SHORT).show();
        }
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

            /**
             * When the user clicks on the announcements button, it will send them to the announcements
             * page of the event
             * @param v the view of the page
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventInfoActivity.this, AttendeeAnnouncementsActivity.class);
                intent.putExtra(EventManager.eventIDLabel, event.getId());
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
        signupButton.setOnClickListener(v -> signup());
    }

    /**
     * Hook from EventInfoActivityTemplate that is called before updating the base page, used to do
     * additional setup before updating the basic information.
     */
    @Override
    protected void preSetup(){
        // We add check-in information if relevant
        // First check that check-in wasn't specified
        boolean specifiedCheckedIn = getIntent().getExtras().getBoolean(EventManager.checkedInLabel);
        if(specifiedCheckedIn){
            checkedInDisplay();
        }
        else{
            // verify if user is already checked-in event: hide/show content in consequence
            List<String> attendees = event.getCheckedInAttendees();
            if(attendees != null && attendees.contains(MainActivity.attendee.getIdentifier())){
                checkedInDisplay();
            }
            else{
                notCheckedInDisplay();
            }
        }
    }

    /**
     * Create the activity
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // retrieve corresponding event in database, then load page
        String eventID = getIntent().getExtras().getString(EventManager.eventIDLabel);

        assert eventID != null;
        initializePage(eventID);
    }

}
