package com.example.noram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerProfileFragment extends Fragment {

    public OrganizerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrganizerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerProfileFragment newInstance() {
        OrganizerProfileFragment fragment = new OrganizerProfileFragment();
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
        return inflater.inflate(R.layout.fragment_organizer_profile, container, false);
    }
}