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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_info);

        // retrieve corresponding event in database
        int eventID = getIntent().getIntExtra(eventIDLabel,0);
        // TODO: Actually query database to get corresponding event
        event = new Event();

        // get all variables from page
        ImageButton backButton = findViewById(R.id.backButton);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView organizerText = findViewById(R.id.organizerText);
        ImageView organizerImage = findViewById(R.id.organizerImage);
        TextView eventLocation = findViewById(R.id.eventLocation);
        ImageView eventImage = findViewById(R.id.eventImage);
        TextView eventDescription = findViewById(R.id.eventDescription);
        Button signupButton = findViewById(R.id.signupButton);

        // verify if user is already checked-in event: if not, hide some content
        // TODO: check if event is checked-in
        if(true){
            //displayCheckedIn();
        }
        else{
            //hideCheckedIn();
        }

        // update event info
        eventTitle.setText(event.getName());
        //organizerText.setText(); // TODO: update organizer (not implemented in event yet)
        // TODO: update organizer image
        //eventLocation.setText(); // TODO: format LocalDateTime with current API lvl
        // TODO: update event image
        eventDescription.setText(event.getDetails());

        // connect buttons
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendeeEventInfo.this, MainActivity.class));
            }
        });

    }


}
