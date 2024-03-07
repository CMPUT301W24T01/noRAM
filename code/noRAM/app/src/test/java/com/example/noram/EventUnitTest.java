package com.example.noram;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.noram.model.Event;

import org.junit.Test;

public class EventUnitTest {
    /**
     * Tests the equals method of the event.
     */
    @Test
    public void testEquals() {
        Event event = new Event();
        event.setId("myId");
        Event event2 = new Event();
        event2.setId("notmyId");
        Event event3 = new Event();
        event3.setId("myId");

        assertFalse(event.equals("apple"));
        assertFalse(event.equals(event2));
        assertTrue(event.equals(event3));
    }
}
