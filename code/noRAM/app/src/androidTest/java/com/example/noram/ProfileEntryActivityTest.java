package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertSame;

import android.Manifest;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.noram.model.Attendee;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Espresso Tests for the ProfileEntryActivity
 */
public class ProfileEntryActivityTest {
    public ActivityScenario<ProfileEntryActivity> scenario;
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    /**
     * Setup before all unit tests
     */
    @Before
    public void setup() {
        MainActivity.attendee = new Attendee("temp");
        scenario = ActivityScenario.launch(ProfileEntryActivity.class);
    }

    /**
     * Tests that the continue button properly updates the activity UI
     */
    @Test
    public void continueButtonTest() {
        onView(withId(R.id.edit_attendee_first_name)).perform(scrollTo()).perform(typeText("testname"));
        onView(withId(R.id.edit_attendee_last_name)).perform(scrollTo()).perform(typeText("testname"));
        onView(withId(R.id.edit_attendee_home_page)).perform(scrollTo()).perform(typeText("testpage"));
        onView(withId(R.id.edit_attendee_email)).perform(scrollTo()).perform(typeText("test@test.com"));

        onView(withId(R.id.profile_entry_continue_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.image_view)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_attendee_first_name)).check(matches(not(isDisplayed())));
    }

    /**
     * Verifies that the finish button properly closes the activity.
     */
    @Test
    public void finishButtonTest() {
        onView(withId(R.id.edit_attendee_first_name)).perform(scrollTo()).perform(typeText("testname"));
        onView(withId(R.id.edit_attendee_last_name)).perform(scrollTo()).perform(typeText("testname"));
        onView(withId(R.id.edit_attendee_home_page)).perform(scrollTo()).perform(typeText("testpage"));
        onView(withId(R.id.edit_attendee_email)).perform(scrollTo()).perform(typeText("test@test.com"));
        onView(withId(R.id.profile_entry_continue_button)).perform(scrollTo()).perform(click());
        onView(withId(R.id.profile_entry_finish_button)).perform(scrollTo()).perform(click());

        assertSame(scenario.getState(), Lifecycle.State.DESTROYED);
    }
}
