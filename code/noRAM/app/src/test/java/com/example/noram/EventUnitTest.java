package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockConstruction;

import com.example.noram.model.Event;
import com.example.noram.model.QRCode;
import com.example.noram.model.QRType;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the Event class
 */
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

    /**
     * Test the event constructor with no parameters provided
     */
    @Test
    public void emptyConstructorTest() {
        Event event = new Event();

        assertNull(event.getId());
        assertNull(event.getName());
        assertNull(event.getLocation());
        assertNull(event.getStartTime());
        assertNull(event.getEndTime());
        assertNull(event.getDetails());
        assertNull(event.getMilestones());
        assertNull(event.getCheckInQR());
        assertNull(event.getPromoQR());
    }

    /**
     * Test the event constructor with parameters provided minus the qr code
     */
    @Test
    public void noQRConstructorTest() {
        try(MockedConstruction<QRCode> mockQR =
                mockConstruction(QRCode.class, (mock, context) -> {
                    doNothing().when(mock).updateDBQRCode();
                })) {

            String id = "id";
            String name = "name";
            String location = "location";
            LocalDateTime startTime = LocalDateTime.parse("2021-04-01T12:00:00");
            LocalDateTime endTime = LocalDateTime.parse("2021-04-01T12:00:01");
            String details = "details";
            ArrayList<Integer> milestones = new ArrayList<>(Arrays.asList(1, 2, 3));
            Boolean trackLocation = true;
            Event event = new Event(id, name, location, startTime, endTime, details, milestones, trackLocation);

            assertEquals(event.getId(), id);
            assertEquals(event.getName(), name);
            assertEquals(event.getLocation(), location);
            assertEquals(event.getStartTime(), startTime);
            assertEquals(event.getEndTime(), endTime);
            assertEquals(event.getDetails(), details);
            assertEquals(event.getMilestones(), milestones);
            assertEquals(event.isTrackLocation(), trackLocation);
            assertNotNull(event.getCheckInQR());
            assertNotNull(event.getPromoQR());
        }
    }

    /**
     * Test the event constructor with all parameters provided
     */
    @Test
    public void fullConstructorTest() {
        try(MockedConstruction<QRCode> mockQR =
                mockConstruction(QRCode.class, (mock, context) -> {
                    doNothing().when(mock).updateDBQRCode();
                })) {

            String id = "id";
            String name = "name";
            String location = "location";
            LocalDateTime startTime = LocalDateTime.parse("2021-04-01T12:00:00");
            LocalDateTime endTime = LocalDateTime.parse("2021-04-01T12:00:01");
            String details = "details";
            ArrayList<Integer> milestones = new ArrayList<>(Arrays.asList(1, 2, 3));
            Boolean trackLocation = true;
            QRCode checkInQR = new QRCode("checkIn", "id", QRType.SIGN_IN);
            QRCode promoQR = new QRCode("promo", "id", QRType.PROMOTIONAL);
            List<String> checkedIn = new ArrayList<>(Arrays.asList("a", "b", "c"));
            Event event = new Event(id, name, location, startTime, endTime, details, milestones, checkInQR, promoQR, trackLocation, checkedIn);

            assertEquals(event.getId(), id);
            assertEquals(event.getName(), name);
            assertEquals(event.getLocation(), location);
            assertEquals(event.getStartTime(), startTime);
            assertEquals(event.getEndTime(), endTime);
            assertEquals(event.getDetails(), details);
            assertEquals(event.getMilestones(), milestones);
            assertEquals(event.isTrackLocation(), trackLocation);
            assertEquals(event.getCheckInQR(), checkInQR);
            assertEquals(event.getPromoQR(), promoQR);
            assertEquals(event.getCheckedInAttendees(), checkedIn);
        }
    }
}
