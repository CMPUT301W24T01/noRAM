package com.example.noram;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Event;

public class AttendeeAnnouncementsActivity extends AppCompatActivity {
    private static final String eventIDLabel = "eventID";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_announcements);

        // retrieve corresponding event in database
        int eventID = getIntent().getIntExtra(eventIDLabel,0);
        // TODO: Actually query database to get corresponding event
    }
}
