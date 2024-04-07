/*
This file is used to display the information about an event. Depending on event's data, the layout page will change.
Outstanding Issues:
- Not all fields on xml page are filled, needs to get organizer content and event posters
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventManager;

import java.util.List;

/**
 * An activity displaying the information about an event. Depending on event's data, the layout page
 * will change.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class AttendeeEventInfoActivity extends EventInfoActivityTemplate {

    private Button signupButton; // button that allows user to sign up for event
    private Button unsignButton;
    private ImageView signupImage; // image showing user is signed in
    private TextView signupText; // text showing that user is signed in

    /**
     * Shows all the signed-up features of the page (if it's not checked-in)
     */
    private void displaySignedIn(){
        signupButton.setVisibility(View.GONE);
        unsignButton.setVisibility(View.VISIBLE);
        signupText.setVisibility(View.VISIBLE);
        signupImage.setVisibility(View.VISIBLE);
    }

    /**
     * Hides all the signed-up features of the page (if it's not checked-in)
     */
    private void hideSignedIn(){
        signupButton.setVisibility(View.VISIBLE);
        unsignButton.setVisibility(View.GONE);
        signupText.setVisibility(View.GONE);
        signupImage.setVisibility(View.GONE);
    }

    /**
     * Signup the user to current event in the database and display a message through a new activity
     */
    private void signup(){
        // TODO: update database to add signed-in attendees to event
        // TODO: send to message page: should send to signed-in page instead of checked-in page
        // sign-in the event and display sign-in message
        EventManager.checkInToEvent(event.getId(), null);
        Toast.makeText(this, "Successfully checked in!", Toast.LENGTH_SHORT).show();
        // load new page (signed-in event)
        EventManager.displayCheckedInEvent(this, event);
        // remove old page
        finish();
        // Check sign-up limit
        if (!event.isLimitedSignUps() || event.getSignUpCount() < event.getSignUpLimit()) {
            // sign-up to the event and display sign-up message
            EventManager.signUpForEvent(event.getId(), false);
            Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
            event.addSignedUpAttendee(MainActivity.attendee.getIdentifier());
            // Update sign ups display
            updateSignUpText();
            // display signed-in features
            displaySignedIn();
        }
        else {
            Toast.makeText(this, "Sign-ups are currently full for this event", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unsign the user from the current event in the database
     */
    private void unsign(){
        // update database and page's event
        EventManager.unsignFromEvent(event.getId());
        event.removeSignedUpAttendee(MainActivity.attendee.getIdentifier());
        // update signed-in count and don't show as signed in anymore
        updateSignUpText();
        hideSignedIn();
        // show feedback message
        Toast.makeText(this, "Sign in undone!", Toast.LENGTH_SHORT).show();
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

        // get extra views
        unsignButton = findViewById(R.id.unsignButton);
        signupButton = findViewById(R.id.signupButton);
        signupImage = findViewById(R.id.signedInImage);
        signupText = findViewById(R.id.signedInText);

        // connect signup and unsign button
        signupButton.setOnClickListener(v -> signup());
        unsignButton.setOnClickListener(v -> unsign());

        // show or hide signup features if already signed-in or not
        List<String> attendees = event.getSignedUpAttendees();
        if(attendees != null && attendees.contains(MainActivity.attendee.getIdentifier())){
            displaySignedIn();
        }
        else{
            hideSignedIn();
        }
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
