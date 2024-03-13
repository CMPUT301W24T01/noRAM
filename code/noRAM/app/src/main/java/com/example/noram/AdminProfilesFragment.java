package com.example.noram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Fragment of the Admin page, listing all the profiles of the app's database
 * @author Gabriel
 */
public class AdminProfilesFragment extends Fragment {

    /**
     * Default constructor
     * @return a new instance of this fragment
     */
    public static AdminProfilesFragment newInstance() {
        return new AdminProfilesFragment();
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
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }
}
