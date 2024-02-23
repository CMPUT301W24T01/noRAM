package com.example.noram;

<<<<<<< HEAD
import android.content.Intent;
=======
>>>>>>> origin/master
import android.os.Bundle;

import androidx.fragment.app.Fragment;

<<<<<<< HEAD
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.DataManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.noram.model.Event;

import java.util.ArrayList;
=======
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
>>>>>>> origin/master

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeEventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeEventListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CollectionReference eventRef; // list of events in database
    private ListView allEventList; // list of all events in UI
    private ListView userEventList; // list of all user's events in UI
    private ListView searchEventList; // list of events' search results
    private ArrayList<Event> allEventDataList; // data list of all events
    private ArrayList<Event> userEventDataList; // data list of all user's events
    private ArrayList<Event> searchEventDataList; // data list of events' search results
    private EditText searchInput; // searchbar
    private boolean viewingUserEvents; // indicates if showing all events or just user' events

    public AttendeeEventListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AttendeeEventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendeeEventListFragment newInstance() {
        AttendeeEventListFragment fragment = new AttendeeEventListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // toggle user's events as visible
    public void displayMyEvents(){
        userEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
    }

    // toggle all events as visible
    public void displayAllEvents(){
        allEventList.setVisibility(View.VISIBLE);
        userEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
    }

    // toggle all events that correspond to recent search
    public void searchEvents(){
        // show search list
        searchEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        userEventList.setVisibility(View.INVISIBLE);

        // update search list
        searchEventDataList.clear();
        // TODO: query the database to get matching events and insert them in searchEventDataList
        // Note: use viewingUserEvents to further narrow the query (if we just want user events)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_attendee_event_list, container, false);

        // get database
        eventRef = ((DataManager) AttendeeEventListFragment.this.getActivity().getApplication())
                .getdatabase().getEventRef();

        // get all views and initialize variables
        Button myEventsButton = rootView.findViewById(R.id.myEventsButton);
        Button allEventsButton = rootView.findViewById(R.id.allEventsButton);
        ImageButton houseButton = rootView.findViewById(R.id.houseButton);
        searchInput = rootView.findViewById(R.id.searchInput);
        allEventList = rootView.findViewById(R.id.allEventsList);
        allEventDataList = new ArrayList<Event>();
        userEventList = rootView.findViewById(R.id.userEventsList);
        userEventDataList = new ArrayList<Event>();
        searchEventList = rootView.findViewById(R.id.searchEventsList);
        searchEventDataList = new ArrayList<Event>();

        // connect list to their adapters
        EventArrayAdapter allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        EventArrayAdapter userEventAdapter = new EventArrayAdapter(this.getContext(), userEventDataList);
        EventArrayAdapter searchEventAdapter = new EventArrayAdapter(this.getContext(), searchEventDataList);
        allEventList.setAdapter(allEventAdapter);
        userEventList.setAdapter(userEventAdapter);
        searchEventList.setAdapter(searchEventAdapter);

        // connect each button to corresponding function
        myEventsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                displayMyEvents();
            }
        });
        allEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                displayAllEvents();
            }
        });
        houseButton.setOnClickListener(new View.OnClickListener() {
            // go back to main menu when home button is clicked
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendeeEventListFragment.this.getContext(), MainActivity.class));
            }
        });

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
                searchEvents();
            }
        });

        // connect both list so that each item display its event
        allEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = allEventDataList.get(position);
                displayEvent(event);
            }
        });
        userEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = userEventDataList.get(position);
                displayEvent(event);
            }
        });

        // connect database to both data lists
        eventRef.addSnapShotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot querySnapshots, FirebaseFirestoreException error){
                if(error != null){
                    Log.e("Firestore", error.toString());
                    return;
                }
                if(querySnapshots != null){
                    allEventDataList.clear();
                    userEventDataList.clear();
                    for(QueryDocumentSnapshot doc: querySnapshots){
                        // get event's info and create it
                        Event event = new Event(Integer.parseInt(doc.getId()),
                                doc.getString("Name"),
                                doc.getString("Location"),
                                doc.getString("StartTime"),
                                doc.getString("EndTime"),
                                doc.getString("Details"),
                                doc.getString("Milestones"),
                                Boolean.TRUE.equals(doc.getBoolean("TrackLocation"))
                        );
                        // TODO: Add images to the event (not added to constructor)
                        // TODO: Ensure the database fields that are called are correct
                        // TODO: Convert non-strings to correct format
                        // add event to all events list
                        allEventDataList.add(event);
                        // if user correspond, add event to myEvents list
                        // TODO: check in database how to find corresponding user
                        if(true) {
                            userEventDataList.add(event);
                        }
                    }
                }
            }
        });

        return rootView;
    }
}