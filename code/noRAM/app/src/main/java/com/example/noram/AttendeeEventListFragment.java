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

import com.example.noram.AttendeeEventInfo;
import com.example.noram.MainActivity;
import com.example.noram.controller.EventArrayAdapter;
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
public class AttendeeEventListFragment extends Fragment {
    public static final String eventIDLabel = "eventID";
    private CollectionReference eventRef; // list of events in database
    private ListView allEventList; // list of all events in UI
    private ListView userEventList; // list of all user's events in UI
    private ListView searchEventList; // list of events' search results
    private ArrayList<Event> allEventDataList; // data list of all events
    private ArrayList<Event> userEventDataList; // data list of all user's events
    private ArrayList<Event> searchEventDataList; // data list of events' search results
    private EditText searchInput; // searchbar

    EventArrayAdapter allEventAdapter; // adapter for allEvent list
    EventArrayAdapter userEventAdapter; // adapter for userEvent list
    EventArrayAdapter searchEventAdapter; // adapter for searchEvent list

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
    public void displayMyEvents(){
        userEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Makes the list of all events visible and hides the other list
     */
    public void displayAllEvents(){
        allEventList.setVisibility(View.VISIBLE);
        userEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Query the database and makes the list containing the result of a recent search visible,
     * while hiding the other lists
     * @param search The input of the user in the search, used in the database's query
     */
    public void searchEvents(String search){
        // show search list
        searchEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        userEventList.setVisibility(View.INVISIBLE);

        // remove old search
        searchEventDataList.clear();
        // search through events' details, name and location
        eventRef.get().addOnSuccessListener(querySnapshot -> {
            for(QueryDocumentSnapshot doc: querySnapshot){

                String name = doc.getString("name");
                String details = doc.getString("details");
                String location = doc.getString("location");

                if((name != null && name.contains(search))||
                (details != null && details.contains(search)) ||
                (location != null && location.contains(search)) )
                {
                    // add valid events to result
                    Event event = new Event();
                    event.updateWithDocument(doc);
                    searchEventDataList.add(event);
                    searchEventAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * When an event is clicked on, its information is displayed by calling a new activity
     * @param event The event whose information need to be displayed
     */
    public void displayEvent(Event event){
        Intent intent = new Intent(getActivity(), AttendeeEventInfo.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        intent.putExtras(bundle);
        Log.d("EventList", "ListID is " + event.getId());
        startActivity(intent);
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

        // get event collection
        eventRef = MainActivity.db.getEventsRef();

        // get all views and initialize variables
        Button myEventsButton = rootView.findViewById(R.id.myEventsButton);
        Button allEventsButton = rootView.findViewById(R.id.allEventsButton);
        searchInput = rootView.findViewById(R.id.searchInput);
        allEventList = rootView.findViewById(R.id.allEventsList);
        allEventDataList = new ArrayList<>();
        userEventList = rootView.findViewById(R.id.userEventsList);
        userEventDataList = new ArrayList<>();
        searchEventList = rootView.findViewById(R.id.searchEventsList);
        searchEventDataList = new ArrayList<>();

        // connect list to their adapters
        allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        userEventAdapter = new EventArrayAdapter(this.getContext(), userEventDataList);
        searchEventAdapter = new EventArrayAdapter(this.getContext(), searchEventDataList);
        allEventList.setAdapter(allEventAdapter);
        userEventList.setAdapter(userEventAdapter);
        searchEventList.setAdapter(searchEventAdapter);

        // connect each button to corresponding function
        myEventsButton.setOnClickListener(view -> displayMyEvents());
        allEventsButton.setOnClickListener(view -> displayAllEvents());

        // connect searchbar to listen for user input
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchEvents(editable.toString());
            }
        });

        // connect the three lists so that each item display its event
        allEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = allEventDataList.get(position);
            displayEvent(event);
        });
        userEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = userEventDataList.get(position);
            displayEvent(event);
        });
        searchEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = searchEventDataList.get(position);
            displayEvent(event);
        });

        // TODO: connect database to all-events and user-events data lists (need ref implemented)
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
                    allEventAdapter.notifyDataSetChanged();

                    // if user correspond, add event to myEvents list
                    // TODO: check in database how to find corresponding user
                    ArrayList<String> attendees = (ArrayList<String>) doc.get("attendees");

                    if(attendees!=null && attendees.contains(MainActivity.attendee.getIdentifier())) {
                        userEventDataList.add(event);
                        userEventAdapter.notifyDataSetChanged();
                    }
                }
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