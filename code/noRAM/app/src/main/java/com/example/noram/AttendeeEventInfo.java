package com.example.noram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.noram.model.Event;

public class AttendeeEventInfo extends AppCompatActivity {
    private static final String eventIDLabel = "eventID";
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_attendee_info);

        // retrieve corresponding event in database
        int eventID = getIntent().getIntExtra(eventIDLabel,0);
    }


}
