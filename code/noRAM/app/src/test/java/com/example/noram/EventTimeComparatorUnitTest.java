/*
This file is used to test the EventTimeComparator class.
Outstanding Issues:
- None
 */

package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.noram.model.Event;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Unit tests for the EventTimeComparator
 * @maintainer Christiaan
 * @author Christiaan
 */
public class EventTimeComparatorUnitTest {

    public static Stream<Arguments> objectProvideParameter() {
        Event event1 = new Event();
        Event event2 = new Event();

        event1.setId("1");
        event2.setId("2");

        event1.setStartTime(LocalDateTime.now());
        event1.setEndTime(LocalDateTime.now().plusHours(1));
        event2.setStartTime(LocalDateTime.now().plusHours(2));
        event2.setEndTime(LocalDateTime.now().plusHours(3));


        return Stream.of(
                Arguments.of(event1, event2, -1),
                Arguments.of(event2, event1, 1),
                Arguments.of(event1, event1, 0)
        );
    }

    /**
     * Tests that the EventTimeComparator properly compares two events
     * @param event1 first event being compared
     * @param event2 second event being compared
     * @param expectedResult expected comparison result
     */
    @ParameterizedTest
    @MethodSource("objectProvideParameter")
    public void compare(Event event1, Event event2, int expectedResult) {
        int result = new EventTimeComparator().compare(event1, event2);
        assertEquals(expectedResult, result);
    }
}
