/* This file contains the Espresso tests for the AttendeeActivity
 * Outstanding Issues
 * - None
 */

package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertSame;

import android.Manifest;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.GrantPermissionRule;

import com.example.noram.model.Attendee;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Espresso tests for the attendee activity
 * @maintainer Cole
 * @author Cole
 */
public class AttendeeActivityTest {
    public ActivityScenario<AttendeeActivity> scenario;
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    /**
     * Setup before all unit tests
     */
    @Before
    public void setup() {
        Intents.init();
        MainActivity.attendee = new Attendee("temp");
        scenario = ActivityScenario.launch(AttendeeActivity.class);
    }

    /**
     * Test the events navigation properly navigates to the bottom bar.
     */
    @Test
    public void eventsNavTest() {
        onView(withId(R.id.navbar_profile)).perform(click());
        onView(withId(R.id.fragment_attendee_profile)).check(matches(isDisplayed()));

        onView(withId(R.id.navbar_events)).perform(click());
        onView(withId(R.id.fragment_attendee_event_list)).check(matches(isDisplayed()));

        onView(withId(R.id.navbar_scan)).perform(click());
        onView(withId(R.id.fragment_attendee_qr_scan)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the home button closes the activity
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void homeButtonTest() throws InterruptedException {
        onView(withId(R.id.attendee_home_button)).perform(click());

        // note we don't test for the main activity here, since in the unit test
        // we didn't start it. instead check that the activity gets destroyed.
        // sleep to allow proper activity update
        Thread.sleep(3000);
        assertSame(scenario.getState(), Lifecycle.State.DESTROYED);
    }


    /**
     * Tests that editing a profile, saving, navigating away, and navigating back persists the
     * change.
     */
    @Test
    public void editProfileConfirmTest() {
        onView(withId(R.id.navbar_profile)).perform(click());
        onView(withId(R.id.edit_attendee_first_name)).perform(scrollTo()).perform(typeText("newName"));
        onView(withId(R.id.edit_attendee_last_name)).perform(scrollTo()).perform(typeText("newName"));
        onView(withId(R.id.edit_attendee_home_page)).perform(scrollTo()).perform(typeText("newPage"));
        onView(withId(R.id.edit_attendee_email)).perform(scrollTo()).perform(typeText("my@email.ca"));
        onView(withId(R.id.attendee_info_save_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.navbar_events)).perform(click());
        onView(withId(R.id.navbar_profile)).perform(click());

        onView(withId(R.id.edit_attendee_first_name)).check(matches(withText("newName")));
        onView(withId(R.id.edit_attendee_last_name)).check(matches(withText("newName")));
        onView(withId(R.id.edit_attendee_home_page)).check(matches(withText("newPage")));
        onView(withId(R.id.edit_attendee_email)).check(matches(withText("my@email.ca")));

    }

    /**
     * Tests that the edit profile cancel button discards all changes
     */
    @Test
    public void editProfileCancelTest() {
        onView(withId(R.id.navbar_profile)).perform(click());
        onView(withId(R.id.edit_attendee_first_name)).perform(scrollTo()).perform(typeText("newName"));

        onView(withId(R.id.attendee_info_cancel_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.edit_attendee_first_name)).check(matches(not(withText("newName"))));
    }

    /**
     * Test that the all events button works
     */
    @Test
    public void testAllEventsButton() {
        onView(withId(R.id.navbar_events)).perform(click());
        onView(withId(R.id.allEventsButton)).perform(click());
        onView(withId(R.id.allEventsList)).check(matches(isDisplayed()));
    }

    /**
     * Test that the my events button works
     */
    @Test
    public void testMyEventsButton() {
        onView(withId(R.id.navbar_events)).perform(click());
        onView(withId(R.id.myEventsButton)).perform(click());
        onView(withId(R.id.userEventsList)).check(matches(isDisplayed()));
    }

    /**
     * Test that the search bar works
     */
    @Test
    public void testSearchBar() {
        onView(withId(R.id.navbar_events)).perform(click());
        onView(withId(R.id.searchInput)).perform(click());
        onView(withId(R.id.searchInput)).perform(typeText("test"));
        onView(withId(R.id.searchEventsList)).check(matches(isDisplayed()));
    }

    /**
     * Release intents on shutdown
     */
    @After
    public void shutdown() {
        Intents.release();
    }
}
