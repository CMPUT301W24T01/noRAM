package com.example.noram;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertSame;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.noram.model.Attendee;
import com.example.noram.model.Database;
import com.example.noram.model.Organizer;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Espresso Tests for the Organizer Activity
 * @maintainer Cole
 * @author Cole
 * @author Carlin
 * @author Christiaan
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
        MainActivity.organizer = new Organizer("temp", "name", "photo_path");
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
    public void homeButtonTest() throws InterruptedException {
        onView(withId(R.id.organizer_home_button)).perform(click());

        // note we don't test for the main activity here, since in the unit test
        // we didn't start it. instead check that the activity gets destroyed.
        // sleep to allow proper activity update
        Thread.sleep(3000);
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

        onView(withId(R.id.organizer_create_event_complete_button)).check(doesNotExist());
    }

    /**
     * Tests that qr codes are properly generated when an event is created
     */
    @Test
    public void qrCodesGeneratedTest() {
        // Mock away the database so the event doesn't add an event to the db
        MainActivity.db = Mockito.mock(Database.class, RETURNS_DEEP_STUBS);
        when(MainActivity.db.getEventsRef().document().set(any(Object.class))).thenReturn(null);
        when(MainActivity.db.getQrRef().document().set(any(Object.class))).thenReturn(null);
        doNothing().when(MainActivity.db).uploadPhoto(any(Uri.class), any(String.class));

        // zoom through event setup
        onView(withId(R.id.navbar_new_event)).perform(click());
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_name_text))
                .perform(scrollTo()).perform(typeText("event"));
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_location_text))
                .perform(scrollTo()).perform(typeText("location"));
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

        // generate qr code and check that they appear
        onView(withId(R.id.organizer_create_event_complete_button)).perform(click());
        onView(withId(R.id.qr_checkin)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_promo)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the re-use QR code page will not navigate to the next page if you have selected to
     * upload a QR code but have not uploaded one.
     * Tests with both the promo and checkin codes.
     */
    @Test
    public void reuseQRCodesNotUploadedTest() {
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

        // verify we navigated - now we are on the QR options page
        intended(hasComponent(AddEventQROptionsActivity.class.getName()));

        onView(withId(R.id.upload_button_promo)).perform(scrollTo()).perform(click());
        onView(withId(R.id.organizer_create_event_complete_button)).perform(scrollTo()).perform(click());
        // verify we DON'T navigate
        onView(withId(R.id.qr_promo)).check(doesNotExist());

        // do the same for checkin
        onView(withId(R.id.autogenerate_button_promo)).perform(scrollTo()).perform(click());
        onView(withId(R.id.upload_button_checkin)).perform(scrollTo()).perform(click());
        onView(withId(R.id.organizer_create_event_complete_button)).perform(scrollTo()).perform(click());
        // verify we DON'T navigate
        onView(withId(R.id.qr_checkin)).check(doesNotExist());
    }

    /**
     * Tests that the share button opens a share intent
     */
    @Test
    public void shareTest() {
        // Mock away the database so the event doesn't add an event to the db
        MainActivity.db = Mockito.mock(Database.class, RETURNS_DEEP_STUBS);
        when(MainActivity.db.getEventsRef().document().set(any(Object.class))).thenReturn(null);
        when(MainActivity.db.getQrRef().document().set(any(Object.class))).thenReturn(null);
        doNothing().when(MainActivity.db).uploadPhoto(any(Uri.class), any(String.class));

        // zoom through event setup
        onView(withId(R.id.navbar_new_event)).perform(click());
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_name_text))
                .perform(scrollTo()).perform(typeText("event"));
        onView(withId(R.id.organizer_fragment_create_event_p1_edit_location_text))
                .perform(scrollTo()).perform(typeText("location"));
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
        onView(withId(R.id.organizer_create_event_complete_button)).perform(click());

        onView(withId(R.id.share_checkin)).perform(scrollTo()).perform(click());

        // check if a share/chooser
        Matcher<Intent> matcher = allOf(
                hasAction(Intent.ACTION_CHOOSER)
        );
        intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(0, null));
        intended(matcher);
    }

    /**
     * Tests updating display name within dashboard
     */
    @Test
    public void editDisplayNameConfirmTest() {

        // Update display name in profile
        onView(withId(R.id.navbar_organizer_profile)).perform(click());
        onView(withId(R.id.edit_organizer_display_name))
                .perform(scrollTo())
                .perform(clearText())
                .perform(typeText("newName"));

        // Confirm change
        onView(withId(R.id.organizer_info_save_button))
                .perform(scrollTo())
                .perform(click());

        // Navigate away amd come back
        onView(withId(R.id.navbar_new_event)).perform(click());
        onView(withId(R.id.navbar_organizer_profile)).perform(click());

        // Check that text remains
        onView(withId(R.id.edit_organizer_display_name)).check(matches(withText("newName")));
    }

    /**
     * Tests updating display name within dashboard
     */
    @Test
    public void editDisplayNameCancelTest() {

        // Update display name in profile
        onView(withId(R.id.navbar_organizer_profile)).perform(click());
        onView(withId(R.id.edit_organizer_display_name))
                .perform(scrollTo())
                .perform(clearText())
                .perform(typeText("newName"));

        // Cancel change
        onView(withId(R.id.organizer_info_cancel_button))
                .perform(scrollTo())
                .perform(click());

        // Navigate away amd come back
        onView(withId(R.id.navbar_new_event)).perform(click());
        onView(withId(R.id.navbar_organizer_profile)).perform(click());

        // Check that text remains
        onView(withId(R.id.edit_organizer_display_name)).check(matches(withText("name")));
    }

    /**
     * Test that the my events button works
     */
    @Test
    public void testAllEventsButton() {
        onView(withId(R.id.navbar_my_events)).perform(click());
        onView(withId(R.id.allEventsButton)).perform(click());
        onView(withId(R.id.allEventsList)).check(matches(isDisplayed()));
    }

    /**
     * Test that the all events button works
     */
    @Test
    public void testPastEventsButton() {
        onView(withId(R.id.navbar_my_events)).perform(click());
        onView(withId(R.id.pastEventsButton)).perform(click());
        onView(withId(R.id.pastEventsList)).check(matches(isDisplayed()));
    }

    /**
     * Test that the search bar works
     */
    @Test
    public void testSearchBar() {
        onView(withId(R.id.navbar_my_events)).perform(click());
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
