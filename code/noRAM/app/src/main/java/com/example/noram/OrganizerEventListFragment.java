/*
This file is used to display the list of events that a user has created.
Outstanding Issues:
- When the searchbar is cleared, the "AllEvents" list is shown by default (no matter what list was
being consulted)
 */

package com.example.noram;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;
import com.example.noram.model.ListType;
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

    // attributes for all event list
    private ListView allEventList; // list of all events in UI
    private ArrayList<Event> allEventDataList; // data list of all events
    private EventArrayAdapter allEventAdapter; // adapter for allEvent list
    // attributes for past event list
    private ListView pastEventList;
    private ArrayList<Event> pastEventDataList;
    private EventArrayAdapter pastEventAdapter;
    private TextView noResults;
    private TextView noEvents;
    private Button allEventsButton;
    private Button pastEventsButton;

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
        EventManager.displayOrganizerEvent(getContext(), event);
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
        pastEventList.setVisibility(View.INVISIBLE);
        if (searchEventDataList.isEmpty() && eventListRef != null && !eventListRef.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Hook that hides the searchList view on the screen. Override to show the other views that need
     * to be visible
     */
    @Override
    protected void hideSearchList(){
        searchEventList.setVisibility(View.INVISIBLE);
        if (listType == ListType.SPECIFIC) {
            allEventList.setVisibility(View.INVISIBLE);
            pastEventList.setVisibility(View.VISIBLE);
        } else {
            allEventList.setVisibility(View.VISIBLE);
            pastEventList.setVisibility(View.INVISIBLE);
        }
        noResults.setVisibility(View.INVISIBLE);
    }

    /**
     * Makes all-events list visible and hides the other lists
     */
    private void displayAllEvents(){
        // make sure context is not null
        Context context = getContext();
        if (context == null) {
            return;
        }

        // clear search bar and makes searches happen on allEvents list
        setReferenceSearchList(allEventDataList, ListType.GENERAL);
        searchBox.setText("");

        // change button backgrounds
        pastEventsButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));
        allEventsButton.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_button_background));

        // afterward toggle visibility of lists
        allEventList.setVisibility(View.VISIBLE);
        pastEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
        if (allEventDataList.isEmpty()) {
            noEvents.setVisibility(View.VISIBLE);
        } else {
            noEvents.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Makes the list of past events visible and hides the other list
     */
    private void displayPastEvents(){
        // make sure context is not null
        Context context = getContext();
        if (context == null) {
            return;
        }

        // clear search bar and makes searches happen on pastEvent list
        setReferenceSearchList(pastEventDataList, ListType.SPECIFIC);
        searchBox.setText("");

        // change button backgrounds
        pastEventsButton.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_button_background));
        allEventsButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background));

        // afterward toggle visibility of lists
        pastEventList.setVisibility(View.VISIBLE);
        allEventList.setVisibility(View.INVISIBLE);
        searchEventList.setVisibility(View.INVISIBLE);
        if (pastEventDataList.isEmpty()) {
            noEvents.setVisibility(View.VISIBLE);
        } else {
            noEvents.setVisibility(View.INVISIBLE);
        }
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

        // get all views and initialize variables
        allEventsButton = rootView.findViewById(R.id.allEventsButton);
        pastEventsButton = rootView.findViewById(R.id.pastEventsButton);
        noResults = rootView.findViewById(R.id.organizerEventsNoResults);
        noEvents = rootView.findViewById(R.id.organizerEventsNoEvents);
        allEventList = rootView.findViewById(R.id.allEventsList);
        allEventDataList = new ArrayList<>();
        pastEventList = rootView.findViewById(R.id.pastEventsList);
        pastEventDataList = new ArrayList<>();

        // setup the search list

        setupSearch(rootView.findViewById(R.id.searchEventsList), rootView.findViewById(R.id.searchInput));
        // searches are by default on all events
        setReferenceSearchList(allEventDataList, ListType.GENERAL);

        // connect list to their adapters
        allEventAdapter = new EventArrayAdapter(this.getContext(), allEventDataList);
        allEventList.setAdapter(allEventAdapter);
        pastEventAdapter = new EventArrayAdapter(this.getContext(), pastEventDataList);
        pastEventList.setAdapter(pastEventAdapter);

        // connect the two lists so that each item display its event
        allEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = allEventDataList.get(position);
            EventManager.displayOrganizerEvent(getContext(), event);
        });
        pastEventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = pastEventDataList.get(position);
            EventManager.displayOrganizerEvent(getContext(), event);
        });

        // connect each button to corresponding function
        allEventsButton.setOnClickListener(view -> displayAllEvents());
        pastEventsButton.setOnClickListener(view -> displayPastEvents());

        // connect database to lists
        eventRef.whereEqualTo("organizerID", MainActivity.organizer.getIdentifier())
                .addSnapshotListener((querySnapshots, error) -> {

                    // if error, log it and return
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }

                    // if querySnapshots is not null, update the list of events
                    if (querySnapshots != null) {
                        allEventDataList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshots) {
                            // get event's info and create it
                            Event event = new Event();
                            event.updateWithDocument(doc);

                            // either add to all or past event list if event already happened or not
                            if(event.hasHappened()){
                                pastEventDataList.add(event);
                            }
                            else{
                                allEventDataList.add(event);
                            }
                        }

                        // sort events so that "NOW" events are at the top
                        allEventDataList.sort(new EventTimeComparator());
                        pastEventDataList.sort(new EventTimeComparator());

                        // update
                        allEventAdapter.notifyDataSetChanged();
                        pastEventAdapter.notifyDataSetChanged();

                        // Display the correct list
                        if (listType == ListType.SPECIFIC) {
                            displayPastEvents();
                        } else {
                            displayAllEvents();
                        }
                    }
                });

        return rootView;
    }

    /**
     * Resume to correct list type
     */
    @Override
    public void onResume() {
        super.onResume();
        if (listType == ListType.SPECIFIC) {
            displayPastEvents();
        } else {
            displayAllEvents();
        }
    }
}