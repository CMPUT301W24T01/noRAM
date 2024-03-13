/*
This file is used to display the admin interface. This activity is used to display the admin home page and to navigate to other admin sections.
Outstanding Issues:
- None
 */

package com.example.noram;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.noram.AdminHomeFragment;

/**
 * The AdminActivity class is the activity that is used to display the admin
 * interface. This activity is used to display the admin home page and to
 * navigate to other admin sections.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Gabriel
 * @author Gabriel
 * @author Cole
 */
public class AdminActivity extends AppCompatActivity {
    
    public final Fragment homeFragment = AdminHomeFragment.newInstance();
    public final Fragment eventsFragment = AdminEventsFragment.newInstance();
    public final Fragment imagesFragment = AdminImagesFragment.newInstance();
    public final Fragment profilesFragment = AdminProfilesFragment.newInstance();
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    // values for fragments to use
    public static final int homePage = 0;
    public static final int eventsPage = 1;
    public static final int imagesPage = 2;
    public static final int profilesPage = 3;

    private Fragment activeFragment;  // fragment being displayed
    ImageButton backButton; // button to go back to admin's main menu
    ImageButton homeButton; // button to go back to app's main menu

    /**
     * Change the page displayed on the Admin section
     * @param pageNum The fragment that is now being displayed
     */
    public void displayFragment(int pageNum){
        // get corresponding fragment
        Fragment newFragment;
        if(pageNum == 0){
            newFragment = homeFragment;
        } else if(pageNum == 1){
            newFragment = eventsFragment;
        } else if(pageNum == 2){
            newFragment = imagesFragment;
        } else if(pageNum == 3){
            newFragment = profilesFragment;
        }else{
            throw new IllegalArgumentException("pageNum must be between 0 and 3");
        }

        // display new fragment
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(newFragment)
                .commitNow();
        activeFragment = newFragment;

        // swap button depending if are going back to home menu or not
        if(newFragment == homeFragment){
            backButton.setVisibility(View.INVISIBLE);
            homeButton.setVisibility(View.VISIBLE);
        }
        else{
            backButton.setVisibility(View.VISIBLE);
            homeButton.setVisibility(View.INVISIBLE);
        }
    }

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
        backButton = findViewById(R.id.admin_back_button);
        homeButton = findViewById(R.id.admin_home_button);

        // 1st visible fragment is admin menu
        // hide back button for now, while keeping home button displayed
        activeFragment = homeFragment;
        backButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.VISIBLE);

        // create all fragments
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "home")
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, eventsFragment, "events")
                .hide(eventsFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, profilesFragment, "profiles")
                .hide(profilesFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, imagesFragment, "images")
                .hide(imagesFragment)
                .commit();

        // connect buttons
        homeButton.setOnClickListener(v -> finish());
        backButton.setOnClickListener(v -> displayFragment(homePage));

    }
}