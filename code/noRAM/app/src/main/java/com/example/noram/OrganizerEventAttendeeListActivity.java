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

import com.example.noram.controller.AttendeeArrayAdapter;
import com.example.noram.controller.EventAttendeeArrayAdapter;
import com.example.noram.model.Attendee;
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
    private ListView checkedInAttendeeList; // list of all attendees in UI
    private ListView signedUpAttendeeList;
    private ListView searchCheckedInAttendeeList; // list of attendees' search results
    private ListView searchSignedUpAttendeeList;
    private ArrayList<AttendeeCheckInCounter> checkedInAttendeeDataList; // data list of all attendees and their counts
    private ArrayList<Attendee> signedUpAttendeeDataList;
    private ArrayList<AttendeeCheckInCounter> searchCheckedInAttendeeDataList; // data list of attendees' search results and their counts
    private ArrayList<Attendee> searchSignedUpAttendeeDataList;
    private EditText searchInput; // searchbar
    private EventAttendeeArrayAdapter checkedInAttendeeAdapter; // adapter for attendee list
    private EventAttendeeArrayAdapter searchCheckedInAttendeeAdapter; // adapter for searchAttendee list
    private AttendeeArrayAdapter signedUpAttendeeAdapter;
    private AttendeeArrayAdapter searchSignedUpAttendeeAdapter;
    private enum Showing {SIGNEDUP, CHECKEDIN}
    private Showing showing = Showing.SIGNEDUP;

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

        // Checked in attendee lists
        checkedInAttendeeList = findViewById(R.id.organizer_event_attendee_checked_in_list);
        checkedInAttendeeDataList = new ArrayList<>();
        searchCheckedInAttendeeList = findViewById(R.id.organizer_event_attendee_search_checked_in_list);
        searchCheckedInAttendeeDataList = new ArrayList<>();

        // Signed up attendee lists
        signedUpAttendeeList = findViewById(R.id.organizer_event_attendee_signed_up_list);
        signedUpAttendeeDataList = new ArrayList<>();
        searchSignedUpAttendeeList = findViewById(R.id.organizer_event_attendee_search_signed_up_list);
        searchSignedUpAttendeeDataList = new ArrayList<>();

        // connect checked-in lists to their adapters
        checkedInAttendeeAdapter = new EventAttendeeArrayAdapter(this, checkedInAttendeeDataList);
        searchCheckedInAttendeeAdapter = new EventAttendeeArrayAdapter(this, searchCheckedInAttendeeDataList);
        checkedInAttendeeList.setAdapter(checkedInAttendeeAdapter);
        searchCheckedInAttendeeList.setAdapter(searchCheckedInAttendeeAdapter);

        // connect signed-up lists to their adapters
        signedUpAttendeeAdapter = new AttendeeArrayAdapter(this, signedUpAttendeeDataList);
        searchSignedUpAttendeeAdapter = new AttendeeArrayAdapter(this, searchSignedUpAttendeeDataList);
        signedUpAttendeeList.setAdapter(signedUpAttendeeAdapter);
        searchSignedUpAttendeeList.setAdapter(searchSignedUpAttendeeAdapter);

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

                // Get the checked-in attendees and their check-in counts
                event.getCheckedInAttendeesAndCounts(attendeeCallback -> {
                    checkedInAttendeeDataList.clear();
                    checkedInAttendeeDataList.addAll(attendeeCallback);
                    checkedInAttendeeAdapter.notifyDataSetChanged();
//                    findViewById(R.id.organizer_event_attendee_loading).setVisibility(View.GONE);
//                    if (checkedInAttendeeDataList.isEmpty()) {
//                        findViewById(R.id.organizer_event_attendee_empty).setVisibility(View.VISIBLE);
//                    }
                });

                // Get the signed-up attendees
                event.getSignedUpAttendeeObjects(attendeeCallback -> {
                    signedUpAttendeeDataList.clear();
                    signedUpAttendeeDataList.addAll(attendeeCallback);
                    signedUpAttendeeAdapter.notifyDataSetChanged();
                    findViewById(R.id.organizer_event_attendee_loading).setVisibility(View.GONE);
                    if (signedUpAttendeeDataList.isEmpty()) {
                        findViewById(R.id.organizer_event_milestones_empty).setVisibility(View.VISIBLE);
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

        // Set up signed-up button
        findViewById(R.id.organizer_event_attendee_signed_up_button).setOnClickListener(v -> {
            if (showing == Showing.CHECKEDIN) {
                // Flip showing
                showing = Showing.SIGNEDUP;

                // Hide other lists
                checkedInAttendeeList.setVisibility(View.GONE);
                searchCheckedInAttendeeList.setVisibility(View.GONE);

                // Show correct lists
                signedUpAttendeeList.setVisibility(View.VISIBLE);
                searchSignedUpAttendeeList.setVisibility(View.INVISIBLE);
            }
        });

        // Set up checked-in button
        findViewById(R.id.organizer_event_attendee_signed_up_button).setOnClickListener(v -> {
            if (showing == Showing.CHECKEDIN) {
                // Flip showing
                showing = Showing.CHECKEDIN;

                // Hide other lists
                checkedInAttendeeList.setVisibility(View.GONE);
                searchCheckedInAttendeeList.setVisibility(View.GONE);

                // Show correct lists
                checkedInAttendeeList.setVisibility(View.VISIBLE);
                searchCheckedInAttendeeList.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Show search results on the searchList if the search is non-empty. Otherwise, show all events.
     * @param search The input of the user in the search, used to populate the search list
     */
    public void searchAttendees(String search) {
        if (showing == Showing.CHECKEDIN) {
            // if search is empty, show back all events and return. Otherwise, show searched events
            if (search.isEmpty()) {
                searchCheckedInAttendeeList.setVisibility(View.INVISIBLE);
                checkedInAttendeeList.setVisibility(View.VISIBLE);
                return;
            }

            searchCheckedInAttendeeList.setVisibility(View.VISIBLE);
            checkedInAttendeeList.setVisibility(View.INVISIBLE);

            // remove old search
            searchCheckedInAttendeeDataList.clear();
            // search through attendees' name and check-in count
            for (AttendeeCheckInCounter attendeeAndCount : checkedInAttendeeDataList) {

                // get attendee's info
                String name = (attendeeAndCount.getAttendee().getFirstName() + " " + attendeeAndCount.getAttendee().getLastName()).toLowerCase(Locale.ROOT);
                String checkInCount = Integer.toString(attendeeAndCount.getCheckInCount()).toLowerCase(Locale.ROOT);

                // if event contains search, add it to the search list
                if ((name.contains(search)) || (checkInCount.contains(search))) {
                    // add valid events to result
                    searchCheckedInAttendeeDataList.add(attendeeAndCount);
                }
            }
            searchCheckedInAttendeeAdapter.notifyDataSetChanged();
        }
        else {
            // if search is empty, show back all events and return. Otherwise, show searched events
            if (search.isEmpty()) {
                searchSignedUpAttendeeList.setVisibility(View.INVISIBLE);
                signedUpAttendeeList.setVisibility(View.VISIBLE);
                return;
            }

            searchSignedUpAttendeeList.setVisibility(View.VISIBLE);
            signedUpAttendeeList.setVisibility(View.INVISIBLE);

            // remove old search
            searchSignedUpAttendeeDataList.clear();
            // search through attendees' name and check-in count
            for (Attendee attendee : signedUpAttendeeDataList) {

                // get attendee's info
                String name = (attendee.getFirstName() + " " + attendee.getLastName()).toLowerCase(Locale.ROOT);

                // if event contains search, add it to the search list
                if (name.contains(search)) {
                    // add valid events to result
                    searchSignedUpAttendeeDataList.add(attendee);
                }
            }
            searchSignedUpAttendeeAdapter.notifyDataSetChanged();
        }
    }
}