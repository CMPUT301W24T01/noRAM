package com.example.noram;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AdminActivity extends AppCompatActivity {

    // TODO: create fragments for all admin sections
    private final Fragment homeFragment = AdminHomeFragment.newInstance();
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ((ImageButton) findViewById(R.id.home_button)).setOnClickListener(v -> {
            finish();
        });

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "home")
                .commit();
    }
}