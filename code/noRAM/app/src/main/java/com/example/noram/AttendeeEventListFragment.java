package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

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
     * Makes the list containing the result of a recent search visible, while hiding the other lists
     */
    public void searchEvents(String search){
        // show search list
        searchEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        userEventList.setVisibility(View.INVISIBLE);

        // remove old search
        searchEventDataList.clear();
        // search through events' details, name and location
        eventRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
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
            }
        });
        /*
        // Unusable: Firebase doesn't provide substring search
        Query query = eventRef.whereEqualTo("details", search)
                .whereEqualTo("name", search)
                .whereEqualTo("location", search);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            // update datalist with results
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for(QueryDocumentSnapshot doc: querySnapshot){
                    Event event = new Event();
                    event.updateWithDocument(doc);
                    searchEventDataList.add(event);
                }
            }
        });
         */
    }

    /**
     * When an event is clicked on, its information is displayed by calling a new activity
     * @param event The event whose information need to be displayed
     */
    public void displayEvent(Event event){
        Intent intent = new Intent(AttendeeEventListFragment.this.getContext(), AttendeeEventInfo.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        intent.putExtras(bundle);
        Log.d("EventList", "ListID is " + event.getId());
        startActivity(intent);
    }

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
        allEventDataList = new ArrayList<Event>();
        userEventList = rootView.findViewById(R.id.userEventsList);
        userEventDataList = new ArrayList<Event>();
        searchEventList = rootView.findViewById(R.id.searchEventsList);
        searchEventDataList = new ArrayList<Event>();

        // connect list to their adapters
        allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        userEventAdapter = new EventArrayAdapter(this.getContext(), userEventDataList);
        searchEventAdapter = new EventArrayAdapter(this.getContext(), searchEventDataList);
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
        searchEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = searchEventDataList.get(position);
                displayEvent(event);
            }
        });


        // TODO: connect database to all-events and user-events data lists (need ref implemented)
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>(){
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
                        Event event = new Event();
                        event.updateWithDocument(doc);
                        allEventDataList.add(event);
                        allEventAdapter.notifyDataSetChanged();
                        // if user correspond, add event to myEvents list
                        // TODO: check in database how to find corresponding user
                        if(false) {
                            userEventDataList.add(event);
                            userEventAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        return rootView;
    }
}