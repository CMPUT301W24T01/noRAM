package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertSame;

import android.Manifest;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Espresso Tests for the admin activity
 */
public class AdminActivityTest {
    public ActivityScenario<AdminActivity> scenario;
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    /**
     * Setup before all unit tests
     */
    @Before
    public void setup() {
        scenario = ActivityScenario.launch(AdminActivity.class);
    }

    /**
     * Test that the home button properly closes the activity.
     */
    @Test
    public void homeButtonTest() {
        onView(withId(R.id.admin_home_button)).perform(click());

        // note we don't test for the main activity here, since in the unit test
        // we didn't start it. instead check that the activity gets destroyed.
        assertSame(scenario.getState(), Lifecycle.State.DESTROYED);
    }

    /**
     * Test that we transition between the admin fragments correctly
     */
    @Test
    public void fragmentsTransitionTest(){
        onView(withId(R.id.fragment_admin_home)).check(matches(isDisplayed()));
        // click on events & check
        onView(withId(R.id.events_button)).perform(click());
        onView(withId(R.id.fragment_admin_events)).check(matches(isDisplayed()));
        // check for profiles
        onView(withId(R.id.admin_back_button)).perform(click());
        onView(withId(R.id.profiles_button)).perform(click());
        onView(withId(R.id.fragment_admin_profiles)).check(matches(isDisplayed()));
        // check for photos
        onView(withId(R.id.admin_back_button)).perform(click());
        onView(withId(R.id.images_button)).perform(click());
        onView(withId(R.id.fragment_admin_images)).check(matches(isDisplayed()));
    }
}
