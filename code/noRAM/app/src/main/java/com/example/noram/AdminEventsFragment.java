package com.example.noram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Fragment of the Admin page, listing all of the events of the app's database
 * @author Gabriel
 */
public class AdminEventsFragment extends Fragment {

    /**
     * Default constructor
     * @return a new instance of this fragment
     */
    public static AdminEventsFragment newInstance() {
        return new AdminEventsFragment();
    }

    public ListView eventsList;
    ArrayList<Event> eventsDataList;
    EventArrayAdapter eventsAdapter;
    CollectionReference eventRef;

    /**
     * Inflates the layout for this fragment
     * @param inflater the layout inflater
     * @param container the view group
     * @param savedInstanceState the saved instance state
     * @return the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_events, container, false);

        // get basic elements
        eventsList = rootView.findViewById(R.id.eventsList);
        eventsDataList = new ArrayList<>();
        eventsAdapter = new EventArrayAdapter(this.getContext(), eventsDataList);
        eventsList.setAdapter(eventsAdapter);
        eventRef = MainActivity.db.getEventsRef();

        // connect each list element to show the 'delete' popup
        eventsList.setOnItemClickListener((parent, view, position, id) -> {
            // get document
            Event event = eventsDataList.get(position);
            DocumentReference doc = eventRef.document(event.getId());

            // initialize popup
            ConfirmDeleteFragment deleteFragment = new ConfirmDeleteFragment();
            deleteFragment.setDeleteDoc(doc);
            deleteFragment.show(getParentFragmentManager(), "Delete Document");

        });

        // connect database to list
        eventRef.addSnapshotListener((querySnapshots, error) -> {
            if(error != null){
                Log.e("Firestore", error.toString());
                return;
            }
            if(querySnapshots != null){
                eventsDataList.clear();
                for(QueryDocumentSnapshot doc: querySnapshots){
                    // get event's info and create it
                    Event event = new Event();
                    event.updateWithDocument(doc);
                    eventsDataList.add(event);
                }
                eventsAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }
}
