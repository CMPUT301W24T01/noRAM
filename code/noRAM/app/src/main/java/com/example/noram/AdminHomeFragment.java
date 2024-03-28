/*
This file is used to display the Admins main menu screen, from where other subpages can be accessed.
Outstanding Issues:
- None
 */

package com.example.noram;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.noram.model.Admin;

/**
 * Fragment displaying the Admins main menu screen, from where other subpages can be accessed
 * A {@link Fragment} subclass.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class AdminHomeFragment extends Fragment {

    /**
     * Default constructor
     * @return a new instance of this fragment
     */
    public static AdminHomeFragment newInstance() {
        return new AdminHomeFragment();
    }

    /**
     * Inflates the layout for this fragment
     * @param inflater the layout inflater
     * @param container the view group
     * @param savedInstanceState the saved instance state
     * @return the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        AdminActivity parentActivity = (AdminActivity) getActivity();
        if(parentActivity != null){
            // connect buttons
            rootView.findViewById(R.id.events_button).setOnClickListener(
                    v -> parentActivity.displayFragment(AdminActivity.eventsPage)
            );
            rootView.findViewById(R.id.profiles_button).setOnClickListener(
                    v -> parentActivity.displayFragment(AdminActivity.profilesPage)
            );
            rootView.findViewById(R.id.images_button).setOnClickListener(
                    v -> parentActivity.displayFragment(AdminActivity.imagesPage)
            );
        }

        return rootView;
    }
}
