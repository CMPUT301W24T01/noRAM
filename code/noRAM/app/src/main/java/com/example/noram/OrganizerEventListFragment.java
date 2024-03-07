package com.example.noram;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A {@link Fragment} subclass.
 * A fragment used to display the list of events that a user has created.
 * Users can search through their events list and get more info on a specific event through this
 * fragment.
 */
public class OrganizerEventListFragment extends Fragment {

    private CollectionReference eventRef; // list of events in database
    private ListView allEventList; // list of all events in UI
    private ListView searchEventList; // list of events' search results
    private ArrayList<Event> allEventDataList; // data list of all events
    private ArrayList<Event> searchEventDataList; // data list of events' search results
    private EditText searchInput; // searchbar

    EventArrayAdapter allEventAdapter; // adapter for allEvent list
    EventArrayAdapter searchEventAdapter; // adapter for searchEvent list


    public OrganizerEventListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrganizerEventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerEventListFragment newInstance() {
        OrganizerEventListFragment fragment = new OrganizerEventListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    /**
     * Show search results on the searchList if the search is non-empty. Otherwise, show all events.
     * @param search The input of the user in the search, used in the database's query
     */
    public void searchEvents(String search) {
        // if search is empty, show back all events and return. Otherwise, show searched events
        if (search.isEmpty()) {
            searchEventList.setVisibility(View.INVISIBLE);
            allEventList.setVisibility(View.VISIBLE);
            return;
        }

        searchEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);

        // remove old search
        searchEventDataList.clear();
        // search through events' details, name and location
        eventRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot doc : querySnapshot) {

                    String name = doc.getString("name");
                    String details = doc.getString("details");
                    String location = doc.getString("location");

                    if ((name != null && name.contains(search)) ||
                            (details != null && details.contains(search)) ||
                            (location != null && location.contains(search))) {
                        // add valid events to result
                        Event event = new Event();
                        event.updateWithDocument(doc);
                        searchEventDataList.add(event);
                        searchEventAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * Creates the view for the Organizer's Events List Fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_organizer_event_list, container, false);

        // get event collection
        eventRef = MainActivity.db.getEventsRef();

        // get all views and initialize variables
        searchInput = rootView.findViewById(R.id.searchInput);
        allEventList = rootView.findViewById(R.id.allEventsList);
        allEventDataList = new ArrayList<Event>();
        searchEventList = rootView.findViewById(R.id.searchEventsList);
        searchEventDataList = new ArrayList<Event>();

        // connect list to their adapters
        allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        searchEventAdapter = new EventArrayAdapter(this.getContext(), searchEventDataList);
        allEventList.setAdapter(allEventAdapter);
        searchEventList.setAdapter(searchEventAdapter);

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

        // connect the two lists so that each item display its event
        allEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = allEventDataList.get(position);
                // TODO: display event information with organizer page

                // Temp: Edit event
                Intent intent = new Intent(getContext(), OrganizerEditEventActivity.class);
                intent.putExtra("event", event.getId());
                startActivity(intent);
            }
        });
        searchEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = searchEventDataList.get(position);
                // TODO: display event information with organizer page
            }
        });


        // TODO: Only show events of the user (instead of all events)
        //eventRef.where("organizer", "==", MainActivity.attendee).addSnapshotListener...
        eventRef.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot querySnapshots, FirebaseFirestoreException error){
                if(error != null){
                    Log.e("Firestore", error.toString());
                    return;
                }
                if(querySnapshots != null){
                    allEventDataList.clear();
                    for(QueryDocumentSnapshot doc: querySnapshots){
                        // get event's info and create it
                        Event event = new Event();
                        event.updateWithDocument(doc);
                        allEventDataList.add(event);
                        allEventAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return rootView;
    }
}