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
import android.widget.TextView;

import androidx.annotation.StringRes;
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
    TextView headerText; // text indicating which fragment is being currently displayed

    /**
     * Change the page displayed on the Admin section
     * @param pageNum The fragment that is now being displayed
     */
    public void displayFragment(int pageNum){
        // get corresponding fragment
        Fragment newFragment;
        int pageTitleID;

        if(pageNum == 0){
            newFragment = homeFragment;
            pageTitleID = R.string.administrator_page_title;
        } else if(pageNum == 1){
            newFragment = eventsFragment;
            pageTitleID = R.string.admin_events_page_title;
        } else if(pageNum == 2){
            newFragment = imagesFragment;
            pageTitleID = R.string.admin_images_page_title;
        } else if(pageNum == 3){
            newFragment = profilesFragment;
            pageTitleID = R.string.admin_users_page_title;
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

        // update header text
        headerText.setText(pageTitleID);
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
        headerText = findViewById(R.id.admin_header_text);

        // Begin in admin menu. Back button is hidden
        activeFragment = homeFragment;
        backButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.VISIBLE);
        headerText.setText(R.string.administrator_page_title);

        // create all fragments
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "home")
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, eventsFragment, "events")
                .hide(eventsFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, profilesFragment, "profiles")
                .hide(profilesFragment)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, imagesFragment, "images")
                .hide(imagesFragment)
                .commit();

        // connect buttons
        homeButton.setOnClickListener(v -> finish());
        backButton.setOnClickListener(v -> displayFragment(homePage));

    }
}