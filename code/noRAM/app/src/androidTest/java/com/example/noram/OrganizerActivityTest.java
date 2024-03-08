package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertSame;

import android.Manifest;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Espresso Tests for the Organizer Activity
 */
public class OrganizerActivityTest {
    public ActivityScenario<OrganizerActivity> scenario;
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    /**
     * Setup before all unit tests
     */
    @Before
    public void setup() {
        Intents.init();
        scenario = ActivityScenario.launch(OrganizerActivity.class);
    }

    /**
     * Test the events navigation properly navigates to the bottom bar.
     */
    @Test
    public void eventsNavTest() {
        onView(withId(R.id.navbar_organizer_profile)).perform(click());
        onView(withId(R.id.fragment_organizer_profile)).check(matches(isDisplayed()));

        onView(withId(R.id.navbar_new_event)).perform(click());
        onView(withId(R.id.fragment_organizer_create_event)).check(matches(isDisplayed()));

        onView(withId(R.id.navbar_my_events)).perform(click());
        onView(withId(R.id.fragment_organizer_event_list)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the home button closes the activity
     */
    @Test
    public void homeButtonTest() {
        onView(withId(R.id.organizer_home_button)).perform(click());

        // note we don't test for the main activity here, since in the unit test
        // we didn't start it. instead check that the activity gets destroyed.
        assertSame(scenario.getState(), Lifecycle.State.DESTROYED);
    }

    /**
     * Tests that the event create fragment properly loads the next fragment when all required fields
     * are filled in
     */
    @Test
    public void eventFragmentValidFieldsNextTest() {
        onView(withId(R.id.navbar_new_event)).perform(click());
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_name_text))
                .perform(scrollTo()).perform(typeText("event"));
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_location_text))
                .perform(scrollTo()).perform(typeText("location"));

        // date/time pickers need a lot of really funky stuff to validate :)
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_startDateTime_button))
                .perform(scrollTo())
                .perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2024, 6, 10));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(10, 10));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_endDateTime_button))
                .perform(scrollTo())
                .perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2024, 6, 10));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(10, 10));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.organizer_fragment_create_event_p1_edit_next_button))
                .perform(scrollTo()).perform(click());

        intended(hasComponent(AddEventQROptionsActivity.class.getName()));
    }

    /**
     * Verifies that the create event fragment does not go to the next page if fields are not
     * valid.
     */
    @Test
    public void eventFragmentInvalidFieldsTest() {
        onView(withId(R.id.navbar_new_event)).perform(click());
        // don't fill anything in
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_next_button))
                .perform(scrollTo()).perform(click());

        onView(withId(R.id.event_add_p2_gen_QR_button)).check(doesNotExist());
    }

    /**
     * Release intents on shutdown
     */
    @After
    public void shutdown() {
        Intents.release();
    }
}
