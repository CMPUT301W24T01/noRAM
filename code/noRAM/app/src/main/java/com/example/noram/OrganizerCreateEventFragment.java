package com.example.noram;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerCreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerCreateEventFragment extends Fragment {

    public OrganizerCreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrganizerCreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerCreateEventFragment newInstance() {
        OrganizerCreateEventFragment fragment = new OrganizerCreateEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_organizer_create_event, container, false);
        // Temp stuff for add event
        Button temp = root.findViewById(R.id.temp_button_for_add_event);
        temp.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddEventActivity.class))
        );

        return root;
    }
}