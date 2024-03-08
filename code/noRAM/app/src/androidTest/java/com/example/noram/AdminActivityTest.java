package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
}
