package com.example.noram;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


import com.example.noram.model.Attendee;

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
        String profPic = "temp";
        Boolean allowLoc = true;

        // Act
        Attendee testAttendee = new Attendee(identifier, name, last, page, email, profPic, allowLoc);

        // Assert
        assertEquals(identifier, testAttendee.getIdentifier());
        assertEquals(name, testAttendee.getFirstName());
        assertEquals(last, testAttendee.getLastName());
        assertEquals(page, testAttendee.getHomePage());
        assertEquals(email, testAttendee.getEmail());
        assertEquals(allowLoc, testAttendee.getAllowLocation());
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
        assertEquals("", attendee.getProfilePicture());
        assertEquals("", attendee.getEmail());
    }

    /**
     * Tests that every setter method of attendee calls the updateDBAttendee() method.
     */
    @Test
    public void dbUpdateTest() {
        // Arrange
        // setup mocking
        Attendee attendee = mock(Attendee.class);
        final int[] methodCalledCount = {0};
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                methodCalledCount[0] += 1;
                return null;
            }
        }).when(attendee).updateDBAttendee();
        doCallRealMethod().when(attendee).setAllowLocation(any(Boolean.class));
        doCallRealMethod().when(attendee).setFirstName(any(String.class));
        doCallRealMethod().when(attendee).setLastName(any(String.class));
        doCallRealMethod().when(attendee).setEmail(any(String.class));
        doCallRealMethod().when(attendee).setHomePage(any(String.class));
        doCallRealMethod().when(attendee).setIdentifier(any(String.class));
        doCallRealMethod().when(attendee).setProfilePicture(any(String.class));

        // Act
        attendee.setProfilePicture("a");
        attendee.setAllowLocation(true);
        attendee.setIdentifier("a");
        attendee.setFirstName("a");
        attendee.setLastName("a");
        attendee.setHomePage("a");
        attendee.setEmail("a");

        // Assert
        assertEquals(7, methodCalledCount[0]);
    }
}
