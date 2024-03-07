/*
This file is used to display the profile of an organizer.
Outstanding Issues:
- None
 */

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
 * @maintainer Cole
 * @author Cole
 */
public class OrganizerProfileFragment extends Fragment {

    /**
     * Default constructor
     */
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

    /**
     * This method is called when the fragment is created.
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is called when the fragment is created.
     * @param inflater the layout inflater
     * @param container the view group
     * @param savedInstanceState the saved instance state
     * @return the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set header and Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer_profile, container, false);
    }
}