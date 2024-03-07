package com.example.noram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * Fragment displaying the Admin's main menu screen, from where other subpages can be accessed
 */
public class AdminHomeFragment extends Fragment {
    public static AdminHomeFragment newInstance() {
        return new AdminHomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }
}
