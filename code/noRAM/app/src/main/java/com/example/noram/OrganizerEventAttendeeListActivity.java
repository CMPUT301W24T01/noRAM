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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

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
 * @Author Carlin
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
    private Button signedUpButton;
    private Button checkedInButton;
    private TextView checkedInEmpty;
    private TextView signedUpEmpty;
    private TextView searchEmpty;

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
        searchInput = findViewById(R.id.searchInput);

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
        signedUpAttendeeAdapter = new AttendeeArrayAdapter(this, signedUpAttendeeDataList, AttendeeArrayAdapter.Format.IDHIDDEN);
        searchSignedUpAttendeeAdapter = new AttendeeArrayAdapter(this, searchSignedUpAttendeeDataList, AttendeeArrayAdapter.Format.IDHIDDEN);
        signedUpAttendeeList.setAdapter(signedUpAttendeeAdapter);
        searchSignedUpAttendeeList.setAdapter(searchSignedUpAttendeeAdapter);

        // Get buttons and set them to the correct states
        signedUpButton = findViewById(R.id.organizer_event_attendee_signed_up_button);
        checkedInButton = findViewById(R.id.organizer_event_attendee_checked_in_button);
        if (showing == Showing.SIGNEDUP) {
            signedUpButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.selected_button_background));
        } else {
            checkedInButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.selected_button_background));
        }

        // Get "no attendees"/"no results" displays
        checkedInEmpty = findViewById(R.id.organizer_event_checked_in_attendee_empty);
        signedUpEmpty = findViewById(R.id.organizer_event_signed_up_attendee_empty);
        searchEmpty = findViewById(R.id.organizer_event_attendee_search_empty);

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
                    findViewById(R.id.organizer_event_attendee_loading).setVisibility(View.GONE);
                    displayCurrentList();
                });

                // Get the signed-up attendees
                event.getSignedUpAttendeeObjects(attendeeCallback -> {
                    signedUpAttendeeDataList.clear();
                    signedUpAttendeeDataList.addAll(attendeeCallback);
                    signedUpAttendeeAdapter.notifyDataSetChanged();
                    findViewById(R.id.organizer_event_attendee_loading).setVisibility(View.GONE);
                    displayCurrentList();
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

        // Set up the list buttons
        signedUpButton.setOnClickListener(v -> displaySignedUp());
        checkedInButton.setOnClickListener(v -> displayCheckedIn());
    }

    /**
     * Display the signed-up attendees list
     */
    private void displaySignedUp() {
        showing = Showing.SIGNEDUP;

        // Clear empty message and search
        checkedInEmpty.setVisibility(View.GONE);
        searchInput.setText("");

        // Change button backgrounds
        signedUpButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.selected_button_background));
        checkedInButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.button_background));

        // Hide other lists
        checkedInAttendeeList.setVisibility(View.GONE);
        searchCheckedInAttendeeList.setVisibility(View.GONE);

        // Show correct lists
        signedUpAttendeeList.setVisibility(View.VISIBLE);
        searchSignedUpAttendeeList.setVisibility(View.INVISIBLE);

        // Check emptiness
        if (signedUpAttendeeDataList.isEmpty()) {
            signedUpEmpty.setVisibility(View.VISIBLE);
        } else {
            signedUpEmpty.setVisibility(View.GONE);
        }
    }

    /**
     * Display the checked-in attendees list
     */
    private void displayCheckedIn() {
        showing = Showing.CHECKEDIN;

        // Clear empty message and search
        signedUpEmpty.setVisibility(View.GONE);
        searchInput.setText("");

        // Change button backgrounds
        checkedInButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.selected_button_background));
        signedUpButton.setBackground(AppCompatResources.getDrawable(this, R.drawable.button_background));

        // Hide other lists
        signedUpAttendeeList.setVisibility(View.GONE);
        searchSignedUpAttendeeList.setVisibility(View.GONE);

        // Show correct lists
        checkedInAttendeeList.setVisibility(View.VISIBLE);
        searchCheckedInAttendeeList.setVisibility(View.INVISIBLE);

        // Check emptiness
        if (checkedInAttendeeDataList.isEmpty()) {
            checkedInEmpty.setVisibility(View.VISIBLE);
        } else {
            checkedInEmpty.setVisibility(View.GONE);
        }
    }

    /**
     * Display the current list of attendees
     */
    private void displayCurrentList() {
        if (showing == Showing.SIGNEDUP) {
            displaySignedUp();
        } else {
            displayCheckedIn();
        }
    }

    /**
     * Shows the search list and hides the main list
     */
    private void showSearchList() {
        if (showing == Showing.CHECKEDIN) {
            searchCheckedInAttendeeList.setVisibility(View.VISIBLE);
            checkedInAttendeeList.setVisibility(View.INVISIBLE);
            if (searchCheckedInAttendeeDataList.isEmpty() && !checkedInAttendeeDataList.isEmpty()) {
                searchEmpty.setVisibility(View.VISIBLE);
            } else {
                searchEmpty.setVisibility(View.INVISIBLE);
            }
        } else {
            searchSignedUpAttendeeList.setVisibility(View.VISIBLE);
            signedUpAttendeeList.setVisibility(View.INVISIBLE);
            if (searchSignedUpAttendeeDataList.isEmpty() && !signedUpAttendeeDataList.isEmpty()) {
                searchEmpty.setVisibility(View.VISIBLE);
            } else {
                searchEmpty.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Hides the search list and shows the main list
     */
    private void hideSearchList() {
        if (showing == Showing.CHECKEDIN) {
            searchCheckedInAttendeeList.setVisibility(View.INVISIBLE);
            checkedInAttendeeList.setVisibility(View.VISIBLE);
        } else {
            searchSignedUpAttendeeList.setVisibility(View.INVISIBLE);
            signedUpAttendeeList.setVisibility(View.VISIBLE);
        }
        searchEmpty.setVisibility(View.GONE);
    }

    /**
     * Show search results on the searchList if the search is non-empty. Otherwise, show all events.
     * @param search The input of the user in the search, used to populate the search list
     */
    public void searchAttendees(String search) {
        // if search is empty, show back all events and return. Otherwise, show searched events
        if (search.isEmpty()) {
            hideSearchList();
            return;
        }

        if (showing == Showing.CHECKEDIN) {
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

        } else {
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

        // show search list
        showSearchList();
    }
}