/* This file contains the Espresso tests for the MainActivity
 * Outstanding Issues
 * - None
 */

package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import android.Manifest;
import android.view.View;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.GrantPermissionRule;

import com.example.noram.model.Attendee;
import com.example.noram.model.Database;
import com.example.noram.model.Organizer;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Espresso tests for the main activity
 * @maintainer Cole
 * @author Cole
 */
public class MainActivityTest {
    public ActivityScenario<MainActivity> scenario;
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    /**
     * Grant notification permission
     */
    @Rule
    public GrantPermissionRule permissionNotificatiosn = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    /**
     * Setup before all unit tests
     * @throws InterruptedException due to sleep
     */
    @Before
    public void setup() throws InterruptedException {
        Intents.init();
        MainActivity.attendee = new Attendee("test");
        MainActivity.organizer = new Organizer();
        scenario = ActivityScenario.launch(MainActivity.class);
        
        // Firebase seems to struggle if we don't give time to init in espresso tests
        Thread.sleep(2000);
    }

    /**
     * Verifies the attendee "browse events" button opens the attendee page
     */
    @Test
    public void attendeeButtonTest() {
        MainActivity.db = new Database();
        MainActivity.db = Mockito.spy(MainActivity.db);
        doNothing().when(MainActivity.db).downloadPhoto(any(String.class), any());
        onView(withId(R.id.bottom_nav_attend_events)).perform(click());

        intended(hasComponent(AttendeeActivity.class.getName()));
    }

    /**
     * Verifies that the organizer button opens the organizer page
     */
    @Test
    public void organizerButtonTest() {
        MainActivity.db = new Database();
        MainActivity.db = Mockito.spy(MainActivity.db);
        doNothing().when(MainActivity.db).downloadPhoto(any(String.class), any());
        onView(withId(R.id.bottom_nav_organize_events)).perform(click());
        intended(hasComponent(OrganizerActivity.class.getName()));

    }

    /**
     * Tests whether the admin button properly navigates to the admin activity.
     */
    @Test
    public void adminButtonTest() {
        // force show the admin button, since it might be hidden.
        onView(withId(R.id.adminButton)).perform(new ViewAction() {
            @Override
            public String getDescription() {
                return "Show view";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(Button.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.setVisibility(View.VISIBLE);
            }
        });
        onView(withId(R.id.adminButton)).perform(click());

        intended(hasComponent(AdminActivity.class.getName()));
    }

    /**
     * Release intents on shutdown
     */
    @After
    public void shutdown() {
        Intents.release();
    }
}
