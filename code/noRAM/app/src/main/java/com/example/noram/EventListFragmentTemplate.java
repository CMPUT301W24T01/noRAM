/*
This file is used to provide a template for displaying list of events, that other fragments can use.
Outstanding Issues:
- Searchbar shows static searches (the list is not updated in real-time)
 */

package com.example.noram;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * A template {@link Fragment} for event lists.
 * Should be used to create fragments that contain lists of events
 * @maintainer Gabriel
 * @author Gabriel
 */
public abstract class EventListFragmentTemplate extends Fragment {
    private ArrayList<Event> searchEventDataList; // data list of events' search results
    EventArrayAdapter searchEventAdapter; // adapter for searchEvent list
    protected final CollectionReference eventRef = MainActivity.db.getEventsRef(); // list of events in database
    protected ArrayList<Event> eventListRef = null; // list of events on which searches are performed
    protected ListView searchEventList; // view showing all the searched events
    protected EditText searchBox;

    /**
     * This method is called when the fragment is resumed.
     * It hides the search ListView by default.
     */
    @Override
    public void onResume(){
        super.onResume();
        hideSearchList();
    }

    /**
     * Changes the reference list (on which searches are performed) for a new list
     * @param newList The new list on which searches will now be performed
     */
    protected void setReferenceSearchList(ArrayList<Event> newList){
        eventListRef = newList;
        Log.d("EVENTLISTREF", newList.toString());
        Log.d("EVENTLISTREF", eventListRef.toString());
    }

    /**
     * Setup the ListView and EditText passed so that any text entered in the EditText will show the
     * ListView with corresponding search results, pulled from the database
     * @param searchList The ListView that will display search results
     * @param searchInput The EditText that will take user input for searches
     */
    protected void setupSearch(ListView searchList, EditText searchInput){
        // searchbox used to input searches
        searchBox = searchInput;

        // basic connections between lists and adapters
        searchEventList = searchList;
        searchEventDataList = new ArrayList<>();
        searchEventAdapter = new EventArrayAdapter(this.getContext(), searchEventDataList);
        searchList.setAdapter(searchEventAdapter);

        // connect each element of the list to a function
        searchList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = searchEventDataList.get(position);
            searchElementsClick(event);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            /**
             * Notify that within s, the count characters beginning at start are about to be
             * replaced by new text with length after
             * @param s Text currently inside EditText
             * @param start Index indicating the beginning of the characters that will be replaced
             * @param count Number of characters being replaced
             * @param after Number of characters that are replacing the old ones
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            /**
             * notify that, within s, the count characters beginning at start have just replaced
             * old text that had length before.
             * @param s Text currently inside EditText
             * @param start Starting index of the characters that were replaced
             * @param before Number of characters that have been replaced
             * @param count Number of new characters that replaced the old ones
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            /**
             * Notify that, somewhere within s, the text has been changed
             * @param editable Text currently inside EditText
             */
            @Override
            public void afterTextChanged(Editable editable) {
                searchEvents(editable.toString());
            }
        });
    }

    /**
     * Hook that shows the searchList view on the screen. Override to hide other views that could be
     * blocking the list from being visible
     */
    protected void showSearchList(){
        searchEventList.setVisibility(View.VISIBLE);
    }

    /**
     * Hook that hides the searchList view on the screen.
     * Override to show the other views that need to be visible
     */
    protected void hideSearchList() {
        searchEventList.setVisibility(View.INVISIBLE);
    }

    /**
     * Hook that is called when a view in the searchList view is clicked on
     * @param event The event that was clicked on (in the searchList)
     */
    protected abstract void searchElementsClick(Event event);

    /**
     * Query the database and makes the list containing the result of a recent search visible,
     * while hiding the other lists
     * @param search The input of the user in the search, used in the database's query
     */
    private void searchEvents(String search){
        // hide search list if query is empty
        if(search.isEmpty()){
            hideSearchList();
            return;
        }

        // check that reference list was initialized
        if(eventListRef == null){
            throw new RuntimeException("eventListRef should have been initialized using " +
                "setReferenceSearchList before doing a search."
            );
        }

        // show search list
        showSearchList();

        // remove old search
        searchEventDataList.clear();

        // search through events' details, name and location
        for(Event event: eventListRef){
            String name = event.getName();
            String details = event.getDetails();
            String location = event.getLocation();

            if((name != null && name.toLowerCase().contains(search.toLowerCase()))||
                    (details != null && details.toLowerCase().contains(search.toLowerCase())) ||
                    (location != null && location.toLowerCase().contains(search.toLowerCase())) )
            {
                // add valid events to result
                searchEventDataList.add(event);
            }
        }

        // notify
        searchEventAdapter.notifyDataSetChanged();
    }
}
