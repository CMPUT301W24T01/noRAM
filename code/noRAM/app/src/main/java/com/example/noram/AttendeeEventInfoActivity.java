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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * An activity displaying the information about an event. Depending on event's data, the layout page
 * will change.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class AttendeeEventInfoActivity extends AppCompatActivity {
    private Event event; // current event being inquired
    private TextView eventTitle; // event's title
    private TextView organizerText; // text indicating event's organizer
    private ImageView organizerImage; // profile picture of event's organizer
    private TextView eventLocation; // event's location
    private ImageView eventImage; // event's image
    private TextView eventDescription; // event's description
    private final CollectionReference eventsRef = MainActivity.db.getEventsRef(); // events in the database
    /**
     * Signup the user to current event in the database and display a message through a new activity
     */
    private void signup(){
        // TODO: update database to add signed-in attendees to event
        // TODO: send to message page: should send to signed-in page instead of checked-in page
        // sign-in the event and display sign-in message
        EventManager.signUpForEvent(event.getId());
        Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
        // load new page (signed-in event)
        EventManager.displayCheckedInEvent(this, event);
        // remove old page
        finish();
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
     * Update page's event ("event") with database's info
     */
    private void baseSetup(){
        // check that check-in wasn't specified
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

        // get all variables from page
        ImageButton backButton = findViewById(R.id.backButton);
        eventTitle = findViewById(R.id.eventTitle);
        organizerText = findViewById(R.id.organizerText);
        organizerImage = findViewById(R.id.organizerImage);
        eventLocation = findViewById(R.id.eventLocation);
        eventImage = findViewById(R.id.eventImage);
        eventDescription = findViewById(R.id.eventDescription);

        // update page's info
        eventTitle.setText(event.getName());
        eventDescription.setText(event.getDetails());
        LocalDateTime startTime = event.getStartTime();
        eventLocation.setText(String.format("%s from %s - %s @ %s",
                startTime.format(DateTimeFormatter.ofPattern("MMMM dd")),
                startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                event.getLocation()
        ));

        //download the event image from db and populate the screen
        eventImage = findViewById(R.id.eventImage);
        String findImage = "event_banners/"+event.getId()+"-upload";
        // set imageview and update organizer image preview
        if (FirebaseStorage.getInstance().getReference().child(findImage) != null) {
            MainActivity.db.downloadPhoto(findImage,
                    t -> runOnUiThread(() -> eventImage.setImageBitmap(t)));
        }
        //Note for when we download organizer photo:
        //remove purple background, and android icon in xml
        //if you want image to format nicely.
        //use android:scaleType="fitCenter"
        //look at xml fpr eventImage

        //Log.d("Uploaded photo", findImage);
        //Log.d("EventInfo", event.getName());
        //Log.d("EventInfo", event.getDetails());
        //Log.d("EventInfo", event.getLocation());

        //organizerText.setText(); // TODO: update organizer (not implemented in event yet)
        // TODO: update organizer image
        //eventLocation.setText(); // TODO: format LocalDateTime with current API lvl
        // TODO: update event image

        // connect back button
        backButton.setOnClickListener(v -> {finish();});
    }

    /**
     * Create the activity
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // retrieve corresponding event in database
        String eventID = getIntent().getExtras().getString(EventManager.eventIDLabel);

        // retrieve event then load page
        assert eventID != null;
        eventsRef.document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            /**
             * Update the page's event with the document's info
             * @param documentSnapshot the document snapshot of the event
             */
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    // update event
                    event = new Event();
                    event.updateWithDocument(documentSnapshot);
                    // update page's info
                    baseSetup();
                }
                else{
                    // doesn't exist
                    Log.e("AttendeeEventInfo", "Couldn't find the event in the database");
                }
            }
        });
    }
}
