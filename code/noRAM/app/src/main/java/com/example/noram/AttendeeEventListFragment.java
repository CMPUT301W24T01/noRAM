/*
This file is used to display the list of events for the attendee. It allows the user to see all events, their own events, and search for events.
Outstanding Issues:
- UI needs to be cleaned up
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeEventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @maintainer Gabriel
 * @author Gabriel
 * @author Cole
 */
public class AttendeeEventListFragment extends EventListFragmentTemplate {
    private final CollectionReference eventRef = MainActivity.db.getEventsRef(); // list of events in database
    private ListView allEventList; // list of all events in UI
    private ListView userEventList; // list of all user's events in UI
    private ListView searchEventList; // list of events' search results
    private ArrayList<Event> allEventDataList; // data list of all events
    private ArrayList<Event> userEventDataList; // data list of all user's events

    EventArrayAdapter allEventAdapter; // adapter for allEvent list
    EventArrayAdapter userEventAdapter; // adapter for userEvent list

    /**
     * Required empty public constructor
     */
    public AttendeeEventListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AttendeeEventListFragment.
     */
    public static AttendeeEventListFragment newInstance() {
        AttendeeEventListFragment fragment = new AttendeeEventListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Hook from EventListFragmentTemplate that shows the searchList view on the screen and hide
     * other views that could be blocking the searchlist from being visible
     */
    @Override
    protected void showSearchList(){
        searchEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        userEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Hook from AttendeeEventListFragment that is called when a view in the searchList view is
     * clicked on
     * @param event The event that was clicked on (in the searchList)
     */
    @Override
    protected void searchElementsClick(Event event){
        // display event that has been clicked on
        EventManager.displayEvent(getActivity(),event);
    }

    /**
     * Create the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Makes the user's personal events visible and hides the other lists
     */
    private void displayMyEvents(){
        userEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Makes the list of all events visible and hides the other list
     */
    private void displayAllEvents(){
        allEventList.setVisibility(View.VISIBLE);
        userEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Create the view of the fragment
     * @param inflater The layout inflater associated with the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     * @return The view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_attendee_event_list, container, false);

        // get all views and initialize variables
        Button myEventsButton = rootView.findViewById(R.id.myEventsButton);
        Button allEventsButton = rootView.findViewById(R.id.allEventsButton);
        allEventList = rootView.findViewById(R.id.allEventsList);
        allEventDataList = new ArrayList<>();
        userEventList = rootView.findViewById(R.id.userEventsList);
        userEventDataList = new ArrayList<>();

        // setup search functionality
        searchEventList = rootView.findViewById(R.id.searchEventsList);
        setupSearch(searchEventList, rootView.findViewById(R.id.searchInput));

        // connect list to their adapters
        allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        userEventAdapter = new EventArrayAdapter(this.getContext(), userEventDataList);
        allEventList.setAdapter(allEventAdapter);
        userEventList.setAdapter(userEventAdapter);

        // connect each button to corresponding function
        myEventsButton.setOnClickListener(view -> displayMyEvents());
        allEventsButton.setOnClickListener(view -> displayAllEvents());

        // connect the three lists so that each item display its event
        allEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = allEventDataList.get(position);
            EventManager.displayEvent(getActivity(),event);
        });
        userEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = userEventDataList.get(position);
            EventManager.displayEvent(getActivity(),event);
        });

        eventRef.addSnapshotListener((querySnapshots, error) -> {
            if(error != null){
                Log.e("Firestore", error.toString());
                return;
            }
            if(querySnapshots != null){
                allEventDataList.clear();
                userEventDataList.clear();
                for(QueryDocumentSnapshot doc: querySnapshots){
                    // get event's info and create it
                    Event event = new Event();
                    event.updateWithDocument(doc);
                    allEventDataList.add(event);

                    // if user correspond, add event to myEvents list
                    ArrayList<String> attendees = (ArrayList<String>) doc.get("checkedInAttendees");
                    if(attendees!=null && attendees.contains(MainActivity.attendee.getIdentifier())) {
                        userEventDataList.add(event);
                    }
                }
                // update after both lists are changed
                allEventAdapter.notifyDataSetChanged();
                userEventAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    /**
     * Function to programmatically move to an event's info page
     * @param event event to move to
     */
    public void viewEventPage(Event event) {

        // get the position of the event and programmatically click it.
        int position = allEventDataList.indexOf(event);
        allEventList.performItemClick(
                allEventList.getAdapter().getView(position, null, null),
                position,
                allEventList.getAdapter().getItemId(position));
    }
}