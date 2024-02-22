package com.example.noram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.QuerySnapshot;

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
    private

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
        eventRef = ((DataManager) getApplication()).getdatabase().getEventRef();

        // get all views
        Button myEventsButton = rootView.findViewById(R.id.myEventsButton);
        Button allEventsButton = rootView.findViewById(R.id.allEventsButton);
        EditText searchInput = rootView.findViewById(R.id.searchInput);
        ListView eventsList = rootView.findViewById(R.id.eventsList);

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
        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                }
            }
        });

        return rootView;
    }
}