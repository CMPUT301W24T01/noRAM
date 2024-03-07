package com.example.noram;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.noram.model.Attendee;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AttendeeActivityTest {
    public ActivityScenario<AttendeeActivity> scenario;
    @Rule
    public GrantPermissionRule permissionCamera = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    /**
     * Setup before all unit tests
     * @throws InterruptedException due to sleep
     */
    @Before
    public void setup() throws InterruptedException {
        Intents.init();
        MainActivity.attendee = new Attendee("temp");
        scenario = ActivityScenario.launch(AttendeeActivity.class);
        // Firebase seems to struggle if we don't give time to init in espresso tests
        Thread.sleep(2000);
    }


    /**
     * Test the events navigation properly navigates to the bottom bar.
     */
    @Test
    public void eventsNavTest() {

    }
}
