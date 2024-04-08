/*
 * This file displays the list of attendee milestones for an event
 * Outstanding Issues:
 * - None
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventMilestoneArrayAdapter;
import com.example.noram.model.Event;
import com.example.noram.model.Milestone;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

/**
 * The OrganizerEventMilestonesActivity class displays the milestones for an event for the organizer.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Ethan
 * @author Ethan
 */
public class OrganizerEventMilestonesActivity extends AppCompatActivity {
    private KonfettiView confetti;
    private Party party;
    private Event event;
    private ListView milestoneList; // list of all milestones in UI
    private ArrayList<Milestone> milestoneDataList; // data list of all milestones
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

                ArrayList<Milestone> milestoneCounts = event.getMilestoneCounts();
                // Update the list
                milestoneDataList.clear();
                milestoneDataList.addAll(milestoneCounts);
                milestoneAdapter.notifyDataSetChanged();
                findViewById(R.id.organizer_event_milestones_loading).setVisibility(View.GONE);

                // Show empty message if no milestones
                if (milestoneDataList.isEmpty()) {
                    findViewById(R.id.organizer_event_milestones_empty).setVisibility(View.VISIBLE);
                } else {
                    // If a new milestone is reached, show confetti
                    if (event.getUniqueAttendeeCount() > event.getLastMilestone()) {

                        // Get an array of sorted milestones from the milestoneCounts
                        ArrayList<Long> milestones = new ArrayList<>();
                        for (Milestone milestone : milestoneCounts) {
                            milestones.add(Long.valueOf(milestone.getMilestone()));
                        }

                        // Check if a new milestone has been reached
                        int milestoneIndex = milestones.indexOf(event.getLastMilestone());
                        if (milestoneIndex < milestones.size() - 1 && milestones.get(milestoneIndex + 1) <= event.getUniqueAttendeeCount()) {
                            // Go to the largest milestone reached to avoid repeats
                            while (milestoneIndex < milestones.size() - 1 && milestones.get(milestoneIndex + 1) <= event.getUniqueAttendeeCount()) {
                                event.setLastMilestone(milestones.get(milestoneIndex + 1));
                                milestoneIndex++;
                            }
                            event.updateDBEvent();

                            // Show confetti
                            confetti = findViewById(R.id.organizer_event_milestones_confetti);
                            EmitterConfig config = new Emitter(10, TimeUnit.SECONDS).perSecond(50);
                            party = new PartyFactory(config)
                                    .angle(270)
                                    .spread(90)
                                    .setSpeedBetween(1f, 5f)
                                    .sizes(new Size(12, 5f, 0.2f))
                                    .position(0.0, 0.0, 1.0, 0.0)
                                    .build();
                            confetti.start(party);

                            // Show milestone message
                            if (milestones.get(milestoneIndex) == 1) {
                                Toast.makeText(this, "Congratulations! You have reached the " + milestones.get(milestoneIndex) + " attendee milestone!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Congratulations! You have reached the " + milestones.get(milestoneIndex) + " attendees milestone!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                }

            });

        } else {
            throw new RuntimeException("Must pass Event with key \"event\" using intent.putExtra(\"event\", eventIDAsString);");
        }

        // Set up the back button
        findViewById(R.id.organizer_event_milestones_back).setOnClickListener(v -> finish());
    }
}