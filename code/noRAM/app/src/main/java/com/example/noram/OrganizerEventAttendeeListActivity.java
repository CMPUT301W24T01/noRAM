/*
* This file displays the list of attendees for an event and the number of times they have checked in.
* Outstanding Issues:
* - None
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventAttendeeArrayAdapter;
import com.example.noram.model.AttendeeCheckInCounter;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Locale;
/**
 * The OrganizerEventAttendeeListActivity class displays the list of attendees for an event.
 * A {@link AppCompatActivity} subclass.
 * @Maintainer Ethan
 * @Author Ethan
 */
public class OrganizerEventAttendeeListActivity extends AppCompatActivity {
    private Event event;
    private ListView attendeeList; // list of all attendees in UI
    private ListView searchAttendeeList; // list of attendees' search results
    private ArrayList<AttendeeCheckInCounter> attendeeDataList; // data list of all attendees and their counts
    private ArrayList<AttendeeCheckInCounter> searchAttendeeDataList; // data list of attendees' search results and their counts
    private EditText searchInput; // searchbar
    private EventAttendeeArrayAdapter attendeeAdapter; // adapter for attendee list
    private EventAttendeeArrayAdapter searchAttendeeAdapter; // adapter for searchAttendee list

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *    previously being shut down then this Bundle contains the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_attendee);

        // get all views and initialize variables
        searchInput = findViewById(R.id.organizer_event_attendee_search);
        attendeeList = findViewById(R.id.organizer_event_attendee_list);
        attendeeDataList = new ArrayList<>();
        searchAttendeeList = findViewById(R.id.organizer_event_attendee_search_list);
        searchAttendeeDataList = new ArrayList<>();

        // connect list to their adapters
        attendeeAdapter = new EventAttendeeArrayAdapter(this, attendeeDataList);
        searchAttendeeAdapter = new EventAttendeeArrayAdapter(this, searchAttendeeDataList);
        attendeeList.setAdapter(attendeeAdapter);
        searchAttendeeList.setAdapter(searchAttendeeAdapter);

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
                event.getCheckedInAttendeesAndCounts(attendeeCallback -> {
                    attendeeDataList.clear();
                    attendeeDataList.addAll(attendeeCallback);
                    attendeeAdapter.notifyDataSetChanged();
                    findViewById(R.id.organizer_event_attendee_loading).setVisibility(View.GONE);
                    if (attendeeDataList.isEmpty()) {
                        findViewById(R.id.organizer_event_attendee_empty).setVisibility(View.VISIBLE);
                    }
                });
            });

        } else {
            throw new RuntimeException("Must pass Event with key \"event\" using intent.putExtra(\"event\", eventIDAsString);");
        }

        // connect searchbar to listen for user input
        searchInput.addTextChangedListener(new TextWatcher() {
            /**
             * This method is called to notify you that, somewhere within s, the text has been changed.
             * @param s The text that has been changed
             * @param start The starting index of the changed part in the text
             * @param after The length of the changed part in the s sequence since the start index
             * @param count The length of the new sequence in s
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            /**
             * This method is called to notify you that, somewhere within s, the text has been changed.
             * @param s The text that has been changed
             * @param start The starting index of the changed part in the text
             * @param before The length of the changed part in the s sequence since the start index
             * @param count The length of the new sequence in s
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            /**
             * This method is called to notify you that, somewhere within s, the text has been changed.
             * @param editable The text that has been changed
             */
            @Override
            public void afterTextChanged(Editable editable) {
                searchAttendees(editable.toString().toLowerCase(Locale.ROOT));
            }
        });

        // Set up the back button
        findViewById(R.id.organizer_event_attendee_back).setOnClickListener(v -> finish());
    }

    /**
     * Show search results on the searchList if the search is non-empty. Otherwise, show all events.
     * @param search The input of the user in the search, used to populate the search list
     */
    public void searchAttendees(String search) {
        // if search is empty, show back all events and return. Otherwise, show searched events
        if (search.isEmpty()) {
            searchAttendeeList.setVisibility(View.INVISIBLE);
            attendeeList.setVisibility(View.VISIBLE);
            return;
        }

        searchAttendeeList.setVisibility(View.VISIBLE);
        attendeeList.setVisibility(View.INVISIBLE);

        // remove old search
        searchAttendeeDataList.clear();
        // search through attendees' name and check-in count
        for (AttendeeCheckInCounter attendeeAndCount : attendeeDataList) {

            // get attendee's info
            String name = (attendeeAndCount.getAttendee().getFirstName() + " " + attendeeAndCount.getAttendee().getLastName()).toLowerCase(Locale.ROOT);
            String checkInCount = Integer.toString(attendeeAndCount.getCheckInCount()).toLowerCase(Locale.ROOT);

            // if event contains search, add it to the search list
            if ((name.contains(search)) || (checkInCount.contains(search))) {
                // add valid events to result
                searchAttendeeDataList.add(attendeeAndCount);
            }
        }
        searchAttendeeAdapter.notifyDataSetChanged();
    }
}