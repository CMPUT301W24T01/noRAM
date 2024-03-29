/*
This file is used to display the list of events that a user has created.
Outstanding Issues:
- Instead of the user's events being displayed, all events are listed
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
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * A {@link Fragment} subclass.
 * A fragment used to display the list of events that a user has created.
 * Users can search through their events list and get more info on a specific event through this
 * fragment.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class OrganizerEventListFragment extends EventListFragmentTemplate {

    private ListView allEventList; // list of all events in UI
    private ArrayList<Event> allEventDataList; // data list of all events
    EventArrayAdapter allEventAdapter; // adapter for allEvent list

    /**
     * Default constructor
     */
    public OrganizerEventListFragment() {}

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

    /**
     * Hook that is called when a view in the searchList view is clicked on
     * @param event The event that was clicked on (in the searchList)
     */
    @Override
    protected  void searchElementsClick(Event event){
        // TODO: display event information with organizer page
    }

    /**
     * Called when the fragment is created. Initializes the fragment's variables and views.
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state,
     *                           this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    /**
     * Hook that shows the searchList view on the screen. Override to hide other views that could be
     * blocking the list from being visible
     */
    @Override
    protected void showSearchList(){
        searchEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Hook that hides the searchList view on the screen. Override to show the other views that need
     * to be visible
     */
    @Override
    protected void hideSearchList(){
        searchEventList.setVisibility(View.INVISIBLE);
        allEventList.setVisibility(View.VISIBLE);
    }

    /**
     * Creates the ArrayList that will contain all of the database's events. This will be used to
     * make static searches (locally)
     * @return An Event arrayList containing all of the events on which the search will be performed
     */
    @Override
    protected ArrayList<Event> generateEventList(){
        ArrayList<Event> entireList = new ArrayList<>();

        // searches will be on events that organizer created
        eventRef.whereEqualTo("organizerID", MainActivity.organizer.getIdentifier())
            .get().addOnSuccessListener(querySnapshot -> {
                for(QueryDocumentSnapshot doc: querySnapshot){
                    Event event = new Event();
                    event.updateWithDocument(doc);
                    entireList.add(event);
                }
            }
        );

        return entireList;
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

        // setup the search list
        setupSearch(
                rootView.findViewById(R.id.searchEventsList), rootView.findViewById(R.id.searchInput)
        );

        // get all views and initialize variables
        allEventList = rootView.findViewById(R.id.allEventsList);
        allEventDataList = new ArrayList<>();

        // connect list to their adapters
        allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        allEventList.setAdapter(allEventAdapter);

        // connect the two lists so that each item display its event
        allEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = allEventDataList.get(position);
            Intent intent = new Intent(getContext(), OrganizerEventInfoActivity.class);
            intent.putExtra("event", event.getId());
            startActivity(intent);
        });

        eventRef.whereEqualTo("organizerID", MainActivity.organizer.getIdentifier())
                .addSnapshotListener((querySnapshots, error) -> {

            // if error, log it and return
            if(error != null){
                Log.e("Firestore", error.toString());
                return;
            }

            // if querySnapshots is not null, update the list of events
            if(querySnapshots != null){
                allEventDataList.clear();
                for(QueryDocumentSnapshot doc: querySnapshots){
                    // get event's info and create it
                    Event event = new Event();
                    event.updateWithDocument(doc);
                    allEventDataList.add(event);
                }
                allEventAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }
}