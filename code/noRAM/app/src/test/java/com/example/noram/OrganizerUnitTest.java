/* Unit test class for the Organizer class */
package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.noram.model.Attendee;
import com.example.noram.model.Organizer;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the organizer class
 * @maintainer Cole
 * @author Cole
 */
public class OrganizerUnitTest {

    /**
     * Tests that the constructor properly assigns all attributes
     */
    @Test
    public void constructorTest() {
        String id = "id";
        String photoPath = "myPhoto";
        String name = "John John";
        Organizer organizer = new Organizer(id, name, photoPath);

        assertEquals(organizer.getIdentifier(), id);
        assertEquals(organizer.getName(), name);
        assertEquals(organizer.getPhotoPath(), photoPath);
    }

    /**
     * Tests that syncWithAttendee properly sets all fields to match the attendee.
     */
    @Test
    public void syncWithAttendeeTest() {
        Attendee attendee = new Attendee("id", "John", "John", "myHomePage.ca", "john@john.ca", false, false, null);
        Organizer organizer = new Organizer();

        organizer.syncWithAttendee(attendee);
        assertEquals(organizer.getIdentifier(), attendee.getIdentifier());
        assertEquals(organizer.getPhotoPath(), attendee.getProfilePhotoString());
        assertEquals(organizer.getName(), attendee.getFirstName() + " " + attendee.getLastName());
    }
}
