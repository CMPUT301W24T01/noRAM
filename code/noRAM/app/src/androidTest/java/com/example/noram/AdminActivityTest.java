/* This file is a test file for the AdminActivity class. It tests the home button and the fragment transitions.
 * Outstanding Issues
 * - None
 */

package com.example.noram;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertSame;

import android.Manifest;

/**
 * Espresso Tests for the admin activity
 * @maintainer Cole
 * @author Cole
 * @author Gabriel
 */
public class AdminActivityTest {
    public ActivityScenario<AdminActivity> scenario;

    /**
     * Grant camera permission
     */
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
     * @throws InterruptedException if the thread is interrupted
     */
    @Test
    public void homeButtonTest() throws InterruptedException {
        onView(withId(R.id.admin_home_button)).perform(click());

        // note we don't test for the main activity here, since in the unit test
        // we didn't start it. instead check that the activity gets destroyed.
        // sleep to allow proper activity update
        Thread.sleep(3000);
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
