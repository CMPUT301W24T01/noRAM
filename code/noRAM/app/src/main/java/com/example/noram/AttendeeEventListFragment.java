package com.example.noram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.noram.model.DataManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.noram.model.Event;

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
    private CollectionReference eventRef; // list of events in database
    private ListView eventList;
    private ArrayList<Event> eventDataList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendee_event_list, container, false);

        // get database
        eventRef = ((DataManager) AttendeeEventListFragment.this.getApplication()).getdatabase().getEventRef();

        // get all views
        Button myEventsButton = rootView.findViewById(R.id.myEventsButton);
        Button allEventsButton = rootView.findViewById(R.id.allEventsButton);
        EditText searchInput = rootView.findViewById(R.id.searchInput);
        eventList = rootView.findViewById(R.id.eventsList);
        eventDataList = new ArrayList<Event>();

        // connect events' data to events' list
        c

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
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayEvent();
            }
        });

        // connect database to listView
        eventRef.addSnapShotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot querySnapshots, FirebaseFirestoreException error){
                if(error != null){
                    Log.e("Firestore", error.toString());
                    return;
                }
                if(querySnapshots != null){
                    eventList.clear();
                    for(QueryDocumentSnapshot doc: querySnapshots){
                        int id = Integer.parseInt(doc.getId());
                        // TODO: create and save each book from database to list
                    }
                }
            }
        });

        return rootView;
    }
}