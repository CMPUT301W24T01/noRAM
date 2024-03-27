package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.noram.model.Attendee;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the Attendee class
 */
public class AttendeeUnitTest {
    /**
     * Tests that the constructor properly sets fields for an attendee
     */
    @Test
    public void constructorTest() {
        // Arrange
        String identifier = "id";
        String name = "a";
        String last = "b";
        String page = "page.com";
        String email = "a@page.com";
        Boolean profPic = false;
        Boolean allowLoc = true;
        ArrayList<String> checkedInto = new ArrayList<>();
        checkedInto.add("test");

        // Act
        Attendee testAttendee = new Attendee(identifier, name, last, page, email, allowLoc, profPic, checkedInto);

        // Assert
        assertEquals(identifier, testAttendee.getIdentifier());
        assertEquals(name, testAttendee.getFirstName());
        assertEquals(last, testAttendee.getLastName());
        assertEquals(page, testAttendee.getHomePage());
        assertEquals(email, testAttendee.getEmail());
        assertEquals(allowLoc, testAttendee.getAllowLocation());
        assertEquals(profPic, testAttendee.getDefaultProfilePhoto());
        assertEquals(checkedInto, testAttendee.getEventsCheckedInto());
    }

    /**
     * Tests that the id-only constructor properly sets other fields to the defaults
     */
    @Test
    public void constructorDefaultTest() {
        // Arrange
        // Act
        Attendee attendee = new Attendee("id");

        // Assert
        assertEquals(false, attendee.getAllowLocation());
        assertEquals("", attendee.getFirstName());
        assertEquals("", attendee.getLastName());
        assertEquals("", attendee.getHomePage());
        assertEquals(true, attendee.getDefaultProfilePhoto());
        assertEquals("", attendee.getEmail());
        assertTrue(attendee.getEventsCheckedInto().isEmpty());
    }

    /**
     * Tests that every setter method of attendee calls the updateDBAttendee() method.
     */
    @Test
    public void dbUpdateTest() {
        // Arrange
        // setup mocking
        Attendee attendee = mock(Attendee.class);
        doNothing().when(attendee).updateDBAttendee();
        doCallRealMethod().when(attendee).setAllowLocation(any(Boolean.class));
        doCallRealMethod().when(attendee).setFirstName(any(String.class));
        doCallRealMethod().when(attendee).setLastName(any(String.class));
        doCallRealMethod().when(attendee).setEmail(any(String.class));
        doCallRealMethod().when(attendee).setHomePage(any(String.class));
        doCallRealMethod().when(attendee).setIdentifier(any(String.class));
        doCallRealMethod().when(attendee).setDefaultProfilePhoto(any(Boolean.class));
        doCallRealMethod().when(attendee).setEventsCheckedInto(any(ArrayList.class));

        // Act
        attendee.setAllowLocation(true);
        attendee.setIdentifier("a");
        attendee.setFirstName("a");
        attendee.setLastName("a");
        attendee.setHomePage("a");
        attendee.setEmail("a");
        attendee.setDefaultProfilePhoto(false);
        attendee.setEventsCheckedInto(new ArrayList<>());

        // Assert
        verify(attendee, times(8)).updateDBAttendee();
    }

    /**
     * Tests that the getProfilePhotoString method returns the correct string when using default photo
     */
    @Test
    public void getDefaultProfilePhotoStringTest() {
        // Arrange
        Attendee attendee = new Attendee("id");
        // Act
        String photo = attendee.getProfilePhotoString();
        // Assert
        assertEquals("profile_photos/id-default", photo);
    }

    /**
     * Tests that the getProfilePhotoString method returns the correct string when using uploaded photo
     */
    @Test
    public void getProfilePhotoStringTest() {
        // Arrange
        Attendee attendee = mock(Attendee.class);
        doCallRealMethod().when(attendee).getProfilePhotoString();
        doCallRealMethod().when(attendee).setDefaultProfilePhoto(any(Boolean.class));
        doCallRealMethod().when(attendee).setIdentifier(any(String.class));
        doCallRealMethod().when(attendee).getIdentifier();
        doNothing().when(attendee).updateDBAttendee();
        // Act
        attendee.setIdentifier("id");
        attendee.setDefaultProfilePhoto(false);
        String photo = attendee.getProfilePhotoString();
        // Assert
        assertEquals("profile_photos/id-upload", photo);
    }

    @Test
    public void updateWithMapTest() {
        Attendee attendee = new Attendee("id");
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", "a");
        map.put("lastName", "b");
        map.put("homePage", "c");
        map.put("email", "d");
        map.put("allowLocation", true);
        map.put("defaultProfilePhoto", false);
        map.put("eventsCheckedInto", new ArrayList<>(Arrays.asList("test")));
        attendee.updateWithMap(map);

        assertEquals("a", attendee.getFirstName());
        assertEquals("b", attendee.getLastName());
        assertEquals("c", attendee.getHomePage());
        assertEquals("d", attendee.getEmail());
        assertEquals(true, attendee.getAllowLocation());
        assertEquals(false, attendee.getDefaultProfilePhoto());
        assertEquals(new ArrayList<>(Arrays.asList("test")), attendee.getEventsCheckedInto());
    }
}
