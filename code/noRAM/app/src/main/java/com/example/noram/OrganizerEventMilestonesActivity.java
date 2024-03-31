/*
 * This file displays the list of attendee milestones for an event
 * Outstanding Issues:
 * - None
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventMilestoneArrayAdapter;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * The OrganizerEventMilestonesActivity class displays the milestones for an event for the organizer.
 * A {@link AppCompatActivity} subclass.
 */
public class OrganizerEventMilestonesActivity extends AppCompatActivity {
    private Event event;
    private ListView milestoneList; // list of all milestones in UI
    private ArrayList<Pair<Integer, Integer>> milestoneDataList; // data list of all milestones
    private EventMilestoneArrayAdapter milestoneAdapter; // adapter for milestone list

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *    previously being shut down then this Bundle contains the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_milestones);

        // get all views and initialize variables
        milestoneList = findViewById(R.id.organizer_event_milestones_list);
        milestoneDataList = new ArrayList<>();
        milestoneAdapter = new EventMilestoneArrayAdapter(this, milestoneDataList);
        milestoneList.setAdapter(milestoneAdapter);

        // get event from intent
        Intent intent = getIntent();
        if (intent.hasExtra("event")) {
            String eventID = intent.getStringExtra("event");
            assert(eventID != null);
            Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventID).get();
            task.addOnSuccessListener(documentSnapshot -> {
                // Get the event details
                event = new Event();
                event.updateWithDocument(documentSnapshot);

                // Get the attendees and their check-in counts
                milestoneDataList.clear();
                milestoneDataList.addAll(event.getMilestoneCounts());
                milestoneAdapter.notifyDataSetChanged();
                findViewById(R.id.organizer_event_milestone_loading).setVisibility(View.GONE);

                if (milestoneDataList.isEmpty()) {
                    findViewById(R.id.organizer_event_milestones_empty).setVisibility(View.VISIBLE);
                }
            });

        } else {
            throw new RuntimeException("Must pass Event with key \"event\" using intent.putExtra(\"event\", eventIDAsString);");
        }

        // Set up the back button
        findViewById(R.id.organizer_event_milestones_back).setOnClickListener(v -> finish());
    }
}